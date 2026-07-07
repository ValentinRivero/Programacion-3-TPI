package com.error404.mundialtpi.utils

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import org.json.JSONObject

object ErrorHandler {
    fun obtenerMensajeAmigable(e: Exception): String {
        return when (e) {
            is ConnectException -> "No se pudo conectar al servidor. Verificá que esté encendido."
            is SocketTimeoutException -> "El servidor tardó mucho en responder. Intentá de nuevo."
            is IOException -> "Error de red. Comprobá tu conexión a internet."

            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()

                val mensajeApi = try {
                    if (!errorBody.isNullOrEmpty() && errorBody.contains("message")) {
                        JSONObject(errorBody).getString("message")
                    } else null
                } catch (ex: Exception) { null }

                when (e.code()) {
                    401 -> mensajeApi ?: "Email o contraseña incorrectos o sesión expirada."
                    400 -> mensajeApi ?: "Datos inválidos. Revisá la información ingresada."
                    404 -> "Servicio no encontrado."
                    500 -> "Error interno del servidor. Intentá más tarde."
                    else -> "Ocurrió un error inesperado (Código: ${e.code()})."
                }
            }

            else -> "Ocurrió un error desconocido."
        }
    }
}