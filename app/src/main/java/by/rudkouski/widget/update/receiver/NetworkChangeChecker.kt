package by.rudkouski.widget.update.receiver

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.updateCurrentLocation
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver.Companion.updateAllWeathers
import java.util.concurrent.atomic.AtomicBoolean

object NetworkChangeChecker {

    private val isRegistered = AtomicBoolean(false)
    private val connectivityManager = appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkCallbacks = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateAllWeathers(appContext)
            updateCurrentLocation(appContext)
        }

        override fun onLost(network: Network) {
            unregisterReceiver()
        }
    }

    fun registerReceiver() {
        if (!isRegistered.get()) {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(), networkCallbacks)
            isRegistered.set(true)
        }
    }

    fun unregisterReceiver() {
        if (isRegistered.get()) {
            val connectivityManager = appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(networkCallbacks)
            isRegistered.set(false)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isOnline() = isNetworkAvailable()
}