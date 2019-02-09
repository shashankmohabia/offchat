package com.example.offchat

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var wifiManager: WifiManager
    lateinit var wifiP2pManager: WifiP2pManager
    lateinit var wifiP2pChannel: WifiP2pManager.Channel
    lateinit var intentFilter:IntentFilter
    lateinit var broadcastReceiver: WifiDirectBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        setupOnclickListeners()

    }

    private fun setupOnclickListeners() {

        onOff.setOnClickListener {

            if (wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = false
                onOff.text = "Wifi On"
            } else {
                wifiManager.isWifiEnabled = true
                onOff.text = "Wifi Off"
            }
        }
    }

    private fun init() {
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        wifiP2pChannel = wifiP2pManager.initialize(this, mainLooper, null)
        intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        broadcastReceiver = WifiDirectBroadcastReceiver(wifiP2pManager,wifiP2pChannel,this)
    }
}
