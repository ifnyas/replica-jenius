package app.ifnyas.jenius.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.viewbinding.library.fragment.viewBinding
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.ifnyas.jenius.App.Companion.fu
import app.ifnyas.jenius.R
import app.ifnyas.jenius.databinding.FragmentHomeBinding
import app.ifnyas.jenius.model.InOut
import app.ifnyas.jenius.model.InOutViewHolder
import app.ifnyas.jenius.view.SplashActivity
import app.ifnyas.jenius.viewmodel.HomeViewModel
import app.ifnyas.jenius.viewmodel.MainViewModel
import coil.load
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val TAG: String by lazy { javaClass.simpleName }
    private val avm: MainViewModel by activityViewModels()
    private val vm: HomeViewModel by viewModels()
    private val bind: FragmentHomeBinding by viewBinding()

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        initFun()
    }

    private fun initFun() {
        initBtn()
        initData()
        //debug()
    }

    private fun initData() {
        lifecycleScope.launch {
            // start progress
            setProgressView(true)

            // run progress
            kotlin.runCatching { vm.getUserQuery() }
                    .onFailure { requestFailure(it) }

            kotlin.runCatching { vm.getInOutQuery() }
                    .onFailure { requestFailure(it) }

            kotlin.runCatching { vm.getWealthQuery() }
                    .onSuccess { initView() }
                    .onFailure { requestFailure(it) }

            // end progress
            setProgressView(false)
        }
    }

    private fun initBtn() {
        bind.btnProfile.setOnClickListener { createLogoutDialog() }
    }

    private fun initView() {
        bind.btnProfile.text = vm.getInitialName()
        bind.textBalance.text = vm.getPrimaryBalance()
        initRecycler()
    }

    private fun initRecycler() {
        val list = vm.getInOutList()
        bind.textStaticListEmpty.visibility = if (list.isEmpty()) VISIBLE else GONE
        if (list.isNotEmpty()) bind.rvInout.setup {
            withDataSource(dataSourceTypedOf(list))
            withItem<InOut, InOutViewHolder>(R.layout.item_inout) {
                onBind(::InOutViewHolder) { _, item ->
                    amount.text = vm.formatAmount(item.amount)
                    initial.text = item.initial
                    title.text = item.title
                    date.text = item.date
                    type.text = item.type

                    if (item.image.isNotBlank()) {
                        circle.load(item.image)
                        initial.visibility = GONE
                    } else {
                        circle.setColorFilter(
                                ContextCompat.getColor(
                                        requireContext(),
                                        R.color.indigo_200
                                )
                        )
                    }

                    if (!item.isDebit) {
                        if (item.image.isBlank()) circle.setColorFilter(
                                ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_200
                                )
                        )

                        val amountPlus = "+${amount.text}"
                        amount.apply {
                            text = amountPlus
                            setTextColor(
                                    ContextCompat.getColor(
                                            requireContext(),
                                            R.color.green_500
                                    )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createLogoutDialog() {
        MaterialDialog(requireContext()).show {
            title(text = "Logout")
            message(text = "Keluar dari akun saat ini?")
            positiveButton(text = "Ya") { navToSplash() }
            negativeButton(text = "Kembali")
        }
    }

    private fun navToSplash() {
        avm.refreshSession()
        activity?.apply {
            finishAfterTransition()
            startActivity(Intent(requireContext(), SplashActivity::class.java))
        }
    }

    private fun requestFailure(t: Throwable) {
        t.printStackTrace()
        fu.createExceptionDialog("${t.message}")
    }

    private fun setProgressView(isLoading: Boolean) {
        bind.layRefresh.isRefreshing = isLoading
        bind.progressInout.visibility = if (isLoading) VISIBLE else GONE
    }

    private fun debug() {
        lifecycleScope.launch {
            setProgressView(true)
            kotlin.runCatching { vm.getInOutQueryDebug() }
                    .onSuccess { initView() }
                    .onFailure { it.printStackTrace(); fu.createExceptionDialog("${it.message}") }
            setProgressView(false)
        }
    }
}