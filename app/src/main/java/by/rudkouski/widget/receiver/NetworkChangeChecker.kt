package by.rudkouski.widget.receiver

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import by.rudkouski.widget.app.App
import java.util.concurrent.atomic.AtomicBoolean

object NetworkChangeChecker {

    private val isRegistered = AtomicBoolean(false)
    private val connectivityManager = App.appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkCallbacks = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            WeatherUpdateBroadcastReceiver.updateAllWeathers(App.appContext)
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
            val connectivityManager = App.appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
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