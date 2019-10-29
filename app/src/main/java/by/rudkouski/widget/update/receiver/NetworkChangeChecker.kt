package by.rudkouski.widget.update.receiver

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import by.rudkouski.widget.app.App.Companion.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket


object NetworkChangeChecker {

    private const val HOST_NAME = "8.8.8.8"
    private const val DNS_PORT = 53
    private const val TIMEOUT_MILLIS = 1500

    private val observers = HashSet<NetworkObserver>()
    private val connectivityManager = appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkRequest = NetworkRequest.Builder().build()
    private val networkCallbacks = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            runBlocking {
                if (isInternetAvailable()) {
                    observers.forEach { it.startUpdate(appContext) }
                    unregisterNetworkChangeReceiver()
                }
            }
        }
    }

    fun registerNetworkChangeReceiver(observer: NetworkObserver) {
        if (observers.isEmpty()) {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallbacks)
        }
        observers.add(observer)
    }

    private fun unregisterNetworkChangeReceiver() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallbacks)
        } catch (ex: Exception) {
            Log.w(this.javaClass.simpleName, "NetworkCallback for network was not registered or already unregistered")
        }
        observers.clear()
    }

    suspend fun isOnline(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork
        return activeNetwork != null && isInternetAvailable()
    }

    private suspend fun isInternetAvailable(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val sockAddress = InetSocketAddress(HOST_NAME, DNS_PORT)
                val sock = Socket()
                sock.connect(sockAddress, TIMEOUT_MILLIS)
                sock.close()
                return@withContext true
            } catch (ex: Exception) {
                return@withContext false
            }
        }
    }

    interface NetworkObserver {
        fun startUpdate(context: Context)
    }
}