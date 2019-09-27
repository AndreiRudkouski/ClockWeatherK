package by.rudkouski.widget.update.receiver

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import by.rudkouski.widget.app.App.Companion.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.URL

object NetworkChangeChecker {

    private val observers = HashSet<NetworkObserver>()
    private val connectivityManager = appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkRequest = NetworkRequest.Builder().build()
    private val networkCallbacks = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            if (isInternetAvailable(network)) {
                observers.forEach { it.startUpdate(appContext) }
                unregisterNetworkChangeReceiver()
            }
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
        val activeNetwork = connectivityManager.activeNetwork
        return isInternetAvailable(activeNetwork)
    }

    private fun isInternetAvailable(network: Network?): Boolean {
        if (network != null) {
            return runBlocking {
                withContext(Dispatchers.IO) {
                    try {
                        val connection = network.openConnection(URL("https://www.google.com"))
                        connection.connect()
                        true
                    } catch (ex: Exception) {
                        false
                    }
                }
            }
        }
        return false
    }

    interface NetworkObserver {
        fun startUpdate(context: Context)
    }
}