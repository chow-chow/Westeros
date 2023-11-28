package com.example.westeros.util

import android.util.Log
import androidx.databinding.BindingAdapter
import com.example.westeros.util.errors.ValidationError
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("validationError")
fun bindSetError(view: TextInputLayout, error: ValidationError?) {
    Log.d("BindingAdapter", "bindSetError: $error")
    if (error != null) {
        view.error = view.context.getString(error.errorMessageId)
    } else {
        view.error = null
    }
}