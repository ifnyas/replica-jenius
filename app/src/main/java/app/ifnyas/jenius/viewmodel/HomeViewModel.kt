package app.ifnyas.jenius.viewmodel

import androidx.lifecycle.ViewModel
import app.ifnyas.jenius.App.Companion.ar
import app.ifnyas.jenius.App.Companion.su
import app.ifnyas.jenius.App.Companion.tu
import app.ifnyas.jenius.model.InOut
import app.ifnyas.jenius.util.SessionUtils.Companion.cashTag_STR
import app.ifnyas.jenius.util.SessionUtils.Companion.fullName_STR
import app.ifnyas.jenius.util.SessionUtils.Companion.nickName_STR
import app.ifnyas.jenius.util.SessionUtils.Companion.primaryBalance_INT
import app.ifnyas.jenius.util.SessionUtils.Companion.primaryId_STR
import app.ifnyas.jenius.util.SessionUtils.Companion.profileId_STR
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

class HomeViewModel : ViewModel() {

    private val TAG: String by lazy { javaClass.simpleName }
    private val initialName by lazy { MutableStateFlow("") }
    private val inOutList by lazy { MutableStateFlow(mutableListOf<InOut>()) }

    fun getInOutList(): List<InOut> {
        return inOutList.value
    }

    suspend fun getUserQuery(): HttpResponse {
        // send request
        val req = withContext(Dispatchers.IO) {
            ar.userAccountStatusQuery()
        }

        // parse data
        if (req.status.value == 200) {
            val res = Json.parseToJsonElement(req.readText()).jsonObject
            val data = res["data"]?.jsonObject
            val viewer = data?.get("viewer")?.jsonObject

            // get profile
            val profileId = viewer?.get("id")?.jsonPrimitive?.contentOrNull
            val fullName = viewer?.get("fullName")?.jsonPrimitive?.contentOrNull
            val nickName = viewer?.get("nickname")?.jsonPrimitive?.contentOrNull
            val cashTag = viewer?.get("cashtag")?.jsonPrimitive?.contentOrNull

            // set session
            initialName.value = tu.formatInitial(fullName ?: "")
            su.set(profileId_STR, profileId ?: "")
            su.set(fullName_STR, fullName ?: "")
            su.set(nickName_STR, nickName ?: "")
            su.set(cashTag_STR, cashTag ?: "")
        }

        // return request
        return req
    }

    suspend fun getWealthQuery(): HttpResponse {
        // send request
        val req = withContext(Dispatchers.IO) {
            ar.wealthWidgetQuery()
        }

        // parse data
        if (req.status.value == 200) {
            val res = Json.parseToJsonElement(req.readText()).jsonObject
            val data = res["data"]?.jsonObject
            val viewer = data?.get("viewer")?.jsonObject

            // get primary account
            val account = viewer?.get("accounts")?.jsonArray?.get(0)?.jsonObject
            val primaryId = account?.get("id")?.jsonPrimitive?.contentOrNull
            val primaryBalance = account?.get("balance")?.jsonObject
            val currentPrimaryBalance = primaryBalance?.get("current")?.jsonPrimitive?.intOrNull

            // set session
            su.set(primaryId_STR, primaryId ?: "")
            su.set(primaryBalance_INT, currentPrimaryBalance ?: 0)
        }

        // return request
        return req
    }

    suspend fun getInOutQuery(): HttpResponse {
        // send request
        val req = withContext(Dispatchers.IO) {
            ar.inOutWidgetQuery(10)
        }

        // parse data
        if (req.status.value == 200) {
            val res = Json.parseToJsonElement(req.readText()).jsonObject
            val data = res["data"]?.jsonObject
            val viewer = data?.get("viewer")?.jsonObject

            // get items
            val account = viewer?.get("accounts")?.jsonArray?.get(0)?.jsonObject
            val transactions = account?.get("transactions")?.jsonObject
            val items = transactions?.get("items")?.jsonArray

            items?.forEach {
                val item = it.jsonObject
                val amount = item["amount"]?.jsonPrimitive?.intOrNull ?: 0
                val isDebit = item["debitCredit"]?.jsonPrimitive?.contentOrNull == "DEBIT"
                val createdAt = item["createdAt"]?.jsonPrimitive?.contentOrNull?.trim()
                val type = item["category"]?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull?.trim()
                val title = item["partner"]?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull?.trim()
                val image = item["partner"]?.jsonObject?.get("url")?.jsonPrimitive?.contentOrNull?.trim()
                // add to list
                inOutList.value.add(
                        InOut(
                                tu.formatInitial(title ?: ""), title ?: "",
                                tu.formatDate(createdAt ?: ""), amount,
                                type ?: "", isDebit, image ?: ""
                        )
                )
            }
        }

        // return request
        return req
    }

    suspend fun getInOutQueryDebug() {
        val req = "{\"data\":{\"viewer\":{\"id\":\"3782XA\",\"accounts\":[{\"id\":\"90012708885\",\"type\":\"PRIMARY\",\"transactions\":{\"items\":[{\"id\":\"202103040001@DCB398229\",\"amount\":1,\"currency\":\"IDR\",\"foreignCurrency\":\"IDR\",\"foreignAmount\":1,\"exchangeValue\":1,\"code\":\"082\",\"debitCredit\":\"DEBIT\",\"internal\":false,\"note\":\"tes ngirim\",\"createdAt\":\"2021-03-04T09:42:12.097Z\",\"fee\":0,\"category\":{\"id\":\"998\",\"name\":\"Uang keluar\",\"icon\":\"/avatar/category/images/outgoing.png\",\"__typename\":\"TransactionCategory\"},\"metadata\":{\"isEnriched\":true,\"isJenius\":true,\"isReceipt\":true,\"isSplitBill\":true,\"clientType\":\"PERSONAL\",\"__typename\":\"TransactionMetadata\"},\"partner\":{\"name\":\"IHSAN RIZKY SAPUTRA\",\"account\":\"90014049048\",\"org\":\"Jenius\",\"url\":null,\"__typename\":\"TransactionPartner\"},\"type\":{\"id\":\"116\",\"name\":\"Transfer Keluar\",\"__typename\":\"TransactionType\"},\"additionalData\":[],\"__typename\":\"Transaction\"},{\"id\":\"202103030001@DCB98323\",\"amount\":120000,\"currency\":\"IDR\",\"foreignCurrency\":\"IDR\",\"foreignAmount\":120000,\"exchangeValue\":1,\"code\":\"045\",\"debitCredit\":\"DEBIT\",\"internal\":false,\"note\":\"\",\"createdAt\":\"2021-03-03T00:09:23.911Z\",\"fee\":0,\"category\":{\"id\":\"998\",\"name\":\"Uang keluar\",\"icon\":\"/avatar/category/images/outgoing.png\",\"__typename\":\"TransactionCategory\"},\"metadata\":{\"isEnriched\":true,\"isJenius\":false,\"isReceipt\":true,\"isSplitBill\":true,\"clientType\":\"PERSONAL\",\"__typename\":\"TransactionMetadata\"},\"partner\":{\"name\":\"ISNI NOVITASARI\",\"account\":\"000501106557503\",\"org\":\"BRI\",\"url\":null,\"__typename\":\"TransactionPartner\"},\"type\":{\"id\":\"116\",\"name\":\"Transfer Keluar\",\"__typename\":\"TransactionType\"},\"additionalData\":[],\"__typename\":\"Transaction\"},{\"id\":\"202103020001@DCB609199\",\"amount\":100000,\"currency\":\"IDR\",\"foreignCurrency\":\"IDR\",\"foreignAmount\":100000,\"exchangeValue\":1,\"code\":\"045\",\"debitCredit\":\"DEBIT\",\"internal\":false,\"note\":\"\",\"createdAt\":\"2021-03-02T15:23:50.276Z\",\"fee\":0,\"category\":{\"id\":\"998\",\"name\":\"Uang keluar\",\"icon\":\"/avatar/category/images/outgoing.png\",\"__typename\":\"TransactionCategory\"},\"metadata\":{\"isEnriched\":true,\"isJenius\":false,\"isReceipt\":true,\"isSplitBill\":true,\"clientType\":\"PERSONAL\",\"__typename\":\"TransactionMetadata\"},\"partner\":{\"name\":\"BRIVA JUSTINEJUNO\",\"account\":\"112085624649276\",\"org\":\"BRI\",\"url\":null,\"__typename\":\"TransactionPartner\"},\"type\":{\"id\":\"116\",\"name\":\"Transfer Keluar\",\"__typename\":\"TransactionType\"},\"additionalData\":[],\"__typename\":\"Transaction\"},{\"id\":\"202103010001@DCB808496\",\"amount\":100000,\"currency\":\"IDR\",\"foreignCurrency\":\"IDR\",\"foreignAmount\":100000,\"exchangeValue\":1,\"code\":\"582\",\"debitCredit\":\"CREDIT\",\"internal\":false,\"note\":\"\",\"createdAt\":\"2021-03-01T15:12:51.696Z\",\"fee\":0,\"category\":{\"id\":\"999\",\"name\":\"Uang masuk\",\"icon\":\"/avatar/category/images/incoming.png\",\"__typename\":\"TransactionCategory\"},\"metadata\":{\"isEnriched\":true,\"isJenius\":true,\"isReceipt\":false,\"isSplitBill\":false,\"clientType\":\"PERSONAL\",\"__typename\":\"TransactionMetadata\"},\"partner\":{\"name\":\"PRANESHA WAHYU SINATRIA ALDIARTA\",\"account\":\"90012097892\",\"org\":\"Jenius\",\"url\":null,\"__typename\":\"TransactionPartner\"},\"type\":{\"id\":\"108\",\"name\":\"Transfer Masuk\",\"__typename\":\"TransactionType\"},\"additionalData\":[],\"__typename\":\"Transaction\"},{\"id\":\"202103010001@@AT351129\",\"amount\":300000,\"currency\":\"IDR\",\"foreignCurrency\":\"IDR\",\"foreignAmount\":300000,\"exchangeValue\":1,\"code\":\"0A2\",\"debitCredit\":\"DEBIT\",\"internal\":false,\"note\":\"\",\"createdAt\":\"2021-03-01T05:15:01.151Z\",\"fee\":0,\"category\":{\"id\":\"116\",\"name\":\"Tarik Tunai\",\"icon\":\"/avatar/category/images/cash_withdrawal.png\",\"__typename\":\"TransactionCategory\"},\"metadata\":{\"isEnriched\":true,\"isJenius\":false,\"isReceipt\":false,\"isSplitBill\":true,\"clientType\":\"PERSONAL\",\"__typename\":\"TransactionMetadata\"},\"partner\":{\"name\":\"Tarik Tunai di ATM\",\"account\":\"TELKOM GEGERKALONG BANDUNG ID\",\"org\":null,\"url\":\"https://api.btpn.com/j2image/144/avatar/transaction/images/0a2_prima_atm_cash_withdrawal.png?BTPN-ApiKey=d3fc745f-d978-45b3-857a-f21f9af5fa9b\",\"__typename\":\"TransactionPartner\"},\"type\":{\"id\":\"105\",\"name\":\"Penarikan Tunai\",\"__typename\":\"TransactionType\"},\"additionalData\":[],\"__typename\":\"Transaction\"},{\"id\":\"202102289001@@CI2070995\",\"amount\":163,\"currency\":\"IDR\",\"foreignCurrency\":\"IDR\",\"foreignAmount\":163,\"exchangeValue\":1,\"code\":\"40A\",\"debitCredit\":\"DEBIT\",\"internal\":false,\"note\":\"\",\"createdAt\":\"2021-02-28T13:59:55.153Z\",\"fee\":0,\"category\":{\"id\":\"130\",\"name\":\"Biaya & pajak\",\"icon\":\"/avatar/category/images/cost_taxes.png\",\"__typename\":\"TransactionCategory\"},\"metadata\":{\"isEnriched\":true,\"isJenius\":false,\"isReceipt\":false,\"isSplitBill\":false,\"clientType\":\"PERSONAL\",\"__typename\":\"TransactionMetadata\"},\"partner\":{\"name\":\"Pajak Bunga\",\"account\":\"Active Balance\",\"org\":null,\"url\":\"https://api.btpn.com/j2image/144/avatar/transaction/images/40A_tax_main.png?BTPN-ApiKey=d3fc745f-d978-45b3-857a-f21f9af5fa9b\",\"__typename\":\"TransactionPartner\"},\"type\":{\"id\":\"122\",\"name\":\"Pajak Bunga\",\"__typename\":\"TransactionType\"},\"additionalData\":[],\"__typename\":\"Transaction\"},{\"id\":\"202102289001@@CI2070990\",\"amount\":813,\"currency\":\"IDR\",\"foreignCurrency\":\"IDR\",\"foreignAmount\":813,\"exchangeValue\":1,\"code\":\"92A\",\"debitCredit\":\"CREDIT\",\"internal\":false,\"note\":\"\",\"createdAt\":\"2021-02-28T13:59:55.150Z\",\"fee\":0,\"category\":{\"id\":\"206\",\"name\":\"Bunga\",\"icon\":\"/avatar/category/images/interest.png\",\"__typename\":\"TransactionCategory\"},\"metadata\":{\"isEnriched\":true,\"isJenius\":false,\"isReceipt\":false,\"isSplitBill\":false,\"clientType\":\"PERSONAL\",\"__typename\":\"TransactionMetadata\"},\"partner\":{\"name\":\"Bunga\",\"account\":\"Active Balance\",\"org\":null,\"url\":\"https://api.btpn.com/j2image/144/avatar/transaction/images/92A_interest_main.png?BTPN-ApiKey=d3fc745f-d978-45b3-857a-f21f9af5fa9b\",\"__typename\":\"TransactionPartner\"},\"type\":{\"id\":\"110\",\"name\":\"Bunga\",\"__typename\":\"TransactionType\"},\"additionalData\":[],\"__typename\":\"Transaction\"},{\"id\":\"202102270001@DCB97215\",\"amount\":150000,\"currency\":\"IDR\",\"foreignCurrency\":\"IDR\",\"foreignAmount\":150000,\"exchangeValue\":1,\"code\":\"045\",\"debitCredit\":\"DEBIT\",\"internal\":false,\"note\":\"\",\"createdAt\":\"2021-02-27T00:15:34.149Z\",\"fee\":0,\"category\":{\"id\":\"998\",\"name\":\"Uang keluar\",\"icon\":\"/avatar/category/images/outgoing.png\",\"__typename\":\"TransactionCategory\"},\"metadata\":{\"isEnriched\":true,\"isJenius\":false,\"isReceipt\":true,\"isSplitBill\":true,\"clientType\":\"PERSONAL\",\"__typename\":\"TransactionMetadata\"},\"partner\":{\"name\":\"BRIVA JUSTINEJUNO\",\"account\":\"112085624649276\",\"org\":\"BRI\",\"url\":null,\"__typename\":\"TransactionPartner\"},\"type\":{\"id\":\"116\",\"name\":\"Transfer Keluar\",\"__typename\":\"TransactionType\"},\"additionalData\":[],\"__typename\":\"Transaction\"},{\"id\":\"202102250001@DCB655735\",\"amount\":100000,\"currency\":\"IDR\",\"foreignCurrency\":\"IDR\",\"foreignAmount\":100000,\"exchangeValue\":1,\"code\":\"0D8\",\"debitCredit\":\"DEBIT\",\"internal\":false,\"note\":\"\",\"createdAt\":\"2021-02-25T14:50:43.071Z\",\"fee\":0,\"category\":{\"id\":\"145\",\"name\":\"Top up Wallet\",\"icon\":\"/avatar/category/images/topup_wallet.png\",\"__typename\":\"TransactionCategory\"},\"metadata\":{\"isEnriched\":true,\"isJenius\":false,\"isReceipt\":true,\"isSplitBill\":true,\"clientType\":\"PERSONAL\",\"__typename\":\"TransactionMetadata\"},\"partner\":{\"name\":\"JUSTINE ARDELIA DIGNA         \",\"account\":\"085624649276\",\"org\":\"GOPAY\",\"url\":\"https://api.btpn.com/j2image/144/avatar/transaction/images/0D8_gopay_topup.png?BTPN-ApiKey=d3fc745f-d978-45b3-857a-f21f9af5fa9b\",\"__typename\":\"TransactionPartner\"},\"type\":{\"id\":\"116\",\"name\":\"Transfer Keluar\",\"__typename\":\"TransactionType\"},\"additionalData\":[],\"__typename\":\"Transaction\"},{\"id\":\"202102230001@DCB117733\",\"amount\":100000,\"currency\":\"IDR\",\"foreignCurrency\":\"IDR\",\"foreignAmount\":100000,\"exchangeValue\":1,\"code\":\"045\",\"debitCredit\":\"DEBIT\",\"internal\":false,\"note\":\"\",\"createdAt\":\"2021-02-23T02:34:00.802Z\",\"fee\":0,\"category\":{\"id\":\"998\",\"name\":\"Uang keluar\",\"icon\":\"/avatar/category/images/outgoing.png\",\"__typename\":\"TransactionCategory\"},\"metadata\":{\"isEnriched\":true,\"isJenius\":false,\"isReceipt\":true,\"isSplitBill\":true,\"clientType\":\"PERSONAL\",\"__typename\":\"TransactionMetadata\"},\"partner\":{\"name\":\"BRIVA JUSTINEJUNO\",\"account\":\"112085624649276\",\"org\":\"BRI\",\"url\":null,\"__typename\":\"TransactionPartner\"},\"type\":{\"id\":\"116\",\"name\":\"Transfer Keluar\",\"__typename\":\"TransactionType\"},\"additionalData\":[],\"__typename\":\"Transaction\"}],\"__typename\":\"TransactionList\"},\"__typename\":\"PrimaryAccount\"}],\"__typename\":\"Profile\"}}}"
        val res = Json.parseToJsonElement(req).jsonObject
        val data = res["data"]?.jsonObject
        val viewer = data?.get("viewer")?.jsonObject

        // get items
        val account = viewer?.get("accounts")?.jsonArray?.get(0)?.jsonObject
        val transactions = account?.get("transactions")?.jsonObject
        val items = transactions?.get("items")?.jsonArray

        items?.forEach {
            val item = it.jsonObject
            val amount = item["amount"]?.jsonPrimitive?.intOrNull ?: 0
            val isDebit = item["debitCredit"]?.jsonPrimitive?.contentOrNull == "DEBIT"
            val createdAt = item["createdAt"]?.jsonPrimitive?.contentOrNull?.trim()
            val type = item["category"]?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull?.trim()
            val title = item["partner"]?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull?.trim()
            val image = item["partner"]?.jsonObject?.get("url")?.jsonPrimitive?.contentOrNull?.trim()

            // add to list
            inOutList.value.add(
                    InOut(
                            tu.formatInitial(title ?: ""), title ?: "",
                            tu.formatDate(createdAt ?: ""), amount,
                            type ?: "", isDebit, image ?: ""
                    )
            )
        }
    }

    fun getInitialName(): String {
        return initialName.value
    }

    fun getPrimaryBalance(): String {
        return formatAmount(su.get(primaryBalance_INT) as Int)
    }

    fun formatAmount(amount: Int): String {
        return tu.formatAmount(amount)
    }
}