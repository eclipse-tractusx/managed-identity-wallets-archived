# Managed Identity Wallets <a id="introduction"></a>

[![status: archive](https://github.com/GIScience/badges/raw/master/status/archive.svg)](https://github.com/GIScience/badges#archive)

❗This repository is currently not under active maintenance and therefore archived. If you plan on picking up this product again, feel free to reach out ot the Eclipse Tractus-X projects leads via [mailing list](https://accounts.eclipse.org/mailing-list/tractusx-dev)

The Managed Identity Wallets (MIW) service implements the Self-Sovereign-Identity (SSI) readiness by providing a wallet hosting platform including a DID resolver,service endpoints and the company wallets itself.

Technically this project is developed using the [ktor](https://ktor.io) Microservices
framework and thus the Kotlin language. It is using [gradle](https://gradle.org/) as
build system. To store the wallets and communicate with an external ledger MIW is using
[Aries Cloud Agent Python](https://github.com/hyperledger/aries-cloudagent-python) with
it's [multi-tenant feature](https://github.com/hyperledger/aries-cloudagent-python/blob/main/Multitenancy.md)
and [JSON-LD credential](https://github.com/hyperledger/aries-cloudagent-python/blob/main/JsonLdCredentials.md)
To support credential revocation MIW is using the revocation service within the
[GXFS Notarization API/Service](https://gitlab.com/gaia-x/data-infrastructure-federation-services/not/notarization-service/-/tree/main/services/revocation)
> **Warning**
> This is not yet ready for production usage, as
> [Aries Cloud Agent Python](https://github.com/hyperledger/aries-cloudagent-python)
> does not support `did:indy` resolution yet. This disclaimer will be removed,
> once it is available.https://img.shields.io/badge/Version-0.7.7-informational

# Developer Documentation

To run MIW locally, this section describes the tooling as well as
the local development setup.
## Tooling

Following tools the MIW development team used successfully:

| Area        | Tool               | Download Link    | Comment     |
|-------------|--------------------|------------------|-------------|
| IDE         | IntelliJ           | https://www.jetbrains.com/idea/download/ | Additionally the [envfile plugin](https://plugins.jetbrains.com/plugin/7861-envfile) is suggested |
|             | Visual Studio Code | https://code.visualstudio.com/download | Test with version 1.71.2, additionally Git, Kotlin, Kubernetes plugins are suggested |
| Build       | Gradle             | https://gradle.org/install/ | Tested with version 7.3.3 |
| Runtime     | Docker Desktop     | https://www.docker.com/products/docker-desktop/ | |
|             | Rancher Desktop    | https://rancherdesktop.io | Tested with version 1.5.1, and Docker cli version `Docker version 20.10.17-rd, build c2e4e01` and Docker Compose cli version `Docker Compose version v2.6.1` |
| API Testing | Postman            | https://www.postman.com/downloads/ | Tested with version 9.31.0 |
| Database    | DBeaver            | https://dbeaver.io/ | Tested with version 22.2.0.202209051344 |

## Environment Variables <a id= "environmentVariables"></a>

Please see the file `.env.example` for the environment examples that are used
below. Here a few hints on how to set it up:

| Key                       | Type   | Description                                                                                                                                             |
|---------------------------|--------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| `MIW_DB_JDBC_URL`          | URL    | database connection string, most commonly postgreSQL is used                                                                                            |
| `MIW_DB_JDBC_DRIVER`       | URL    | database driver to use, most commonly postgreSQL is used                                                                                                |
| `MIW_AUTH_JWKS_URL`        | URL    | IAM certs url                                                                                                                                           |
| `MIW_AUTH_ISSUER_URL`      | URL    | IAM token issuer url                                                                                                                                    |
| `MIW_AUTH_REDIRECT_URL`    | URL    | IAM redirect url to the MIW                                                                                                                             |
| `MIW_AUTH_REALM`           | String | IAM realm                                                                                                                                               |
| `MIW_AUTH_ROLE_MAPPINGS`   | String | IAM role mapping                                                                                                                                        |
| `MIW_AUTH_RESOURCE_ID`     | String | IAM resource id                                                                                                                                         |
| `MIW_AUTH_CLIENT_ID`       | String | IAM client id                                                                                                                                           |
| `MIW_AUTH_CLIENT_SECRET`   | String | It can be extracted from keycloak under *realms* &gt;*localkeycloak* &gt; *clients* &gt; *ManagedIdentityWallets* &gt; *credentials*                    |
| `APP_VERSION`             | String | application version, this should be in-line with the version in the deployment                                                                          |
| `ACAPY_API_ADMIN_URL`     | String | admin url of ACA-Py                                                                                                                                     |
| `ACAPY_ADMIN_API_KEY`     | String | admin api key of ACA-Py endpoints                                                                                                                       |
| `ACAPY_BASE_WALLET_API_ADMIN_URL`     | String | admin url of the base endorser ACA-Py                                                                                                                   |
| `ACAPY_BASE_WALLET_ADMIN_API_KEY`     | String | admin api key of the base endorser ACA-Py endpoints                                                                                                     |
| `LOG_LEVEL_KTOR_ROOT`     | String | the log level of Ktor                                                                                                                                   |
| `LOG_LEVEL_NETTY`     | String | the log level of used netty server                                                                                                                      |
| `LOG_LEVEL_ECLIPSE_JETTY`     | String | the log level of used eclipse jetty                                                                                                                     |
| `LOG_LEVEL_EXPOSED`     | String | the log level of used exposed framework                                                                                                                 |
| `LOG_LEVEL_SERVICES_CALLS`     | String | the log level of http client used in internal services. OPTIONS: ALL, HEADERS, BODY, INFO, NONE                                                         |
| `WALLET_SERVICE_REQUEST_TIMEOUT`     | String | the timeout of request in http client of the wallet service in milli seconds                                                                            |
| `WALLET_SERVICE_CONNECT_TIMEOUT`     | String | the timeout of connect in http client of the wallet service in milli seconds                                                                            |
| `WALLET_SERVICE_SOCKET_TIMEOUT`     | String | the timeout of socket in http client of the wallet service in milli seconds                                                                             |
| `BPD_SERVICE_REQUEST_TIMEOUT`     | String | the timeout of request in http client of the BPD service in milli seconds                                                                               |
| `BPD_SERVICE_CONNECT_TIMEOUT`     | String | the timeout of connect in http client of the BPD service in milli seconds                                                                               |
| `BPD_SERVICE_SOCKET_TIMEOUT`     | String | the timeout of socket in http client of the BPD service in milli seconds                                                                                |
| `REVOCATION_SERVICE_REQUEST_TIMEOUT`     | String | the timeout of request in http client of the revocation service in milli seconds                                                                        |
| `REVOCATION_SERVICE_CONNECT_TIMEOUT`     | String | the timeout of connect in http client of the revocation service in milli seconds                                                                        |
| `REVOCATION_SERVICE_SOCKET_TIMEOUT`     | String | the timeout of socket in http client of the revocation service in milli seconds                                                                         |
| `WEBHOOK_SERVICE_REQUEST_TIMEOUT`     | String | the timeout of request in http client of the webhook service in milli seconds                                                                        |
| `WEBHOOK_SERVICE_CONNECT_TIMEOUT`     | String | the timeout of connect in http client of the webhook service in milli seconds                                                                        |
| `WEBHOOK_SERVICE_SOCKET_TIMEOUT`     | String | the timeout of socket in http client of the webhook service in milli seconds                                                                            |
| `ACAPY_NETWORK_IDENTIFIER`| String | Hyperledger Indy name space                                                                                                                             |
| `MIW_BPN`                  | String | BPN of the base wallet                                                                                                                                  |
| `MIW_DID`                  | String | DID of the base wallet, this wallet must be registered on ledger with the endorser role                                                                 |
| `MIW_VERKEY`               | String | Verification key of the base wallet, this wallet must be registered on ledger with the endorser role                                                    |
| `MIW_NAME`                 | String | Name of the base wallet                                                                                                                                 |
|`MIW_ALLOWLIST_DIDS`       | String | List of full DIDs seperated by comma ",". Those DIDs are allowed to send a connection request to managed wallets. Empty for public invitation allowance |
| `MIW_MEMBERSHIP_ORG`  | String | The name used in the Membership credential                                                                                                              |
| `BPDM_DATAPOOL_URL`       | String | BPDM data pool API endpoint                                                                                                                             |
| `BPDM_AUTH_CLIENT_ID`     | String | client id for accessing the BPDM data pool endpoint                                                                                                     |
| `BPDM_AUTH_CLIENT_SECRET` | String | client secret for accessing the BPDM data pool endpoint                                                                                                 |
| `BPDM_AUTH_GRANT_TYPE`    | String | grant type for accessing the BPDM data pool endpoint                                                                                                    |
| `BPDM_AUTH_SCOPE`         | String | openid scope for accessing the BPDM data pool endpoint                                                                                                  |
| `BPDM_AUTH_URL`           | String | IAM url to get the access token for BPDM data pool endpoint                                                                                             |
| `BPDM_PULL_DATA_AT_HOUR`  | String | At which hour (24-hour clock) the cron job should pull the data from the BPDM data pool                                                                 |
| `REVOCATION_URL`          | String | URL of the revocation service                                                                                                                           |
| `REVOCATION_CREATE_STATUS_LIST_CREDENTIAL_AT_HOUR` | String | At which hour (24-hour clock) the cron job should issue/update status-list credentials                                                                  |

## Local Development Setup

To get a full development environment up (first with a in-memory database)
run following these steps:

1. Clone the GitHub repository

    ```bash
    git clone https://github.com/eclipse-tractusx/managed-identity-wallets.git
    cd managed-identity-wallets
    ```

1. The setup requires 3 DIDs. The first DID will be the DID of the base wallet. The second DID is used in the management wallet of the multi-tenancy instance. The third DID is optional and needed only when the external/self-managed instance is used. To generate the required DIDs follow the steps in section [Integrate with Indy Ledger](##Integrate-with-Indy-Ledger)

1. Copy over the `.env.example` to `dev.env`

    ```bash
    cp .env.example dev.env
    ```

1. Replace the placeholders in `dev.env` and then run

    ```bash
    set -a; source dev.env; set +a
    ```

    Note that this command needs to be run again after changing any value in `dev.env`.

1. Start the supporting containers for postgreSQL (database), keycloak (identity
management), ACA-Py (ledger communication) and revocation service (credential
revocation handling)

    ```bash
    cd dev-assets/dev-containers
    docker compose up -d
    ```

    You can stop the containers via `docker compose down -v`

1. Run the MIW service from the project rootfolder via (on MacOS)

    ```bash
    cd ../../
    ./gradlew run
    ```

    or respectively run `Application.kt` within in your IDE (using `dev.env` as configuration).

1. :tada: **First milestone reached the MIW service is up and running!**


## Advanced Development Setup

With the following steps you can explore the API 

1. Start Postman and add the environment `Managed_Identity_Wallet_Local.postman_environment` and the collection `Managed_Identity_Wallet.postman_collection` from ./dev-assets/
    1. In the added environment make sure that the client_id and client_secret are the same as in your `dev.env` configuration.

    1. Issue Status-List Credential by sending a POST request to `/api/credentials/revocations/statusListCredentialRefresh`. This step is temporary until the update to Ktor 2.x

1. The two Postman collections `Base_Wallet_Acapy.postman_collection` and `Managed_Wallets_Acapy.postman_collection` are additional for debugging purposes. these collections include direct calls to the admin API of the Base AcaPy instance and the Multi-tenancy AcaPy instance.

1. The Postman collection `Test-Acapy-SelfManagedWallet-Or-ExternalWallet.postman_collection` sends requests to the external AcaPy instance that simulate an external wallet or self managed wallet.

1. :tada: **Second milestone reached: Your own wallet!**

Now you have achieved the following:

* set up the development environment to run it from source
* initialized the base wallet and its revocation list
* you can retrieve the list of wallets in Postman via the *Get wallets from Managed Identity Wallets*

## Setup Summary

| Service               | URL                     | Description |
|-----------------------|-------------------------|-------------|
| postgreSQL database   | port 5432 on `localhost`| within the Docker Compose setup |
| Keycloak              | http://localhost:8081/  | within the Docker Compose setup, username: `admin` and password: `changeme`, client id: `ManagedIdentityWallets` and client secret can be found under the Clients &gt; ManagedIdentityWallets &gt; Credentials |
| revocation service    | http://localhost:8086   | within the Docker Compose setup |
| ACA-Py for Base Endorser Wallet | http://localhost:10000  | within the Docker Compose setup |
| ACA-Py Multi-tenancy for Managed Wallets | http://localhost:10003  | within the Docker Compose setup |
| MIW service           | http://localhost:8080/  | |
| ACA-Py (External Wallet) | http://localhost:10001  | within the Docker Compose setup |

# Administrator Documentation

## Manual Keycloak Configuration

Within the development setup the Keycloak is initially prepared with the
values in `./dev-assets/dev-containers/keycloak`. The realm could also be
manually added and configured at http://localhost:8081 via the "Add realm"
button. It can be for example named `localkeycloak`. Also add an additional client,
e.g. named `ManagedIdentityWallets` with *valid redirect url* set to
`http://localhost:8080/*`. The roles
 * add_wallets
 * view_wallets
 * update_wallets
 * delete_wallets
 * view_wallet
 * update_wallet
can be added under *Clients > ManagedIdentityWallets > Roles* and then
assigned to the client using *Clients > ManagedIdentityWallets > Client Scopes*
*> Service Account Roles > Client Roles > ManagedIdentityWallets*. The
available scopes/roles are:

1. Role `add_wallets` to create a new wallet

1. Role `view_wallets`:
    * to get a list of all wallets
    * to retrieve one wallet by its identifier
    * to validate a Verifiable Presentation
    * to get all stored Verifiable Credentials

1. Role `update_wallets` for the following actions:
    * to store Verifiable Credential
    * to set the wallet DID to public on chain
    * to issue a Verifiable Credential 
    * to issue a Verifiable Presentation
    * to add, update and delete service endpoint of DIDs
    * to trigger the update of Business Partner Data of all existing wallets
  
1. Role `delete_wallets` to remove a wallet

1. Role `view_wallet` requires the BPN of Caller and it can be used:
    * to get the Wallet of the related BPN
    * to get stored Verifiable Credentials of the related BPN
    * to validate any Verifiable Presentation

1. Role `update_wallet` requires the BPN of Caller and it can be used:
    * to issue Verifiable Credentials (The BPN of issuer will be checked)
    * to issue Verifiable Presentations (The BPN of holder will be checked)
    * to store Verifiable Credentials (The BPN of holder will be checked)
    * to trigger Business Partner Data update for its own BPN

Additionally a Token mapper can to be created under *Clients* &gt;
*ManagedIdentityWallets* &gt; *Mappers* &gt; *create* with the following
configuration (using as example `BPNL000000001`):

| Key                 | Value                     |
|---------------------|---------------------------|
| Name                | StaticBPN                 |
| Mapper Type         | Hardcoded claim           |
| Token Claim Name    | BPN                       |
| Claim value         | BPNL000000001             |
| Claim JSON Type     | String                    |
| Add to ID token     | OFF                       |
| Add to access token | ON                        |
| Add to userinfo     | OFF                       |
| includeInAccessTokenResponse.label | ON         | 

If you receive an error message, that the client secret is not valid, please go into
keycloak admin and within *Clients > Credentials* recreate the secret.

## Manual Database Configuration

Within the development setup databases are initially prepared as needed, in the
cloud deployment that is done via init containers. The MIW and the ACA-Py
service are setting up the required tables on the first start. For MIW this is
done within the `src/main/.../managedidentitywallets/plugins/Persistence.kt` database
setup:

```
SchemaUtils.createMissingTablesAndColumns(Wallets, VerifiableCredentials, SchedulerTasks)
```

The tables of the **Revocation Service** are added manually to the database using the
SQL script at `./dev-asset/dev-containers/revocation/V1.0.0__Create_DB.sql`

## Local docker deployment

First step is to create the distribution of the application:

```bash
./gradlew installDist
```

Next step is to build and tag the Docker image, replacing the
`<VERSION>` with the app version:

```
docker build -t managed-identity-wallets:<VERSION> .
```

Finally, start the image (please make sure that there are no quotes around the
values in the env file):

```
docker run --env-file .env.docker -p 8080:8080 managed-identity-wallets:<VERSION>
```

## Deployment on Kubernetes

*Work in progress*

1. Create a namespace

    Using as example `managed-identity-wallets`:

    ```
    kubectl create namespace managed-identity-wallets
    ```

1. Create relevant secrets

    Altogether four secrets are needed
    * managed-identity-wallets-secrets
    * managed-identity-wallets-acapy-secrets    
    * postgres-acapy-secret-config
    * postgres-managed-identity-wallets-secret-config

    Create these with following commands, after replacing the placeholders:

    ```
    kubectl -n managed-identity-wallets create secret generic managed-identity-wallets-secrets \
      --from-literal=miw-db-jdbc-url='jdbc:postgresql://<placeholder>:5432/<database name>?user=<database user>&password=<<database password>>' \
      --from-literal=miw-auth-client-id='ManagedIdentityWallets' \
      --from-literal=miw-auth-client-secret='<placeholder>' \
      --from-literal=bpdm-auth-client-id='<placeholder>' \
      --from-literal=bpdm-auth-client-secret='<placeholder>'

    kubectl -n managed-identity-wallets create secret generic managed-identity-wallets-acapy-secrets \
      --from-literal=acapy-endorser-wallet-key='<placeholder>' \
      --from-literal=acapy-endorser-agent-wallet-seed='<placeholder>' \
      --from-literal=acapy-endorser-jwt-secret='<placeholder>' \
      --from-literal=acapy-endorser-db-account='postgres' \
      --from-literal=acapy-endorser-db-password='<placeholder>' \
      --from-literal=acapy-endorser-db-admin='postgres' \
      --from-literal=acapy-endorser-db-admin-password='<placeholder>' \
      --from-literal=acapy-endorser-admin-api-key='<placeholder>' \
      --from-literal=acapy-mt-wallet-key='<placeholder>' \
      --from-literal=acapy-mt-agent-wallet-seed='<placeholder>' \
      --from-literal=acapy-mt-jwt-secret='<placeholder>' \
      --from-literal=acapy-mt-db-account='postgres' \
      --from-literal=acapy-mt-db-password='<placeholder>' \
      --from-literal=acapy-mt-db-admin='postgres' \
      --from-literal=acapy-mt-db-admin-password='<placeholder>' \
      --from-literal=acapy-mt-admin-api-key='<placeholder>'

    kubectl -n managed-identity-wallets create secret generic postgres-acapy-secret-config \
    --from-literal=password='<placeholder>' \
    --from-literal=postgres-password='<placeholder>' \
    --from-literal=user='postgres'

    kubectl -n managed-identity-wallets create secret generic postgres-managed-identity-wallets-secret-config \
    --from-literal=password='<placeholder>' \
    --from-literal=postgres-password='<placeholder>' \
    --from-literal=user='postgres'
    ```

1.  If the Indy ledger is write-restricted, the DID of the used seed
    must be registered manually before starting ACA-Py.

1. Install a new deployment via helm

    Run following command to use the base values as well as the predefined values for local deployment:

    ```
    helm install managed-identity-wallets ./charts/managed-identity-wallets/ -n managed-identity-wallets -f ./charts/managed-identity-wallets/values.yaml -f ./charts/managed-identity-wallets/values-local.yaml
    ```

4. Expose via loadbalancer

    ```
    kubectl -n managed-identity-wallets apply -f dev-assets/kube-local-lb.yaml
    ```

5. To check the current deployment and version run `helm list -n <namespace-placeholder>`. Example output:

    ```
    NAME         	NAMESPACE        	REVISION	UPDATED                                	STATUS  	CHART                  	                APP VERSION
    miw       	ingress-miw     	1       	2022-02-24 08:51:39.864930557 +0000 UTC	deployed	managed-identity-wallets-0.1.0	0.0.5      
    ```

# End Users

See OpenAPI documentation, which is automatically created from
the source and available on each deployment at the `/docs` endpoint
(e.g. locally at http://localhost:8080/docs). An export of the JSON
document can be also found in [docs/openapi_v310.json](docs/openapi_v310.json).

# Further Guides

In this section there are advanced cases (e.g. building your own ACA-Py image)
described.

## Preparation of ACA-Py Docker Image <a id= "acapyDockerImage"></a>

ACA-Py can be used via the official image at `bcgovimages/aries-cloudagent:py36-1.16-1_0.7.5`
or build your own image following the steps:
* clone the repository `git clone https://github.com/hyperledger/aries-cloudagent-python.git`
* navigate to the repository `cd aries-cloudagent-python`
* currently tested with version `0.7.5`
* run `git checkout 0.7.5`
* run `docker build -t acapy:0.7.5 -f ./docker/Dockerfile.run .`
* change the used image for `local_base_acapy` and `local_mt_acapy` in `dev-assets/dev-containers/docker-compose.yml`

## Integrate with Indy Ledger

In Indy ledger `Write Access` is usually restricted to endorsers or higher roles. Therefore, the DID and its verkey must be registered
manually before starting ACA-Py.

The [Indy CLI](https://hyperledger-indy.readthedocs.io/projects/sdk/en/latest/docs/design/001-cli/README.html)
in Docker using the [docker-file](https://github.com/hyperledger/indy-sdk/blob/main/cli/cli.dockerfile)
can be used to generate a new DID from a given seed. However, it does not show the
complete `VerKey`, check this [Issue](https://github.com/hyperledger/indy-sdk/issues/2553). 
Therefore, the easiest way to generate a DID is currently to start ACA-Py with a given seed.

  * Navigate to `./dev-assets/generate-did-from-seed`
  * Generate a random seed that has 32 characters. If the use of an offline secure secret/password
    generator is not possible, then these guidelines must be followed:
    * No repeat of characters or strings
    * No patterns or use of dictionary words
    * The use of uppercase and lowercase letters - as well as numbers and allowed symbols
    * No personal preferences like names or phone numbers
  * Set the seed as an enviroment variable e.g. `export SEED=312931k4h15989pqwpou129412i214dk`
  * Run the script generateDidFromSeed script with `./generateDidFromSeed.sh` which starts the
    ACA-Py container and shows the printout of the `DID` and `VerKey` from its logs in the console
    like the following
    ```
    2022-08-12 08:08:13,888 indy.did DEBUG get_my_did_with_meta: <<< res: '{"did":"HW2eFhr3KcZw5JcRW45KNc","verkey":"aEErMofs7DcJT636pocN2RiEHgTLoF4Mpj6heFXwtb3q","tempVerkey":null,"metadata":null}'
    ```
  * If the script did not stop the container, the command `docker compose down -v` can stop and delete it manually
  * Register the DID and verkey with role endorser. This step depends on the used Indy ledger and the defined roles. As an example: On `GreenLight Dev Ledger` the DID and verkey can be registered using the ledger explorer on `http://dev.greenlight.bcovrin.vonx.io/` > `Register from DID`
 
## Testing GitHub actions locally <a id= "testingGitHubActionsLocally"></a>

Using [act](https://github.com/nektos/act) it is possible to test GitHub actions
locally. To run it needs a secrets file, which could be derived on `.env.example`,
see the section above.

```
act --secret-file .env
```

## Test Coverage

Jacoco is used to generate the coverage report. The report generation
and the coverage verification are automatically executed after tests.

The generated HTML report can be found under `jacoco-report/html/`

To generate the report run the command

```
./gradlew jacocoTestReport
```

To check the coverage run the command

```
./gradlew jacocoTestCoverageVerification
```

Currently the minimum is 80%

Files to be excluded from the coverage calculation can be set in
`gradle.properties` using a comma-separated list of files or directories
with possible wildcards as the value for the property `coverage_excludes`.
The files in `models` and `entities` should be excluded as long as they
don't have any logic. The services that are mocked in unit tests must be
excluded. Also their interfaces need to be excluded because they have a
`companion object` that is used to create those services. Files like
`Application.kt` which are tested or simulated indirectly for example
using `withTestApplication` should also be excluded.

## Kotlin Documentation

To generate the Kotlin documentation and java-docs using `dokka` run

```
./gradlew dokkaHtml
./gradlew dokkaJavadoc
```

The generated files can be found under `./build/dokka/html/index.html` and `build/dokka/javadoc/index.html`

Note: Currently, only the Interfaces and their methods are documented.

## Helm Documentation
The `./charts/README.md` is autogenerated from chart metadata using [helm-docs v1.11.0](https://github.com/norwoodj/helm-docs/releases/v1.11.0)

To regenerate the README.md after updating `values.yaml` or `Chart.yaml` run

```
helm-docs --sort-values-order file
```
