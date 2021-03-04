package app.ifnyas.jenius

import android.app.Application
import android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import app.ifnyas.jenius.api.ApiRequest
import app.ifnyas.jenius.util.FunUtils
import app.ifnyas.jenius.util.SessionUtils
import app.ifnyas.jenius.util.TextUtils
import app.ifnyas.jenius.view.MainActivity

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initFun()
    }

    private fun initFun() {
        // set day/night mode
        setDefaultNightMode(MODE_NIGHT_NO)

        // init debug
        if (applicationInfo.flags and FLAG_DEBUGGABLE != 0) initDebug()
    }

    private fun initDebug() {
        //
    }

    companion object {
        lateinit var cxt: MainActivity
        val ar by lazy { ApiRequest() }
        val fu by lazy { FunUtils() }
        val su by lazy { SessionUtils() }
        val tu by lazy { TextUtils() }
    }
}