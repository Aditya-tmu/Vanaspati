package com.vanaspati.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vanaspati.data.PreferencesStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val store = remember { PreferencesStore(ctx) }
    val scope = rememberCoroutineScope()

    val dark by store.darkModeFlow.collectAsState(initial = false)
    val language by store.languageFlow.collectAsState(initial = "en")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
            Switch(checked = dark, onCheckedChange = { checked ->
                scope.launch { store.setDarkMode(checked) }
            })

            Text("Language", style = MaterialTheme.typography.titleMedium)
            Row {
                RadioButton(
                    selected = language == "en",
                    onClick = { scope.launch { store.setLanguage("en") } }
                )
                Text(
                    text = "English",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                        .selectable(
                            selected = language == "en",
                            onClick = { scope.launch { store.setLanguage("en") } }
                        )
                )
            }
            Row {
                RadioButton(
                    selected = language == "hi",
                    onClick = { scope.launch { store.setLanguage("hi") } }
                )
                Text(
                    text = "Hindi",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                        .selectable(
                            selected = language == "hi",
                            onClick = { scope.launch { store.setLanguage("hi") } }
                        )
                )
            }
        }
    }
}