package com.proteahealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.proteahealth.ui.theme.ProteahealthTheme

class profilesettings : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Settings Repository
        val repository = SettingsRepository(applicationContext)

        setContent {
            // Collect settings states dynamically
            val darkModeEnabled by repository.darkMode.collectAsState(initial = false)
            val fontScale by repository.fontScale.collectAsState(initial = 1.0f)

            // Pass the darkTheme parameter into HealthAppTheme
            ProteahealthTheme(darkTheme = darkModeEnabled) {
                var selectedTab by remember { mutableIntStateOf(0) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                label = { Text("Profiles") },
                                icon = { Text("👤") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                label = { Text("Settings") },
                                icon = { Text("⚙️") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        when (selectedTab) {
                            0 -> ProfileContainerScreen()
                            1 -> SettingsScreen(repository = repository)
                        }
                    }
                }
            }
        }
    }
}