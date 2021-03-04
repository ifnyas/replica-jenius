package app.ifnyas.jenius.view

import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.ifnyas.jenius.App.Companion.cxt
import app.ifnyas.jenius.databinding.ActivityMainBinding
import app.ifnyas.jenius.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val TAG: String by lazy { javaClass.simpleName }
    private val bind: ActivityMainBinding by viewBinding()
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        initFun()
    }

    private fun initFun() {
        // set context
        cxt = this
    }
}