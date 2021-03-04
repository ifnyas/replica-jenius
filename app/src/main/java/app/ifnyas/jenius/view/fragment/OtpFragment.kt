package app.ifnyas.jenius.view.fragment

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.viewbinding.library.fragment.viewBinding
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.ifnyas.jenius.App.Companion.fu
import app.ifnyas.jenius.R
import app.ifnyas.jenius.databinding.FragmentOtpBinding
import app.ifnyas.jenius.viewmodel.OtpViewModel
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

class OtpFragment : Fragment(R.layout.fragment_otp) {

    private val TAG: String by lazy { javaClass.simpleName }
    private val vm: OtpViewModel by viewModels()
    private val bind: FragmentOtpBinding by viewBinding()
    private val args: OtpFragmentArgs by navArgs()

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        initFun()
    }

    private fun initFun() {
        // set val
        vm.setAuthId(args.authId)

        // init
        initBtn()
        initView()
    }

    private fun initView() {
        // otp view
        vm.otp.asLiveData().observe(viewLifecycleOwner) { setOtpView() }

        // set text
        val desc = "Masukkan 6 angka kode verifikasi yang dikirim lewat sms ke ${args.phone}"
        bind.textDesc.text = desc
    }

    private fun initBtn() {
        bind.apply {
            // num keys
            listOf(
                    btnKey0, btnKey1, btnKey2, btnKey3, btnKey4,
                    btnKey5, btnKey6, btnKey7, btnKey8, btnKey9
            ).forEachIndexed { i, btnKeyNum ->
                btnKeyNum.setOnClickListener { numPressed("$i") }
            }

            // del keys
            btnKeyDel.setOnClickListener { delPressed() }
        }
    }

    private fun numPressed(num: String) {
        if (vm.getOtp().length < 6) {
            // add otp
            vm.addOtp(num)

            // verify
            if (vm.getOtp().length == 6) verifyOtp()
        }
    }

    private fun delPressed() {
        if (vm.getOtp().isNotEmpty()) vm.delOtp()
        else findNavController().navigateUp()
    }

    private fun verifyOtp() {
        lifecycleScope.launch {
            // start progress
            setProgressView(true)

            // run progress
            kotlin.runCatching { vm.verifyOtp() }
                    .onSuccess { afterLogin(it) }
                    .onFailure { verifyFailed(it) }

            // end progress
            setProgressView(false)
        }
    }

    private fun verifyFailed(e: Throwable) {
        fu.createExceptionDialog("${e.message}")
        vm.clearOtp()
    }

    private fun setOtpView() {
        bind.apply {
            val otpViews = listOf(
                    imgOtp1, imgOtp2, imgOtp3,
                    imgOtp4, imgOtp5, imgOtp6
            )
            val otpLength = vm.getOtp().indices
            otpViews.forEach { it.alpha = 0.5f }
            otpLength.forEach { i -> otpViews[i].alpha = 1f }
        }
    }

    private fun afterLogin(res: HttpResponse) {
        if (res.status.value != 200) verifyFailed()
        else navToHome()
    }

    private fun verifyFailed() {
        vm.clearOtp()
        bind.textDesc.apply {
            text = getString(R.string.edit_invalid)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.red_900))
        }
    }

    private fun navToHome() {
        val dir = OtpFragmentDirections.actionOtpFragmentToHomeFragment()
        findNavController().navigate(dir)
    }

    private fun setProgressView(isLoading: Boolean) {
        bind.progressOtp.visibility = if (isLoading) VISIBLE else GONE
        bind.layKeys.visibility = if (isLoading) GONE else VISIBLE
        bind.textTitle.visibility = if (isLoading) GONE else VISIBLE
        bind.textDesc.visibility = if (isLoading) GONE else VISIBLE
    }
}