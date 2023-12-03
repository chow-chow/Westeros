package com.example.westeros.ui.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.westeros.data.model.Character
import com.example.westeros.data.network.api.ThronesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CharactersStatus { LOADING, ERROR, DONE }
@HiltViewModel
class CharactersViewModel @Inject constructor() : ViewModel() {
    private val _status = MutableLiveData<CharactersStatus>()
    val status: LiveData<CharactersStatus> = _status

    private val _characters = MutableLiveData<List<Character>>()
    val characters: LiveData<List<Character>> = _characters

    init {
        getCharacters()
    }

    private fun getCharacters() {
        viewModelScope.launch {
            _status.value = CharactersStatus.LOADING
            try {
                _characters.value = ThronesApi.retrofitService.getCharacters()
                _status.value = CharactersStatus.DONE
            } catch (e: Exception) {
                _status.value = CharactersStatus.ERROR
                _characters.value = listOf()
            }
        }
    }
}