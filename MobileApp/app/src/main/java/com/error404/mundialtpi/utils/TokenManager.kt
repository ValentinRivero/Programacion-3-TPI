package com.error404.mundialtpi.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("mundial_prefs", Context.MODE_PRIVATE)

    //+ nombre parametro
    fun saveToken(token: String, nombre: String) {
        prefs.edit()
            .putString("jwt_token", token)
            .putString("user_nombre", nombre) // Guardamos el nombre
            .apply()
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)

    //fun para recuperar el nombre
    fun getNombre(): String = prefs.getString("user_nombre", "Usuario") ?: "Usuario"

    fun clearToken() {
        prefs.edit().remove("jwt_token").remove("user_nombre").apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null
}