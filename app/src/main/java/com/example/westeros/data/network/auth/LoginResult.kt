package com.example.westeros.data.network.auth

sealed class LoginResult {
    data object Error: LoginResult()
    data class Success(
        val verified: Boolean
    ): LoginResult()
}
