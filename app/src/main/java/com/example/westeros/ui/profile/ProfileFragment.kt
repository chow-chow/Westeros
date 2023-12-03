package com.example.westeros.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.westeros.R
import com.example.westeros.databinding.FragmentProfileBinding
import com.example.westeros.ui.MainActivity
import com.example.westeros.util.bindImage
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ProfileFragment"
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    private var user: FirebaseUser? = null
    private lateinit var signedInMethod: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = profileViewModel.getCurrentUser()
        signedInMethod = profileViewModel.getSignInMethod()
        with(binding) {
            if (signedInMethod == "google.com") {
                tvUsername.text = user?.displayName
                bindImage(imgProfile, user?.photoUrl.toString())
            } else if (user != null && signedInMethod == "password") {
                tvUsername.text = user?.email
                imgProfile.setImageResource(R.drawable.account_circle_136)
            }
            btnLogout.setOnClickListener {
                logout()
            }
        }
    }

    private fun logout() {
        profileViewModel.logout()
        val intent = Intent(context, MainActivity::class.java)
        context?.startActivity(intent)
        activity?.finish()
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