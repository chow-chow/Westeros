package com.example.westeros.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.databinding.adapters.TextViewBindingAdapter.AfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.westeros.R
import com.example.westeros.databinding.FragmentLoginBinding
import com.example.westeros.ui.app.AppActivity
import com.example.westeros.util.DialogUtils.showErrorDialog
import com.example.westeros.util.ToastUtils.showToastLong
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()
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
        initListeners()
        observeLoginStatus()
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
                        showToastLong(
                            it,
                            it.getString(
                                R.string.sign_in_success,
                                loginViewModel.getCurrentUser()?.email.toString()
                            )
                        )
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

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
    }
}