package app.ifnyas.jenius.util

import android.view.ViewGroup
import androidx.transition.TransitionManager
import app.ifnyas.jenius.App.Companion.cxt
import com.afollestad.materialdialogs.MaterialDialog

class FunUtils {

    fun createExceptionDialog(msg: String) {
        MaterialDialog(cxt).show {
            title(text = "Exception")
            message(text = msg)
        }
    }

    fun beginTransition(view: ViewGroup) {
        TransitionManager.beginDelayedTransition(view)
    }
}