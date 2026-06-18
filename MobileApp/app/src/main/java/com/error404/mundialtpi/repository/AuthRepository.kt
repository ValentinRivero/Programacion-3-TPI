package com.error404.mundialtpi.repository

import com.error404.mundialtpi.models.LoginRequest
import com.error404.mundialtpi.models.LoginResponse
import com.error404.mundialtpi.models.RegistroRequest
import com.error404.mundialtpi.models.RegistroResponse
import com.error404.mundialtpi.network.MundialAPIService
import com.error404.mundialtpi.utils.TokenManager

class AuthRepository(
    private val api: MundialAPIService,
    private val tokenManager: TokenManager
) {
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = api.login(request)
            tokenManager.saveToken(response.token)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registro(request: RegistroRequest): Result<RegistroResponse> {
        return try {
            val response = api.registro(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    fun logout() {
        tokenManager.clearToken()
    }
}
