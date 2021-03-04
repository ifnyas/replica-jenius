package app.ifnyas.jenius.api

import app.ifnyas.jenius.api.ApiClient.httpClient
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class ApiRequest {
    private suspend fun request(body: JsonObject): HttpResponse {
        return try {
            httpClient.post(path = "jenius", body = body)
        } catch (e: ClientRequestException) {
            e.response
        }
    }

    suspend fun loginWithEmailPassword(mail: String, pass: String): HttpResponse {
        val body = buildJsonObject {
            put("query", "mutation loginWithEmailPassword(\$email: String!, \$password: String!) " +
                    "{ loginWithEmailPassword(input: { email: \$email, password: \$password }) " +
                    "{ authId phone __typename }}"
            )
            putJsonObject("variables") {
                put("email", mail)
                put("password", pass)
            }
            put("operationName", "loginWithEmailPassword")
        }

        return request(body)
    }

    suspend fun webLoginVerifyOTP(authId: String, otp: String): HttpResponse {
        val body = buildJsonObject {
            put("query", "mutation webLoginVerifyOTP(\$authId: String!, \$otp: String!) " +
                    "{ webLoginVerifyOTP(input: {authId: \$authId, otp: \$otp}) " +
                    "{ access_token refresh_token id_token mustChangePassword __typename } }"
            )
            putJsonObject("variables") {
                put("authId", authId)
                put("otp", otp)
            }
            put("operationName", "webLoginVerifyOTP")
        }

        return request(body)
    }

    suspend fun userAccountStatusQuery(): HttpResponse {
        val body = buildJsonObject {
            put("query", "query UserAccountStatusQuery {\n  viewer " +
                    "{\n    id\n    fullName\n    nickname\n    avatarUrl\n    cashtag\n    " +
                    "customerType\n    accounts(filter: {types: [PRIMARY]}) " +
                    "{\n      ... on PrimaryAccount {\n        id\n        type\n        " +
                    "status\n        createdAt\n        __typename\n      }\n      " +
                    "__typename\n    }\n    __typename\n  }\n}\n"
            )
            put("operationName", "UserAccountStatusQuery")
        }

        return request(body)
    }

    suspend fun wealthWidgetQuery(): HttpResponse {
        val body = buildJsonObject {
            put("query", "query wealthWidgetQuery {\n  currencies " +
                    "{\n    ...CurrenciesFragment\n    __typename\n  }\n  forexOperationalTime " +
                    "{\n    from\n    to\n    isOperational\n    reason\n    __typename\n  }\n  " +
                    "exchanges {\n    currency\n    buyRate\n    sellRate\n    __typename\n  }" +
                    "\n  settings {\n    flexiSaver {\n      defaultPicture\n      " +
                    "defaultDisplayName\n      maximumFlexiSaver\n      __typename\n    }\n    " +
                    "__typename\n  }\n  viewer {\n    id\n    accounts(filter: " +
                    "{types: [PRIMARY, ECOMMERCE, SUBCARD, DREAMSAVER, MAXISAVER, FLEXISAVER, " +
                    "MAXISAVER_SINAYA, TERM_DEPOSIT_FLEXI, TERM_DEPOSIT_ONCALL, " +
                    "TERM_DEPOSIT_FOREIGN]}) {\n      ... on PrimaryAccount " +
                    "{\n        id\n        displayName\n        status\n        type\n        " +
                    "createdAt\n        currency\n        cards " +
                    "{\n          id\n          status\n          type\n          " +
                    "inflightCardModification {\n            type\n            " +
                    "__typename\n          }\n          __typename\n        }\n        " +
                    "balance {\n          available\n          current\n          " +
                    "hold\n          withheld\n          __typename\n        }\n        " +
                    "__typename\n      }\n      ... on ECommerceAccount " +
                    "{\n        id\n        type\n        balance " +
                    "{\n          available\n          current\n          __typename\n        }" +
                    "\n        cards {\n          id\n          status\n          type\n          " +
                    "image\n          color\n          inflightCardModification {\n            " +
                    "type\n            __typename\n          }\n          __typename\n        }" +
                    "\n        __typename\n      }\n      ... on SubcardAccount {\n        " +
                    "id\n        type\n        balance {\n          available\n          " +
                    "current\n          __typename\n        }\n        cards " +
                    "{\n          id\n          status\n          type\n          " +
                    "alias\n          image\n          color\n          " +
                    "inflightCardModification {\n            type\n            " +
                    "__typename\n          }\n          __typename\n        }\n        " +
                    "__typename\n      }\n      ... on DreamSaverAccount " +
                    "{\n        id\n        displayName\n        pictureUrl\n        " +
                    "dreamSaverStatus\n        type\n        balance {\n          " +
                    "current\n          __typename\n        }\n        __typename\n      }" +
                    "\n      ... on MaxiSaverAccount {\n        id\n        type\n        " +
                    "balance {\n          current\n          __typename\n        }\n        " +
                    "currency\n        __typename\n      }\n      " +
                    "...FlexiSaverAccountFragment\n      __typename\n    }\n    " +
                    "__typename\n  }\n}\n\nfragment CurrenciesFragment on Currency " +
                    "{\n  currency\n  activeImageUrl\n  inActiveImageUrl\n  idID\n  enUS\n  " +
                    "minimumInitialAmount\n  __typename\n}\n\nfragment " +
                    "FlexiSaverAccountFragment on FlexiSaverAccount " +
                    "{\n  ... on FlexiSaverAccount {\n    status\n    id\n    type\n    " +
                    "displayName\n    pictureUrl\n    balance {\n      current\n      " +
                    "__typename\n    }\n    createdAt\n    modifiedAt\n    displayName\n    " +
                    "__typename\n  }\n  __typename\n}\n"
            )
            put("operationName", "wealthWidgetQuery")
        }

        return request(body)
    }

    suspend fun inOutWidgetQuery(limit: Int): HttpResponse {
        val body = buildJsonObject {
            put("query", "query inOutWidgetQuery {\n  viewer {\n    id\n    accounts(filter: " +
                    "{types: [PRIMARY]}) {\n      ... on PrimaryAccount {\n        id\n        " +
                    "type\n        transactions(filter: {includeInternal: false}, limit: $limit, " +
                    "offset: 0) {\n          items {\n            ...TransactionFragment" +
                    "\n            __typename\n          }\n          __typename\n        }" +
                    "\n        __typename\n      }\n      __typename\n    }\n    __typename\n  }" +
                    "\n}\n\nfragment TransactionFragment on Transaction {\n  id\n  amount" +
                    "\n  currency\n  foreignCurrency\n  foreignAmount\n  exchangeValue\n  code" +
                    "\n  debitCredit\n  internal\n  note\n  createdAt\n  fee\n  category {" +
                    "\n    id\n    name\n    icon\n    __typename\n  }\n  metadata {" +
                    "\n    isEnriched\n    isJenius\n    isReceipt\n    isSplitBill" +
                    "\n    clientType\n    __typename\n  }\n  partner {\n    name\n    account" +
                    "\n    org\n    url\n    __typename\n  }\n  type {\n    id\n    name" +
                    "\n    __typename\n  }\n  additionalData {\n    label\n    value" +
                    "\n    __typename\n  }\n  __typename\n}\n")
            put("operationName", "inOutWidgetQuery")
        }

        return request(body)
    }
}