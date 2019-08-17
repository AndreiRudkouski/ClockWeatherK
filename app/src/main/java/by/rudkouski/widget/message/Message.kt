package by.rudkouski.widget.message

import android.content.Context
import android.support.design.widget.Snackbar
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.update.listener.LocationChangeListener.isLocationEnabled
import by.rudkouski.widget.update.receiver.NetworkChangeChecker

object Message {

    fun showShortMessage(message: SpannableString, target: View, context: Context) {
        message.setSpan(ForegroundColorSpan(getLightTextColor(context)), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        Snackbar.make(target, message, Snackbar.LENGTH_SHORT).show()
    }

    fun showNetworkAndLocationEnableMessage(view: View, locationId: Int, context: Context) {
        val message = SpannableStringBuilder()
        if (!NetworkChangeChecker.isOnline()) {
            message.append(SpannableString(context.getString(R.string.no_connection)))
        }
        if (CURRENT_LOCATION_ID == locationId && !isLocationEnabled()) {
            if (!TextUtils.isEmpty(message)) {
                message.append(SpannableString("\n"))
            }
            message.append(SpannableString(context.getString(R.string.no_location)))
        }
        if (!TextUtils.isEmpty(message)) {
            showShortMessage(SpannableString.valueOf(message), view, context)
        }
    }

    private fun getLightTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorTextMain, typedValue, true)
        return typedValue.data
    }
}