package com.proteahealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.proteahealth.data.PharmacistHomeScreen
import com.proteahealth.data.SettingsRepository
import com.proteahealth.ui.theme.ProteahealthTheme
import kotlinx.coroutines.launch

class Pharmacydashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = SettingsRepository(applicationContext)

        setContent {
            val darkModeEnabled by repository.darkMode.collectAsState(initial = false)

            ProteahealthTheme(darkTheme = darkModeEnabled) {
                var selectedTab by remember { mutableIntStateOf(0) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                label = { Text("Orders Queue") },
                                icon = { Icon(Icons.Default.LocalPharmacy, contentDescription = null) }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                label = { Text("Settings") },
                                icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        when (selectedTab) {
                            0 -> PharmacistHomeScreen()
                            1 -> SettingsScreen(repository = repository)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PharmacistHomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Orders Queue",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun SettingsScreen(
    repository: SettingsRepository,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val darkModeEnabled by repository.darkMode.collectAsState(initial = false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode")
            Switch(
                checked = darkModeEnabled,
                onCheckedChange = { enabled ->
                    coroutineScope.launch {
                        repository.saveDarkMode(enabled)
                    }
                }
            )
        }
    }
}
