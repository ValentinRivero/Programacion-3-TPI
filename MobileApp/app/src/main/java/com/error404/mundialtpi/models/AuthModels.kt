package com.error404.mundialtpi.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val mensaje: String? = null
)

@Serializable
data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String
)

@Serializable
data class RegistroResponse(
    val id: Int,
    val nombre: String,
    val email: String
)
