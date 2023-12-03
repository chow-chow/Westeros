package com.example.westeros.ui.characters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.westeros.databinding.FragmentCharactersBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "CharactersFragment"

@AndroidEntryPoint
class CharactersFragment : Fragment() {

    private val modelView: CharactersViewModel by viewModels()

    private var _binding: FragmentCharactersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharactersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = modelView
            charactersRecyclerView.adapter = CharacterAdapter { character ->
                Log.d(TAG, "onViewCreated: $character")
                // show character details fragment
                val action = CharactersFragmentDirections
                    .actionCharactersFragmentToCharacterDetailsFragment(
                        characterId = character.id
                    )
                view.findNavController().navigate(action)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}