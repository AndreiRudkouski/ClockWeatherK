package by.rudkouski.widget.update.receiver

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import by.rudkouski.widget.app.App.Companion.appContext

object NetworkChangeChecker {

    private val observers = HashSet<NetworkObserver>()
    private val connectivityManager = appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkRequest = NetworkRequest.Builder().build()
    private val networkCallbacks = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            observers.forEach { it.startUpdate(appContext) }
        }

        override fun onLost(network: Network) {
            unregisterNetworkChangeReceiver()
        }
    }

    fun registerNetworkChangeReceiver(observer: NetworkObserver) {
        if (observers.isEmpty()) {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallbacks)
        }
        observers.add(observer)
    }

    fun unregisterNetworkChangeReceiver() {
        connectivityManager.unregisterNetworkCallback(networkCallbacks)
        observers.clear()
    }

    fun isOnline(): Boolean {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    interface NetworkObserver {
        fun startUpdate(context: Context)
    }
}