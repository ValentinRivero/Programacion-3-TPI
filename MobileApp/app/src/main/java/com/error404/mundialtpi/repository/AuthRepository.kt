package com.error404.mundialtpi.repository

import com.error404.mundialtpi.models.LoginRequest
import com.error404.mundialtpi.models.LoginResponse
import com.error404.mundialtpi.models.RegistroRequest
import com.error404.mundialtpi.models.RegistroResponse
import com.error404.mundialtpi.network.MundialAPIService
import com.error404.mundialtpi.utils.ErrorHandler
import com.error404.mundialtpi.utils.TokenManager
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val api: MundialAPIService,
    private val tokenManager: TokenManager
) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = api.login(request)
            tokenManager.saveToken(response.token, response.user?.nombre ?: "Usuario")
            Result.success(response)
        } catch (e: Exception) {
            val mensajeAmigable = ErrorHandler.obtenerMensajeAmigable(e)
            Result.failure(Exception(mensajeAmigable))
        }
    }

    suspend fun registro(request: RegistroRequest): Result<RegistroResponse> {
        return try {
            val response = api.registro(request)
            Result.success(response)
        } catch (e: Exception) {
            val mensajeAmigable = ErrorHandler.obtenerMensajeAmigable(e)
            Result.failure(Exception(mensajeAmigable))
        }
    }
    suspend fun guardarToken(tokenEscaneado: String, nombre: String) {
        tokenManager.saveToken(tokenEscaneado, nombre)
    }

    fun isLoggedInFlow(): Flow<Boolean> = tokenManager.isLoggedInFlow

    fun getNombreUsuarioFlow(): Flow<String> = tokenManager.nombreFlow

    suspend fun logout() {
        tokenManager.clearToken()
    }
}