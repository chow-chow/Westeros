package com.example.westeros.util.errors

import android.content.Context
import com.example.westeros.R
import com.example.westeros.util.Constants.MAX_PASSWORD_LENGTH
import com.example.westeros.util.Constants.MIN_PASSWORD_LENGTH

enum class PasswordValidationError : ValidationError {
    EMPTY {
        override val errorMessageId: Int = R.string.error_password_required
    },
    TOO_LONG {
        override val errorMessageId: Int = R.string.error_password_too_long
    },
    TOO_SHORT {
        override val errorMessageId: Int = R.string.error_password_too_short
    },
    NO_DIGIT {
        override val errorMessageId: Int = R.string.error_password_no_digit
    },
    NO_UPPERCASE {
        override val errorMessageId: Int = R.string.error_password_no_uppercase
    },
    NO_LOWERCASE {
        override val errorMessageId: Int = R.string.error_password_no_lowercase
    },
    NO_SPECIAL_CHARACTER {
        override val errorMessageId: Int = R.string.error_password_no_special
    }
}