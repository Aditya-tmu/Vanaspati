package com.vanaspati.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object PreferencesStoreKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val LANGUAGE = stringPreferencesKey("language")
}

class PreferencesStore(private val context: Context) {
    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs: Preferences ->
        prefs[PreferencesStoreKeys.DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesStoreKeys.DARK_MODE] = enabled
        }
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { prefs: Preferences ->
        prefs[PreferencesStoreKeys.LANGUAGE] ?: "en" // Default to English
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesStoreKeys.LANGUAGE] = language
        }
    }
}