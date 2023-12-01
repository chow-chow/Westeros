package com.example.westeros.ui.login

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.westeros.data.network.auth.LoginResult
import com.example.westeros.domain.GetCurrentUserUseCase
import com.example.westeros.domain.LoginFromGoogleIntentUseCase
import com.example.westeros.domain.LoginUseCase
import com.example.westeros.domain.LoginWithGoogleUseCase
import com.example.westeros.util.Constants.MAX_PASSWORD_LENGTH
import com.example.westeros.util.Constants.MIN_PASSWORD_LENGTH
import com.example.westeros.util.errors.EmailValidationError
import com.example.westeros.util.errors.PasswordValidationError
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LoginStatus { LOADING, ERROR, DONE }

@HiltViewModel
class LoginViewModel @Inject constructor(
    val loginUseCase: LoginUseCase,
    val getCurrentUserUseCase: GetCurrentUserUseCase,
    val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    val loginFromGoogleIntentUseCase: LoginFromGoogleIntentUseCase
): ViewModel() {
    private val _loginStatus = MutableLiveData<LoginStatus>()
    val loginStatus: LiveData<LoginStatus> = _loginStatus

    private val _googleLoginIntentSender = MutableLiveData<IntentSender?>()
    val googleLoginIntentSender: LiveData<IntentSender?> = _googleLoginIntentSender

    private val _emailError = MutableLiveData<EmailValidationError?>()
    val emailError: LiveData<EmailValidationError?> = _emailError

    private val _passwordError = MutableLiveData<PasswordValidationError?>()
    val passwordError: LiveData<PasswordValidationError?> = _passwordError

    /*
     * En esta función primero verificamos los campos y luego hacemos el login
     */
    fun onLoginSelected(email: String, password: String) {
        val isValidEmail = isValidEmail(email)
        val isValidPassword = isValidPassword(password)
        if (isValidEmail && isValidPassword) {
            loginUser(email, password)
        }
    }

    fun onLoginWithGoogleSelected() {
        viewModelScope.launch {
            _googleLoginIntentSender.value = loginWithGoogleUseCase()
        }
    }

    fun loginFromGoogleIntent(intent: Intent) {
        viewModelScope.launch {
            _loginStatus.value = LoginStatus.LOADING
            when (loginFromGoogleIntentUseCase(intent)) {
                is LoginResult.Error -> {
                    _loginStatus.value = LoginStatus.ERROR
                    // show error dialog
                }
                is LoginResult.Success -> {
                    _loginStatus.value = LoginStatus.DONE
                    // navigate to AppActivity
                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginStatus.value = LoginStatus.LOADING
            when (loginUseCase(email, password)) {
                is LoginResult.Error -> {
                    _loginStatus.value = LoginStatus.ERROR
                    // show invalid email or password dialog
                }
                is LoginResult.Success -> {
                    _loginStatus.value = LoginStatus.DONE
                    // navigate to AppActivity
                }
            }
        }
    }

    /*
     * Validación del password, como es el login solo validamos que no esté vacío
     * y que la longitud esté dentro de los parámetros
     */
    fun isValidPassword(password: String): Boolean {

        if (password.isEmpty()) {
            _passwordError.value = PasswordValidationError.EMPTY
            return false
        }

        if (password.length < MIN_PASSWORD_LENGTH) {
            _passwordError.value = PasswordValidationError.TOO_SHORT
            return false
        }

        if (password.length >= MAX_PASSWORD_LENGTH) {
            _passwordError.value = PasswordValidationError.TOO_LONG
            return false
        }

        resetPasswordError()
        return true
    }


    /*
     * Validación del email, como es el login solo validamos que no esté vacío y
     * que tenga el formato correcto.
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isEmpty()) {
            _emailError.value = EmailValidationError.EMPTY
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d("LoginViewModel", "isValidEmail: $email")
            _emailError.value = EmailValidationError.INVALID
            return false
        }

        resetEmailError()
        return true
    }

    fun resetEmailError() {
        if (_emailError.value != null) {
            _emailError.value = null
        }
    }

    fun resetPasswordError() {
        if (_passwordError.value != null) {
            _passwordError.value = null
        }
    }

    fun getCurrentUser() = getCurrentUserUseCase()

    fun getSignInMethod(): String {
        return getCurrentUser()?.providerData?.get(1)?.providerId ?: ""
    }
}