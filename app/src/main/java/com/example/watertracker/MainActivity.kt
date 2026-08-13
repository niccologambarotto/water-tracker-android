package com.example.watertracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val healthManager = HealthConnectManager(this)

        setContent {
            MaterialTheme {
                WaterTrackerScreen(healthManager)
            }
        }
    }
}

@Composable
fun WaterTrackerScreen(healthManager: HealthConnectManager) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var totalTodayMl by remember { mutableStateOf(0.0) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions: Set<String> ->
        if (grantedPermissions.containsAll(healthManager.permissions)) {
            Toast.makeText(context, "Permessi concessi!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!healthManager.hasPermissions()) {
            requestPermissionLauncher.launch(healthManager.permissions)
        } else {
            totalTodayMl = healthManager.getTodayWaterTotalMl()
        }
    }

    fun addWater(ml: Double) {
        coroutineScope.launch {
            try {
                healthManager.logWater(ml)
                totalTodayMl = healthManager.getTodayWaterTotalMl()
                Toast.makeText(context, "+${ml.toInt()} ml registrati!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Errore: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Tracciamento Acqua", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("${totalTodayMl.toInt()} ml registrati oggi", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { addWater(250.0) }) { Text("+250 ml") }
            Button(onClick = { addWater(330.0) }) { Text("+330 ml") }
            Button(onClick = { addWater(500.0) }) { Text("+500 ml") }
        }
    }
}
