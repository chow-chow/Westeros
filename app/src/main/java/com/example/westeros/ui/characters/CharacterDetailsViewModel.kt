package com.example.westeros.ui.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.westeros.data.model.Character
import com.example.westeros.data.network.api.ThronesApi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

enum class CharacterDetailsStatus { LOADING, ERROR, DONE }

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor() : ViewModel() {
    private val _apiStatus = MutableLiveData<CharacterDetailsStatus>()
    val apiStatus: LiveData<CharacterDetailsStatus> = _apiStatus

    fun selectedCharacter(characterId: Int): Deferred<Character> {
        return viewModelScope.async {
            _apiStatus.value = CharacterDetailsStatus.LOADING
            try {
                val character = ThronesApi.retrofitService.getCharacter(characterId)
                _apiStatus.value = CharacterDetailsStatus.DONE
                return@async character
            } catch (e: Exception) {
                _apiStatus.value = CharacterDetailsStatus.ERROR
                throw e
            }
        }
    }

}