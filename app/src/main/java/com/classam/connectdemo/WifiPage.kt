package com.classam.connectdemo

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiPage(viewModel: WifiViewModel = viewModel(), navController: NavController) {
    val wifiList by viewModel.wifiList.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()

    val context = LocalContext.current
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    var isWifiEnabled by remember { mutableStateOf(wifiManager.isWifiEnabled) }

    LaunchedEffect(isWifiEnabled) {
        if (isWifiEnabled) {
            viewModel.startUpdatingTime()
            viewModel.updateWifiNetworks()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "WiFi Page") },
                actions = { Text(text = currentTime) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (!isWifiEnabled) {
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            context.startActivity(Intent(Settings.Panel.ACTION_WIFI))
                        } else {
                            wifiManager.isWifiEnabled = true
                        }
                        isWifiEnabled = wifiManager.isWifiEnabled
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enable WiFi")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    items(wifiList) { wifi ->
                        WifiItem(wifi)
                    }
                }
            }
        }
    }
}

@Composable
fun WifiItem(scanResultWrapper: ScanResultWrapper) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = scanResultWrapper.displaySSID, modifier = Modifier.weight(1f))
        Text(text = "RSSI: ${scanResultWrapper.scanResult.level} dBm", style = MaterialTheme.typography.bodySmall)
    }
}





