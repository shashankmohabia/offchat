package com.example.offchat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class WifiDirectBroadcastReceiver(
    private var manager: WifiP2pManager,
    private var channel: WifiP2pManager.Channel,
    var activity: MainActivity
) :
    BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        Log.d("shashank", "Wifi On")
                    } else {
                        Log.d("shashank", "Wifi Off")
                    }
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    // Request available peers from the wifi p2p manager. This is an
                    // asynchronous call and the calling activity is notified with a
                    // callback on PeerListListener.onPeersAvailable()
                    manager.requestPeers(channel, activity.peerListListener)
                    Log.d("shashank", "P2P peers changed")
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    manager.let { manager ->

                        val networkInfo: NetworkInfo? = intent
                            .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO) as NetworkInfo

                        if (networkInfo?.isConnected == true) {
                            manager.requestConnectionInfo(channel, activity.connectionListener)
                        } else {
                            Log.d("shashank", "Device Disconnected")
                        }
                    }
                }
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                }
            }
        }
    }

}