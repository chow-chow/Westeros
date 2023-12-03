package com.example.westeros.ui.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.westeros.domain.CreateAccountUseCase
import com.example.westeros.util.Constants.MAX_EMAIL_LENGTH
import com.example.westeros.util.Constants.MAX_PASSWORD_LENGTH
import com.example.westeros.util.Constants.MIN_EMAIL_LENGTH
import com.example.westeros.util.Constants.MIN_PASSWORD_LENGTH
import com.example.westeros.util.Constants.NO_DIGIT_PATTERN
import com.example.westeros.util.Constants.NO_LOWERCASE_PATTERN
import com.example.westeros.util.Constants.NO_SPECIAL_CHARACTER_PATTERN
import com.example.westeros.util.Constants.NO_UPPERCASE_PATTERN
import com.example.westeros.util.errors.EmailValidationError
import com.example.westeros.util.errors.PasswordValidationError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RegisterStatus { LOADING, ERROR, DONE }
@HiltViewModel
class RegisterViewModel @Inject constructor(
   val createAccountUseCase: CreateAccountUseCase
): ViewModel() {
    private val _registerStatus = MutableLiveData<RegisterStatus>()
    val registerStatus: MutableLiveData<RegisterStatus> = _registerStatus

    private val _emailError = MutableLiveData<EmailValidationError?>()
    val emailError: LiveData<EmailValidationError?> = _emailError

    private val _passwordError = MutableLiveData<PasswordValidationError?>()
    val passwordError: LiveData<PasswordValidationError?> = _passwordError
    fun onRegisterSelected(email: String, password: String) {
        val isValidEmail = isValidEmail(email)
        val isValidPassword = isValidPassword(password)
        if (isValidEmail && isValidPassword) {
            registerUser(email, password)
        }
    }

    private fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            _registerStatus.value = RegisterStatus.LOADING
            val isAccountCreated = createAccountUseCase(email, password)
            if (isAccountCreated) {
                _registerStatus.value = RegisterStatus.DONE
                // show sign in screen with toast
            } else {
                _registerStatus.value = RegisterStatus.ERROR
                // show error dialog
            }
        }
    }

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

        if (email.length < MIN_EMAIL_LENGTH) {
            _emailError.value = EmailValidationError.TOO_SHORT
            return false
        }

        if (email.length > MAX_EMAIL_LENGTH) {
            _emailError.value = EmailValidationError.TOO_LONG
            return false
        }

        resetEmailError()
        return true
    }

    fun isValidPassword(password: String): Boolean {
        if (password.isEmpty()) {
            _passwordError.value = PasswordValidationError.EMPTY
            return false
        }

        if (password.length < MIN_PASSWORD_LENGTH) {
            _passwordError.value = PasswordValidationError.TOO_SHORT
            return false
        }

        if (password.length > MAX_PASSWORD_LENGTH) {
            _passwordError.value = PasswordValidationError.TOO_LONG
            return false
        }

        if (!password.matches(NO_UPPERCASE_PATTERN.toRegex())) {
            _passwordError.value = PasswordValidationError.NO_UPPERCASE
            return false
        }

        if (!password.matches(NO_LOWERCASE_PATTERN.toRegex())) {
            _passwordError.value = PasswordValidationError.NO_LOWERCASE
            return false
        }

        if (!password.matches(NO_SPECIAL_CHARACTER_PATTERN.toRegex())) {
            _passwordError.value = PasswordValidationError.NO_SPECIAL_CHARACTER
            return false
        }

        if (!password.matches(NO_DIGIT_PATTERN.toRegex())) {
            _passwordError.value = PasswordValidationError.NO_DIGIT
            return false
        }

        resetPasswordError()
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
}