package com.example.westeros.util

import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.westeros.R
import com.example.westeros.data.model.Character
import com.example.westeros.ui.characters.CharacterAdapter
import com.example.westeros.util.errors.ValidationError
import com.google.android.material.textfield.TextInputLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.example.westeros.ui.characters.CharactersStatus
import java.util.Locale

@BindingAdapter("validationError")
fun bindSetError(view: TextInputLayout, error: ValidationError?) {
    Log.d("BindingAdapter", "bindSetError: $error")
    if (error != null) {
        view.error = view.context.getString(error.errorMessageId)
    } else {
        view.error = null
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Character>?) {
    val adapter = recyclerView.adapter as CharacterAdapter
    // submitList() es un método de ListAdapter que le dice al ListAdapter cómo calcular las diferencias de la lista
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.loading_animation)
            error(R.drawable.broken_image_24)
        }
    }
}

@BindingAdapter("apiStatus")
fun bindStatus(progressBar: ProgressBar, status: CharactersStatus?) {
    when (status) {
        CharactersStatus.LOADING -> {
            progressBar.visibility = ProgressBar.VISIBLE
        }
        CharactersStatus.ERROR -> {
            progressBar.visibility = ProgressBar.GONE
        }
        CharactersStatus.DONE -> {
            progressBar.visibility = ProgressBar.GONE
        }
        else -> {
            progressBar.visibility = ProgressBar.GONE
        }
    }
}

@BindingAdapter("setHouseImg")
fun bindHouseImage(imgView: ImageView, houseName: String?) {
    when (houseName?.split(" ")?.last()?.lowercase(Locale.ROOT)) {
        "stark" -> {
            imgView.setImageResource(R.drawable.stark)
        }
        "lannister" -> {
            imgView.setImageResource(R.drawable.lannister)
        }
        "targaryen" -> {
            imgView.setImageResource(R.drawable.targaryen)
        }
        "baratheon" -> {
            imgView.setImageResource(R.drawable.baratheon)
        }
        "tyrell" -> {
            imgView.setImageResource(R.drawable.tyrell)
        }
        "martell" -> {
            imgView.setImageResource(R.drawable.martell)
        }
        "arryn" -> {
            imgView.setImageResource(R.drawable.arryn)
        }
        "tully" -> {
            imgView.setImageResource(R.drawable.tully)
        }
        "greyjoy" -> {
            imgView.setImageResource(R.drawable.greyjoy)
        }
        "bolton" -> {
            imgView.setImageResource(R.drawable.bolton)
        }
        else -> {
            imgView.setImageResource(R.drawable.westeros_map)
        }
    }
}