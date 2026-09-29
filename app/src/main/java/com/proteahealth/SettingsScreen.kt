package com.proteahealth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.proteahealth.ui.theme.PrimaryTeal
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    repository: SettingsRepository = SettingsRepository(LocalContext.current)
) {
    val scope = rememberCoroutineScope()

    // Collect flow values as Compose state
    val fontSizeScale by repository.fontScale.collectAsState(initial = 1.0f)
    val soundAlertsEnabled by repository.soundAlerts.collectAsState(initial = true)
    val darkModeEnabled by repository.darkMode.collectAsState(initial = false)
    val dataSaverEnabled by repository.dataSaver.collectAsState(initial = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("App Settings", style = MaterialTheme.typography.headlineMedium, color = PrimaryTeal)
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Font Adjuster
        Text("Font Size: ${(fontSizeScale * 100).toInt()}%", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = fontSizeScale,
            onValueChange = { newValue ->
                scope.launch { repository.saveFontScale(newValue) }
            },
            valueRange = 0.8f..1.4f,
            steps = 3
        )

        // Notification Sound Alert
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Alert Notification Sounds", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = soundAlertsEnabled,
                onCheckedChange = { enabled -> scope.launch { repository.saveSoundAlerts(enabled) } }
            )
        }

        // Dark Theme Switch
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = darkModeEnabled,
                onCheckedChange = { enabled -> scope.launch { repository.saveDarkMode(enabled) } }
            )
        }

        // Data Saver Mode
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Data Saver Mode", style = MaterialTheme.typography.titleMedium)
                Text("Reduces mobile data usage for offline caching", style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = dataSaverEnabled,
                onCheckedChange = { enabled -> scope.launch { repository.saveDataSaver(enabled) } }
            )
        }
    }
}