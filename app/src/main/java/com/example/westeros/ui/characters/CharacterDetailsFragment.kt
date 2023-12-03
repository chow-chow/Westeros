package com.example.westeros.ui.characters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.westeros.databinding.FragmentCharacterDetailsBinding
import kotlinx.coroutines.launch
import com.example.westeros.data.model.Character
import kotlin.properties.Delegates

class CharacterDetailsFragment : Fragment() {

    companion object {
        var CHARACTER_ID = "characterId"
    }

    private val viewModel: CharacterDetailsViewModel by viewModels()

    private var _binding: FragmentCharacterDetailsBinding? = null
    private val binding get() = _binding!!

    private var characterId by Delegates.notNull<Int>()

    private lateinit var selectedCharacter: Character

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            // get character id int from bundle
            characterId = it.getInt(CHARACTER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            selectedCharacter = viewModel.selectedCharacter(characterId).await()
            binding.apply {
                lifecycleOwner = viewLifecycleOwner
                character = selectedCharacter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}