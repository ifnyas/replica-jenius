package app.ifnyas.jenius.model

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import app.ifnyas.jenius.R
import com.afollestad.recyclical.ViewHolder

data class InOut(
        var initial: String = "",
        var title: String = "",
        var date: String = "",
        var amount: Int = 0,
        var type: String = "",
        var isDebit: Boolean = true,
        var image: String = ""
)

class InOutViewHolder(itemView: View) : ViewHolder(itemView) {
    val circle: AppCompatImageView = itemView.findViewById(R.id.img_inout_initial)
    val initial: AppCompatTextView = itemView.findViewById(R.id.text_inout_initial)
    val title: AppCompatTextView = itemView.findViewById(R.id.text_inout_title)
    val date: AppCompatTextView = itemView.findViewById(R.id.text_inout_date)
    val amount: AppCompatTextView = itemView.findViewById(R.id.text_inout_amount)
    val type: AppCompatTextView = itemView.findViewById(R.id.text_inout_type)
}