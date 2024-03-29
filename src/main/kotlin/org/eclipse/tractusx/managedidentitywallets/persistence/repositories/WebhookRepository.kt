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

package org.eclipse.tractusx.managedidentitywallets.persistence.repositories

import org.eclipse.tractusx.managedidentitywallets.models.NotFoundException
import org.eclipse.tractusx.managedidentitywallets.persistence.entities.Webhook
import org.eclipse.tractusx.managedidentitywallets.persistence.entities.Webhooks
import org.jetbrains.exposed.sql.transactions.transaction

class WebhookRepository {

    fun getAll(): List<Webhook> = transaction { Webhook.all().toList() }

    @Throws(NotFoundException::class)
    fun get(
        threadId: String,
    ): Webhook = Webhook.find { Webhooks.threadId eq threadId }
        .firstOrNull()
        ?: throw NotFoundException("Webhook with threadId $threadId not found")

    fun getOrNull(
        threadId: String,
    ): Webhook?  = Webhook.find { Webhooks.threadId eq threadId }.firstOrNull()

    @Throws(NotFoundException::class)
    fun deleteWebhook(threadId: String): Boolean {
        get(threadId).delete()
        return true
    }

    fun add(
        webhookThreadId: String,
        url: String,
        stateOfRequest: String
    ): Webhook {
        return Webhook.new {
            threadId = webhookThreadId
            webhookUrl = url
            state = stateOfRequest
        }
    }

    @Throws(NotFoundException::class)
    fun updateStateOfWebhook(webhookThreadId: String, stateOfRequest: String) {
        get(webhookThreadId).apply {
            state = stateOfRequest
        }
    }

}
