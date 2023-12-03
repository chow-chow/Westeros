package com.example.westeros.ui.profile

import androidx.lifecycle.ViewModel
import com.example.westeros.domain.GetCurrentUserUseCase
import com.example.westeros.domain.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val getCurrentUserUseCase: GetCurrentUserUseCase,
    val logoutUseCase: LogoutUseCase
) : ViewModel() {
    fun getCurrentUser() = getCurrentUserUseCase()
    fun getSignInMethod(): String {
        return getCurrentUser()?.providerData?.get(1)?.providerId ?: ""
    }
    fun logout() = logoutUseCase()
}