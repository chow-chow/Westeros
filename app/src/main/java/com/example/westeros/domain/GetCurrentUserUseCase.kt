package com.example.westeros.domain

import com.example.westeros.data.network.auth.AuthService
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authenticationService: AuthService
){
    operator fun invoke() = authenticationService.getCurrentUser()
}