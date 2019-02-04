package by.rudkouski.widget.receiver

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import by.rudkouski.widget.app.App
import java.util.concurrent.atomic.AtomicBoolean


object NetworkChangeChecker {

    private val isOnline = AtomicBoolean(false)
    private val isRegistered = AtomicBoolean(false)
    private val networkCallbacks = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            isOnline.set(true)
            WeatherUpdateBroadcastReceiver.updateWeather(App.appContext)
        }

        override fun onLost(network: Network) {
            isOnline.set(false)
            unregisterReceiver()
        }
    }

    fun registerReceiver() {
        if (!isRegistered.get()) {
            val connectivityManager = App.appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
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

    fun isOnline() = isOnline.get()
}