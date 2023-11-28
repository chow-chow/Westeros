package com.example.westeros.domain

import com.example.westeros.data.network.auth.AuthService
import com.example.westeros.data.network.auth.LoginResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authService: AuthService){
    suspend operator fun invoke(email: String, password: String): LoginResult =
        authService.loginWithEmailAndPassword(email, password)
}