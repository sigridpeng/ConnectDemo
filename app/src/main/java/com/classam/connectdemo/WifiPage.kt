package com.classam.connectdemo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiPage(viewModel: WifiViewModel = viewModel()) {
    val wifiList by viewModel.wifiList.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(wifiList) { wifi ->
                    WifiItem(scanResultWrapper = wifi)
                }
            }
        }
    }
}

@Composable
fun WifiItem(scanResultWrapper: ScanResultWrapper) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = scanResultWrapper.displaySSID, modifier = Modifier.weight(1f))
        Text(
            text = "RSSI: ${scanResultWrapper.scanResult.level} dBm",
            style = MaterialTheme.typography.bodySmall
        )
    }
}



