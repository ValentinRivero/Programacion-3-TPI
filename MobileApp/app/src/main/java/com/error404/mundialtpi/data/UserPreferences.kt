package com.error404.mundialtpi.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "configuracion_usuario")

class UserPreferences(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val TEMA_OSCURO_KEY = booleanPreferencesKey("tema_oscuro")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val temaOscuroFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[TEMA_OSCURO_KEY] ?: false
    }

    suspend fun guardarToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun borrarToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    suspend fun guardarPreferenciaTema(esOscuro: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TEMA_OSCURO_KEY] = esOscuro
        }
    }
}