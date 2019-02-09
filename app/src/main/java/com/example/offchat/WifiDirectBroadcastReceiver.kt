package com.example.offchat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager

class WifiDirectBroadcastReceiver(manager:WifiP2pManager, channel:WifiP2pManager.Channel, activity: MainActivity): BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1 != null) {
            when(p1.action){
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION ->{}
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION ->{}
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION ->{}
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION ->{}
            }
        }
    }
}