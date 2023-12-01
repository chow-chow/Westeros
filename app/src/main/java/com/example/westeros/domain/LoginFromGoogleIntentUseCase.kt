package com.example.westeros.domain

import android.content.Intent
import com.example.westeros.data.network.auth.AuthService
import com.example.westeros.data.network.auth.LoginResult
import javax.inject.Inject

class LoginFromGoogleIntentUseCase @Inject constructor(private val authService: AuthService) {
    suspend operator fun invoke(intent: Intent?): LoginResult = authService.loginFromGoogleIntent(intent)
}