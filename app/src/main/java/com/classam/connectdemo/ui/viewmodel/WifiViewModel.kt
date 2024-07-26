package com.classam.connectdemo.ui.viewmodel

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classam.connectdemo.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WifiViewModel(context: Context) : ViewModel() {

    private val _currentTime = MutableStateFlow("")
    val currentTime = _currentTime.asStateFlow()

    private val _wifiList = MutableStateFlow<List<ScanResultWrapper>>(emptyList())
    val wifiList = _wifiList.asStateFlow()

    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val context by lazy { context }

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            scanSuccess()
        }
    }

    init {
        startUpdatingTime()
        context.registerReceiver(wifiScanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        startWifiScan()
    }

    fun startUpdatingTime() {
        viewModelScope.launch {
            while (true) {
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                _currentTime.value = sdf.format(Date())
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    fun updateWifiNetworks() {
        if (wifiManager.isWifiEnabled) {
            startWifiScan()
        } else {
            _wifiList.value = emptyList()
        }
    }

    private fun startWifiScan() {
        viewModelScope.launch {
            wifiManager.startScan()
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanSuccess() {
        viewModelScope.launch {
            val scanResults = wifiManager.scanResults
            val filteredResults = scanResults.map { result ->
                if (result.SSID.isEmpty()) {
                    ScanResultWrapper(result, context.getString(R.string.hidden_network))
                } else {
                    ScanResultWrapper(result, result.SSID)
                }
            }.sortedByDescending { it.scanResult.level }
            _wifiList.value = filteredResults
            startWifiScan()
        }
    }

    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(wifiScanReceiver)
    }
}

data class ScanResultWrapper(val scanResult: ScanResult, val displaySSID: String)




