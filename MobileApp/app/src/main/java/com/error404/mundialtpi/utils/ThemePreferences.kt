package com.error404.mundialtpi.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

class ThemePreferences(private val context: Context) {

    companion object {
        val TEMA_OSCURO_KEY = booleanPreferencesKey("tema_oscuro")
    }

    val temaOscuroFlow: Flow<Boolean> = context.themeDataStore.data.map { prefs ->
        prefs[TEMA_OSCURO_KEY] ?: false
    }

    suspend fun guardarTema(esOscuro: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[TEMA_OSCURO_KEY] = esOscuro
        }
    }
}