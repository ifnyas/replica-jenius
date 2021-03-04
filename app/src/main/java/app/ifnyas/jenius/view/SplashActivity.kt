package app.ifnyas.jenius.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // next act
        startActivity(Intent(this, MainActivity::class.java))
        finishAfterTransition()

        // override transition
        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        )
    }
}