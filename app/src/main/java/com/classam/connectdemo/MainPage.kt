package com.classam.connectdemo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Main Page") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("wifi_page") },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Go to WiFi Page")
            }
            Button(
                onClick = { navController.navigate("bluetooth_page") },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Go to Bluetooth Page")
            }
        }
    }
}
