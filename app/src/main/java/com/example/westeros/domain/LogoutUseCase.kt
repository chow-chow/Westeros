package com.example.westeros.domain

import com.example.westeros.data.network.auth.AuthService
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val authService: AuthService) {
    operator fun invoke() = authService.logout()
}