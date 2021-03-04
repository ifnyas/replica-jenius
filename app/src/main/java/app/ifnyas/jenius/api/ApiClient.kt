package app.ifnyas.jenius.api

import app.ifnyas.jenius.App.Companion.su
import app.ifnyas.jenius.util.SessionUtils.Companion.accessToken_STR
import app.ifnyas.jenius.util.SessionUtils.Companion.btpnApiKey_STR
import app.ifnyas.jenius.util.SessionUtils.Companion.idToken_STR
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

object ApiClient {
    val httpClient: HttpClient by lazy {
        HttpClient(Android) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                )
            }

            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }

            defaultRequest {
                host = "api.btpn.com"
                url { protocol = URLProtocol.HTTPS }
                headers.append(HttpHeaders.ContentType, ContentType.Application.Json)
                headers.append(HttpHeaders.Authorization, "${su.get(accessToken_STR)}")
                headers.append("BTPN-ApiKey", "${su.get(btpnApiKey_STR)}")
                headers.append("X-ID-Token", "${su.get(idToken_STR)}")
            }
        }
    }
}