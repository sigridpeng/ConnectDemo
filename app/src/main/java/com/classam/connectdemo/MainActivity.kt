package com.classam.connectdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.classam.connectdemo.ui.theme.ConnectDemoTheme

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.entries.all { it.value }

            if (!allPermissionsGranted) {
                Toast.makeText(
                    this,
                    "需要授予所有權限才能正常運行應用程序",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConnectDemoTheme {
                navController = rememberNavController()

                // Manually create and provide WifiViewModel
                val wifiViewModel: WifiViewModel = remember {
                    WifiViewModel(applicationContext)
                }

                NavHost(navController = navController, startDestination = "main_page") {
                    composable("main_page") { MainPage(navController) }
                    composable("wifi_page") { WifiPage(wifiViewModel) }
                    composable("bluetooth_page") { BluetoothPage() }
                }
            }
        }

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            }

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun arePermissionsGranted(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val bluetoothScanGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val bluetoothConnectGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return fineLocationGranted && bluetoothScanGranted && bluetoothConnectGranted
    }

    private fun navigateTo(page: String) {
        if (arePermissionsGranted()) {
            navController.navigate(page)
        } else {
            Toast.makeText(
                this,
                "需要授予所有權限才能訪問此頁面",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // MainPage composable function
    @Composable
    fun MainPage(navController: NavHostController) {
        Column {
            Button(onClick = { navigateTo("wifi_page") }) {
                Text("Go to WiFi Page")
            }
            Button(onClick = { navigateTo("bluetooth_page") }) {
                Text("Go to Bluetooth Page")
            }
        }
    }
}

