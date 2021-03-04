package app.ifnyas.jenius.viewmodel

import androidx.lifecycle.ViewModel
import app.ifnyas.jenius.App.Companion.ar
import app.ifnyas.jenius.App.Companion.su
import app.ifnyas.jenius.util.SessionUtils.Companion.accessToken_STR
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class LoginViewModel : ViewModel() {

    private val TAG: String by lazy { javaClass.simpleName }
    private val authId by lazy { MutableStateFlow("") }
    private val phone by lazy { MutableStateFlow("") }

    fun getAuthId(): String {
        return authId.value
    }

    fun getPhone(): String {
        return phone.value
    }

    suspend fun doLogin(mail: String, pass: String): HttpResponse {
        // send request
        val req = withContext(Dispatchers.IO) {
            ar.loginWithEmailPassword(mail, pass)
        }

        // parse data
        if (req.status.value == 200) {
            val res = Json.parseToJsonElement(req.readText()).jsonObject
            val data = res["data"]?.jsonObject
            val login = data?.get("loginWithEmailPassword")?.jsonObject

            // set data
            authId.value = login?.get("authId")?.jsonPrimitive?.contentOrNull ?: ""
            phone.value = login?.get("phone")?.jsonPrimitive?.contentOrNull ?: ""
        }

        // return request
        return req
    }

    fun isLoggedIn(): Boolean {
        return "${su.get(accessToken_STR)}".isNotBlank()
    }
}