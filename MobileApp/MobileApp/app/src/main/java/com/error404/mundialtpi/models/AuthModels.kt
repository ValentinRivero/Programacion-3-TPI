package com.error404.mundialtpi.models

data class LoginRequest(
    val email: String,
    val contrasena: String
)

data class LoginResponse(
    val token: String,
    val mensaje: String? = null
)

data class RegistroRequest(
    val nombre: String,
    val email: String,
    val contrasena: String
)

data class RegistroResponse(
    val mensaje: String
)
