/********************************************************************************
 * Copyright (c) 2021,2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.managedidentitywallets.services

import kotlinx.coroutines.runBlocking
import org.eclipse.tractusx.managedidentitywallets.models.ssi.acapy.AriesLdFormats
import org.eclipse.tractusx.managedidentitywallets.models.ssi.acapy.Rfc23State
import org.hyperledger.aries.api.connection.ConnectionRecord
import org.hyperledger.aries.api.connection.ConnectionTheirRole
import org.hyperledger.aries.api.issue_credential_v1.CredentialExchangeState
import org.hyperledger.aries.api.issue_credential_v2.V20CredExRecord
import org.hyperledger.aries.webhook.TenantAwareEventHandler
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

/**
 * The ManagedWalletsAriesEventHandler triggers appropriate responses
 * to some of the Aries-Flow events to enable connection and exchange of credentials
 * with the base wallet, external or internal Wallets.
 */
class ManagedWalletsAriesEventHandler(
    private val walletService: IWalletService,
    private val webhookService: IWebhookService,
    private val utilsService: UtilsService
) : TenantAwareEventHandler() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handleConnection(walletId: String, connection: ConnectionRecord) {
        super.handleConnection(walletId, connection)
        val wallet = walletService.getWallet(walletId)
        log.debug(
            "ConnectionRecord ${connection.connectionId} for wallet ${wallet.bpn} " +
                    "is in state ${connection.rfc23State}"
        )
        when (connection.rfc23State) {
            Rfc23State.REQUEST_RECEIVED.toString() -> {
                runBlocking {
                    if (walletService.validateConnectionRequestForManagedWallets(connection)) {
                        walletService.acceptConnectionRequest(walletId, connection)
                    }
                }
            }
            Rfc23State.COMPLETED.toString() -> {
                runBlocking {
                    val theirDid =
                        utilsService.convertToFullDidIfShort(
                            did = connection.theirPublicDid ?: connection.theirDid
                        )
                    transaction {
                        val extractedConnection  = walletService.getConnection(connection.connectionId)
                        if (extractedConnection == null) {
                            walletService.addConnection(
                                connectionId = connection.connectionId,
                                connectionOwnerDid = wallet.did, // connection.myDid is an internal DID of the Wallet
                                connectionTargetDid = theirDid,
                                connectionState = connection.rfc23State
                            )
                        } else {
                            walletService.updateConnectionState(connection.connectionId, connection.rfc23State)
                        }
                    }
                    if (connection.theirRole == ConnectionTheirRole.INVITER && connection.alias == "endorser") {
                        walletService.setAuthorMetaData(walletId, connection.connectionId)
                        walletService.setCommunicationEndpointUsingEndorsement(walletId)
                    }
                }
            }
            Rfc23State.ABANDONED.toString() -> {
                log.error(
                    "Connection with id ${connection.connectionId} for wallet ${wallet.bpn} " +
                            "is in state abandoned"
                )
            }
            else -> { return }
        }
    }

    override fun handleCredentialV2(walletId: String?, v20Credential: V20CredExRecord?) {
        super.handleCredentialV2(walletId, v20Credential)
        if (v20Credential != null) {
            log.debug("CredExRecord ${v20Credential.credentialExchangeId} is in state ${v20Credential.state}")
            when (v20Credential.state) {
                CredentialExchangeState.OFFER_RECEIVED -> {
                    runBlocking {
                        if (v20Credential.credOffer.formats[0].format == AriesLdFormats.ARIES_LD_PROOF_VC_DETAIL_V_1_0) {
                            walletService.acceptReceivedOfferVc(walletId!!, v20Credential)
                        } else {
                            log.warn(
                                "CredExRecord ${v20Credential.credentialExchangeId} has unsupported format " +
                                        v20Credential.credOffer.formats[0].format
                            )
                        }
                    }
                }
                CredentialExchangeState.CREDENTIAL_RECEIVED -> {
                    runBlocking {
                        if (v20Credential.credIssue.formats[0].format == AriesLdFormats.ARIES_LD_PROOF_VC_V_1_0) {
                            walletService.acceptAndStoreReceivedIssuedVc(walletId!!, v20Credential)
                        } else {
                            log.warn(
                                "CredExRecord ${v20Credential.credentialExchangeId} has unsupported format " +
                                        v20Credential.credIssue.formats[0].format
                            )
                        }
                    }
                }
                CredentialExchangeState.CREDENTIAL_ISSUED -> {
                    checkAndUpdateWebhook(v20Credential, notify = false)
                }
                CredentialExchangeState.DONE,
                CredentialExchangeState.ABANDONED -> {
                    checkAndUpdateWebhook(v20Credential, notify = true)
                    if (v20Credential.state == CredentialExchangeState.ABANDONED) {
                        log.error("CredExRecord ${v20Credential.credentialExchangeId} is in state ABANDONED")
                    }
                }
                else -> { return }
            }
        }
    }

    private fun checkAndUpdateWebhook(v20Credential: V20CredExRecord, notify: Boolean) {
        transaction {
            if (webhookService.getWebhookByThreadId(v20Credential.threadId) != null) {
                webhookService.updateStateOfWebhook(v20Credential.threadId, v20Credential.state.name)
                if (notify) {
                    webhookService.sendWebhookCredentialMessage(v20Credential.threadId, v20Credential)
                }
            }
        }
    }
}
