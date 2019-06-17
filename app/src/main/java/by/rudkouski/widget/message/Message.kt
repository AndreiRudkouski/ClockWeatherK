package by.rudkouski.widget.message

import android.content.Context
import android.support.design.widget.Snackbar
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import by.rudkouski.widget.R

object Message {

    fun getShortMessage(message: SpannableString, target: View, context: Context) {
        message.setSpan(ForegroundColorSpan(getLightTextColor(context)), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        Snackbar.make(target, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun getLightTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorTextMain, typedValue, true)
        return typedValue.data
    }
}