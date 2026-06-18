package com.error404.mundialtpi.utils

import android.util.Patterns

object Validations {
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.isNotBlank() && password.length >= 4
    }

    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length >= 3
    }
}