package app.ifnyas.jenius.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TextUtils {

    private val TAG: String by lazy { javaClass.simpleName }

    fun formatInitial(full: String): String {
        val nameSplit = full.split(" ")
        val firstName = nameSplit[0][0]
        val lastName = if (nameSplit.size > 1) nameSplit[nameSplit.lastIndex][0] else null
        return if (lastName != null) "$firstName$lastName" else "$firstName"
    }

    fun formatAmount(amount: Int): String {
        return NumberFormat.getInstance(Locale.ITALIAN).format(amount)
    }

    fun formatDate(date: String): String {
        val iPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val oPattern = "d MMM yyyy"
        val input = SimpleDateFormat(iPattern, Locale.ROOT).parse(date) ?: Date()
        return SimpleDateFormat(oPattern, Locale.ROOT).format(input)
    }
}