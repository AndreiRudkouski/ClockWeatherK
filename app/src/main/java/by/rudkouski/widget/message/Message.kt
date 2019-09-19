package by.rudkouski.widget.message

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.isLocationEnabled
import by.rudkouski.widget.update.receiver.NetworkChangeChecker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT

object Message {

    fun showMessage(message: SpannableString, target: View, context: Context, length: Int) {
        message.setSpan(ForegroundColorSpan(getLightTextColor(context)), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        Snackbar.make(target, message, length).show()
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
            showMessage(SpannableString.valueOf(message), view, context, LENGTH_SHORT)
        }
    }

    private fun getLightTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorTextMain, typedValue, true)
        return typedValue.data
    }
}