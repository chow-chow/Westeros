package com.example.westeros.util.errors

import android.content.Context
import com.example.westeros.R

enum class EmailValidationError : ValidationError {
    EMPTY {
        override val errorMessageId: Int = R.string.error_email_required
    },
    INVALID {
        override val errorMessageId: Int = R.string.error_email_invalid
    },
    TOO_LONG {
        override val errorMessageId: Int = R.string.error_email_too_long
    },
    TOO_SHORT {
        override val errorMessageId: Int = R.string.error_email_too_short
    },
}