package app.ifnyas.jenius.viewmodel

import androidx.lifecycle.ViewModel
import app.ifnyas.jenius.App.Companion.cxt
import app.ifnyas.jenius.App.Companion.su
import app.ifnyas.jenius.R
import app.ifnyas.jenius.util.SessionUtils.Companion.btpnApiKey_STR
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {

    private val isFirst by lazy { MutableStateFlow(true) }

    fun getIsFirst(): Boolean {
        return isFirst.value
    }

    fun setIsFirst(bool: Boolean) {
        isFirst.value = bool
    }

    fun refreshSession() {
        su.clear()
        su.set(btpnApiKey_STR, cxt.getString(R.string.headers_btpnApiKey))
    }
}