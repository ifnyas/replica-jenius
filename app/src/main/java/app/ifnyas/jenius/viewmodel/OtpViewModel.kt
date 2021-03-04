package app.ifnyas.jenius.viewmodel

import androidx.lifecycle.ViewModel
import app.ifnyas.jenius.App.Companion.ar
import app.ifnyas.jenius.App.Companion.su
import app.ifnyas.jenius.util.SessionUtils.Companion.accessToken_STR
import app.ifnyas.jenius.util.SessionUtils.Companion.idToken_STR
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class OtpViewModel : ViewModel() {
    private val TAG: String by lazy { javaClass.simpleName }
    private val authId by lazy { MutableStateFlow("") }
    val otp by lazy { MutableStateFlow("") }

    fun getOtp(): String {
        return otp.value
    }

    fun addOtp(num: String) {
        otp.value = otp.value.plus(num)
    }

    fun delOtp() {
        otp.value = otp.value.dropLast(1)
    }

    fun clearOtp() {
        otp.value = ""
    }

    fun setAuthId(str: String) {
        authId.value = str
    }

    suspend fun verifyOtp(): HttpResponse {
        // send request
        val req = withContext(Dispatchers.IO) {
            ar.webLoginVerifyOTP(authId.value, otp.value)
        }

        // parse data
        if (req.status.value == 200) {
            val res = Json.parseToJsonElement(req.readText()).jsonObject
            val data = res["data"]?.jsonObject
            val login = data?.get("webLoginVerifyOTP")?.jsonObject
            val accessToken = login?.get("access_token")?.jsonPrimitive?.contentOrNull
            val idToken = login?.get("id_token")?.jsonPrimitive?.contentOrNull

            // set session
            su.set(accessToken_STR, "Bearer $accessToken")
            su.set(idToken_STR, idToken ?: "")
        }

        // return request
        return req
    }
}