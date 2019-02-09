package com.example.offchat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager

class WifiDirectBroadcastReceiver(manager:WifiP2pManager, channel:WifiP2pManager.Channel, activity: MainActivity): BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
    }
}