package com.example.westeros.domain

import com.example.westeros.data.network.auth.AuthService
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(private val authService: AuthService) {
    suspend operator fun invoke() = authService.loginWithGoogle()
}