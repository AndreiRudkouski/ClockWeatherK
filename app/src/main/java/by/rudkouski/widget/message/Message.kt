package by.rudkouski.widget.message

import android.content.Context
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextUtils.isEmpty
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.isLocationEnabled
import by.rudkouski.widget.update.receiver.NetworkChangeChecker.isOnline
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Message {

    fun asyncCheckConnectionsAndShowMessage(networkCheck: Boolean = true,
                                            gpsCheck: Boolean = true,
                                            target: View,
                                            context: Context,
                                            locationId: Int,
                                            defaultMessage: String? = null,
                                            length: Int = LENGTH_SHORT) {
        GlobalScope.launch {
            val message = StringBuilder()
            if (networkCheck && !isOnline()) {
                message.append(context.getString(R.string.no_connection))
            }
            if (gpsCheck && CURRENT_LOCATION_ID == locationId && !isLocationEnabled()) {
                if (!isEmpty(message)) {
                    message.append("\n")
                }
                message.append(context.getString(R.string.no_location))
            }
            if (isEmpty(message) && defaultMessage != null) {
                message.append(defaultMessage)
            }
            if (!isEmpty(message)) {
                showMessage(SpannableString.valueOf(message), target, context, length)
            }
        }
    }

    private fun showMessage(message: SpannableString, target: View, context: Context, length: Int) {
        message.setSpan(ForegroundColorSpan(getLightTextColor(context)), 0, message.length, SPAN_EXCLUSIVE_EXCLUSIVE)
        Snackbar.make(target, message, length).show()
    }

    private fun getLightTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorTextMain, typedValue, true)
        return typedValue.data
    }
}