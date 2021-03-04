package app.ifnyas.jenius.util

import android.content.SharedPreferences
import app.ifnyas.jenius.App.Companion.cxt
import app.ifnyas.jenius.util.SessionUtils.Sp.ed
import app.ifnyas.jenius.util.SessionUtils.Sp.sp

class SessionUtils {

    // obj
    private object Sp {
        val sp: SharedPreferences = cxt.getSharedPreferences("SP", 0)
        val ed: SharedPreferences.Editor = sp.edit()
    }

    // fun
    fun clear() {
        ed.clear().apply()
    }

    fun get(key: String): Any? {
        return when (key.split("_").last()) {
            "STR" -> return getStr(key)
            "INT" -> return getInt(key)
            "BOOL" -> return getBool(key)
            "FLT" -> return getFloat(key)
            "LON" -> return getLong(key)
            else -> null
        }
    }

    fun set(key: String, value: Any) {
        when (key.split("_").last()) {
            "STR" -> setStr(key, value as String)
            "INT" -> setInt(key, value as Int)
            "BOOL" -> setBool(key, value as Boolean)
            "FLT" -> setFloat(key, value as Float)
            "LON" -> setLong(key, value as Long)
        }
    }

    private fun getStr(key: String): String? = sp.getString(key, "")
    private fun getInt(key: String): Int = sp.getInt(key, 0)
    private fun getBool(key: String): Boolean = sp.getBoolean(key, false)
    private fun getFloat(key: String): Float = sp.getFloat(key, 0f)
    private fun getLong(key: String): Long = sp.getLong(key, 0)

    private fun setStr(key: String, value: String) = ed.putString(key, value).apply()
    private fun setInt(key: String, value: Int) = ed.putInt(key, value).apply()
    private fun setBool(key: String, value: Boolean) = ed.putBoolean(key, value).apply()
    private fun setFloat(key: String, value: Float) = ed.putFloat(key, value).apply()
    private fun setLong(key: String, value: Long) = ed.putLong(key, value).apply()

    // const
    companion object {
        // auth
        const val btpnApiKey_STR = "btpnApiKey_STR"
        const val accessToken_STR = "accessToken_STR"
        const val idToken_STR = "idToken_STR"

        // profile
        const val profileId_STR = "profileId_STR"
        const val fullName_STR = "fullName_STR"
        const val nickName_STR = "nickName_STR"
        const val cashTag_STR = "cashTag_STR"

        // Primary Account
        const val primaryId_STR = "primaryAccId_STR"
        const val primaryBalance_INT = "primaryBalance_INT"
    }
}