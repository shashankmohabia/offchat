package com.example.offchat

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class MainActivity : AppCompatActivity() {

    lateinit var wifiManager: WifiManager
    lateinit var wifiP2pManager: WifiP2pManager
    lateinit var wifiP2pChannel: WifiP2pManager.Channel
    lateinit var intentFilter: IntentFilter
    lateinit var broadcastReceiver: WifiDirectBroadcastReceiver

    private val peers = mutableListOf<WifiP2pDevice>()
    private val deviceNameArray = mutableListOf<String>()

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

        discover.setOnClickListener {
            wifiP2pManager.discoverPeers(wifiP2pChannel, object : WifiP2pManager.ActionListener {

                override fun onSuccess() {
                    connectionStatus.text = "Discovery Started"
                }

                override fun onFailure(reasonCode: Int) {
                    connectionStatus.text = "Discovery Failed"
                }
            })
        }

        peerListView.setOnItemClickListener { adapterView, view, i, l ->
            val device = peers[i]
            val config = WifiP2pConfig().apply {
                deviceAddress = device.deviceAddress
                wps.setup = WpsInfo.PBC
            }


            wifiP2pManager.connect(wifiP2pChannel, config, object : WifiP2pManager.ActionListener {

                override fun onSuccess() {
                    Toast.makeText(
                        this@MainActivity,
                        "Connect to ${device.deviceName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(reason: Int) {
                    Toast.makeText(
                        this@MainActivity,
                        "Connect failed. Retry.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
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
        broadcastReceiver = WifiDirectBroadcastReceiver(wifiP2pManager, wifiP2pChannel, this)
    }

    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            for (device in refreshedPeers) {
                deviceNameArray.add(device.deviceName)
            }

            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceNameArray)
            peerListView.adapter = adapter
        }

        if (peers.isEmpty()) {
            Log.d("shashank", "No devices found")
            return@PeerListListener
        }
    }

    val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->

        // InetAddress from WifiP2pInfo struct.
        val groupOwnerAddress: String = info.groupOwnerAddress.hostAddress

        // After the group negotiation, we can determine the group owner
        // (server).
        if (info.groupFormed && info.isGroupOwner) {
            connectionStatus.text = "Host"
        } else if (info.groupFormed) {
            connectionStatus.text = "Client"
        }
    }

    public override fun onResume() {
        super.onResume()
        broadcastReceiver = WifiDirectBroadcastReceiver(wifiP2pManager, wifiP2pChannel, this)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    class ServerClass : Thread() {
        lateinit var socket: Socket
        lateinit var serverSocket: ServerSocket
        override fun run() {
            try {
                serverSocket = ServerSocket(8888)
                socket = serverSocket.accept()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    class ClientClass(var hostAddress: String, var socket: Socket = Socket()) : Thread() {
        override fun run() {
            try {
                socket.connect(InetSocketAddress(hostAddress, 8888), 500)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
