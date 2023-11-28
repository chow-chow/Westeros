package com.example.westeros.util

import android.content.Context
import android.widget.Toast

object ToastUtils {
    fun showToastLong(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}