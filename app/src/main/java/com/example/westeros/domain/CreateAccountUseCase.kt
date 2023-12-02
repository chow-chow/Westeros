package com.example.westeros.domain

import com.example.westeros.data.network.auth.AuthService
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val authenticationService: AuthService
) {
    suspend operator fun invoke(email: String, password: String): Boolean {
        return authenticationService.createAccount(email, password) != null
    }
}