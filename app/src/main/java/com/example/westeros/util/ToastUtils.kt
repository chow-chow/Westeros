package com.example.westeros.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.westeros.R

object ToastUtils {
    fun showToastLong(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}