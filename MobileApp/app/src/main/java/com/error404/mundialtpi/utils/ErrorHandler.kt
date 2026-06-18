package com.error404.mundialtpi.utils

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import org.json.JSONObject

object ErrorHandler {
    fun obtenerMensajeAmigable(e: Exception): String {
        return when (e) {
            //server apagado o no internet
            is ConnectException -> "No se pudo conectar al servidor. Verificá que esté encendido."
            is SocketTimeoutException -> "El servidor tardó mucho en responder. Intentá de nuevo."
            is IOException -> "Error de red. Comprobá tu conexión a internet."

            //errores que el http tira
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()

                when (e.code()) {
                    401 -> "Email o contraseña incorrectos."
                    400 -> {
                        //intento de extraer el mensaje de la API si lo hay
                        if (!errorBody.isNullOrEmpty() && errorBody.contains("message")) {
                            try {
                                JSONObject(errorBody).getString("message")
                            } catch (ex: Exception) {
                                "Datos inválidos. Revisá la información ingresada."
                            }
                        } else {
                            "Datos inválidos. Revisá la información ingresada."
                        }
                    }
                    404 -> "Servicio no encontrado."
                    500 -> "Error interno del servidor. Intentá más tarde."
                    else -> "Ocurrió un error inesperado (Código: ${e.code()})."
                }
            }

            //si explota por algo más
            else -> "Ocurrió un error desconocido."
        }
    }
}