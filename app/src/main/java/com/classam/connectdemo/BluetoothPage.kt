package com.classam.connectdemo

import android.bluetooth.BluetoothAdapter
import android.content.Intent
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
fun BluetoothPage(
    navController: NavController,
    bluetoothViewModel: BluetoothViewModel = viewModel()
) {
    val currentTime by bluetoothViewModel.currentTime.collectAsState()
    val bluetoothDevices by bluetoothViewModel.bluetoothList.collectAsState()

    val context = LocalContext.current
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var isBluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled ?: false) }

    LaunchedEffect(isBluetoothEnabled) {
        if (isBluetoothEnabled) {
            bluetoothViewModel.startUpdatingTime()
            bluetoothViewModel.updateBluetoothDevices(context)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            bluetoothViewModel.stopScanningBluetoothDevices()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Bluetooth Page") },
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
            if (!isBluetoothEnabled) {
                Button(
                    onClick = {
                        context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                        isBluetoothEnabled = bluetoothAdapter?.isEnabled ?: false
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enable Bluetooth")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    items(bluetoothDevices) { device ->
                        DeviceItem(deviceName = device)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceItem(deviceName: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = deviceName, modifier = Modifier.weight(1f))
    }
}




