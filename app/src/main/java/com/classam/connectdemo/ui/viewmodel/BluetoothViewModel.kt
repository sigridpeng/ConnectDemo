package com.classam.connectdemo.ui.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classam.connectdemo.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BluetoothViewModel : ViewModel() {

    private val _currentTime = MutableStateFlow("")
    val currentTime = _currentTime.asStateFlow()

    private val _bluetoothList = MutableStateFlow<List<String>>(emptyList())
    val bluetoothList = _bluetoothList.asStateFlow()

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    fun startUpdatingTime() {
        viewModelScope.launch {
            while (true) {
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                _currentTime.value = sdf.format(Date())
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    fun updateBluetoothDevices(context: Context) {
        if (hasBluetoothScanPermission(context)) {
            startScanningBluetoothDevices()
        } else {
            Toast.makeText(context,
                context.getString(R.string.please_grant_bluetooth_permission), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startScanningBluetoothDevices() {
        bluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScanningBluetoothDevices() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let { device ->
                updateBluetoothList(device)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.forEach { result ->
                result.device?.let { device ->
                    updateBluetoothList(device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d("Bluetooth", "Scan Fail")
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateBluetoothList(device: BluetoothDevice) {
        val currentList = _bluetoothList.value.toMutableList()
        val deviceName = device.name ?: device.address
        if (!currentList.contains(deviceName)) {
            currentList.add(deviceName)
            _bluetoothList.value = currentList
        }
    }

    private fun hasBluetoothScanPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}




