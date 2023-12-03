package com.example.westeros.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.westeros.R
import com.example.westeros.databinding.FragmentLoginBinding
import com.example.westeros.ui.AppActivity
import com.example.westeros.util.DialogUtils.showErrorDialog
import com.example.westeros.util.ToastUtils.showToastLong
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "LoginFragment"
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var launcher : ActivityResultLauncher<IntentSenderRequest>

    private var user: FirebaseUser? = null

    private lateinit var signedInMethod: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = loginViewModel
            loginFragment = this@LoginFragment
        }

        user = loginViewModel.getCurrentUser()
        signedInMethod = loginViewModel.getSignInMethod()

        checkCurrentUser()
        initListeners()
        observeLoginStatus()
        startIntentSender()
        observeIntentSender()
    }

    private fun checkCurrentUser() {
        if (user != null) {
            goToApplication()
            showLoginToast(context)
        }
    }

    private fun initListeners() {
        with(binding) {
            tfEmail.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    loginViewModel.isValidEmail(tfEmail.text.toString())
                }
            }
            tfEmail.addTextChangedListener(createTextWatcher(tfEmail) {
                loginViewModel.resetEmailError()
            })

            tfPassword.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    loginViewModel.isValidPassword(tfPassword.text.toString())
                }
            }
            tfPassword.addTextChangedListener(createTextWatcher(tfPassword) {
                loginViewModel.resetPasswordError()
            })

            btnLogin.setOnClickListener {
                loginViewModel.onLoginSelected(
                    tfEmail.text.toString(),
                    tfPassword.text.toString()
                )
            }

            btnGoogleLogin.setOnClickListener {
                loginViewModel.onLoginWithGoogleSelected()
            }
        }
    }

    private fun createTextWatcher(
        textField: View,
        afterTextChangedAction: () -> Unit
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                afterTextChangedAction.invoke()
            }
        }
    }

    private fun observeLoginStatus() {
        loginViewModel.loginStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                LoginStatus.DONE -> {
                    context?.let {
                        showLoginToast(it)
                    }
                    goToApplication()
                }
                LoginStatus.ERROR -> context?.let {
                    showErrorDialog(
                        it,
                        it.getString(R.string.incorrect_data),
                        it.getString(R.string.incorrect_data_message)
                    )
                }
                else -> {}
            }
        }
    }

    private fun showLoginToast(context: Context?) {
        // Actualizamos el usuario y el método de inicio de sesión
        user = loginViewModel.getCurrentUser()
        signedInMethod = loginViewModel.getSignInMethod()
        context?.let {
            val username = if (signedInMethod == "google.com") {
                user?.displayName.toString()
            } else {
                user?.email.toString()
            }
            showToastLong(
                it,
                it.getString(
                    R.string.sign_in_success,
                    username
                )
            )
        }
    }
    private fun startIntentSender() {
        launcher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {result ->
            if (result.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch {
                    val intent = result.data ?: return@launch
                    loginViewModel.loginFromGoogleIntent(intent)
                }
            }
        }
    }

    private fun observeIntentSender() {
        loginViewModel.googleLoginIntentSender.observe(viewLifecycleOwner) { intentSender ->
            intentSender?.let {
                launcher.launch(
                    IntentSenderRequest.Builder(it).build()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Action bar hidden")
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    /*
     * Navigate to register fragment
     */
    fun goToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    /*
     * Navigate to application
     */
    private fun goToApplication() {
        val intent = Intent(context, AppActivity::class.java)
        context?.startActivity(intent)
        activity?.finish()
    }
}