package app.ifnyas.jenius.view.fragment

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.ifnyas.jenius.App.Companion.fu
import app.ifnyas.jenius.R
import app.ifnyas.jenius.databinding.FragmentLoginBinding
import app.ifnyas.jenius.viewmodel.LoginViewModel
import app.ifnyas.jenius.viewmodel.MainViewModel
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val TAG: String by lazy { javaClass.simpleName }
    private val avm: MainViewModel by activityViewModels()
    private val vm: LoginViewModel by viewModels()
    private val bind: FragmentLoginBinding by viewBinding()

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        if (vm.isLoggedIn()) navToHome() else initFun()
    }

    private fun initFun() {
        initBtn()
        initCard()
        avm.refreshSession()
        debug()
    }

    private fun initCard() {
        lifecycleScope.launch {
            if (avm.getIsFirst()) {
                delay(500)
                fu.beginTransition(bind.root)
                avm.setIsFirst(false)
            }
            bind.cardForm.visibility = VISIBLE
        }
    }

    private fun initBtn() {
        bind.btnLogin.setOnClickListener { if (isFormValid()) doLogin() }
    }

    private fun doLogin() {
        // init val
        val mail = bind.editEmail.text
        val pass = bind.editPass.text

        lifecycleScope.launch {
            // start progress
            setProgressView(true)

            // run progress
            kotlin.runCatching { vm.doLogin("$mail", "$pass") }
                    .onSuccess { afterLogin(it) }
                    .onFailure { fu.createExceptionDialog("${it.message}") }

            // end progress
            setProgressView(false)
        }
    }

    private fun isFormValid(): Boolean {
        // init err
        var err = 0

        // check email
        bind.layEmail.error = if (bind.editEmail.text.isNullOrBlank()) {
            err++; getString(R.string.edit_empty)
        } else ""

        // check pass
        bind.layEmail.error = if (bind.editEmail.text.isNullOrBlank()) {
            err++; getString(R.string.edit_empty)
        } else ""

        // return validation
        return err == 0
    }

    private fun afterLogin(res: HttpResponse) {
        if (res.status.value == 200) navToOtp()
        else {
            bind.layEmail.error = getString(R.string.edit_invalid)
            bind.layPass.error = getString(R.string.edit_invalid)
        }
    }

    private fun navToOtp() {
        val dir = LoginFragmentDirections.actionLoginFragmentToOtpFragment(
                vm.getAuthId(), vm.getPhone()
        )
        findNavController().navigate(dir)
    }

    private fun navToHome() {
        val dir = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        findNavController().navigate(dir)
    }

    private fun setProgressView(isLoading: Boolean) {
        bind.progressLogin.visibility = if (isLoading) VISIBLE else GONE
        bind.btnLogin.visibility = if (isLoading) GONE else VISIBLE
    }

    private fun debug() {
        bind.imgLogo.setOnClickListener {
            bind.editEmail.setText(getString(R.string.debug_mail))
            bind.editPass.setText(getString(R.string.debug_pass))
            //navToHome()
        }
    }
}