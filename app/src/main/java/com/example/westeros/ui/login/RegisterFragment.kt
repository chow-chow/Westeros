package com.example.westeros.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.westeros.R
import com.example.westeros.databinding.FragmentRegisterBinding
import com.example.westeros.ui.app.AppActivity
import com.example.westeros.util.DialogUtils.showErrorDialog
import com.example.westeros.util.ToastUtils.showToastLong
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = registerViewModel
            registerFragment = this@RegisterFragment
        }

        initListeners()
        observeRegisterStatus()
    }

    private fun initListeners() {
        with (binding) {
            tfEmail.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    registerViewModel.isValidEmail(tfEmail.text.toString())
                }
            }
            tfEmail.addTextChangedListener(createTextWatcher(tfEmail) {
                registerViewModel.resetEmailError()
            })
            tfPassword.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    registerViewModel.isValidPassword(tfPassword.text.toString())
                }
            }
            tfPassword.addTextChangedListener(createTextWatcher(tfPassword) {
                registerViewModel.resetPasswordError()
            })
            btnRegister.setOnClickListener {
                registerViewModel.onRegisterSelected(
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

    private fun observeRegisterStatus() {
        registerViewModel.registerStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                RegisterStatus.DONE -> {
                    context?.let {
                        showRegisterToast(it, binding.tfEmail.text.toString())
                    }
                    goToApplication()
                }
                RegisterStatus.ERROR -> {
                    context?.let {
                        showErrorDialog(
                            it,
                            getString(R.string.register_error_title),
                            getString(R.string.register_error_message)
                        )
                    }
                }
                else -> {}
            }
        }
    }

    private fun showRegisterToast(context: Context, username: String) {
        showToastLong(context, getString(R.string.register_success, username))
    }

    private fun goToApplication() {
        val intent = Intent(context, AppActivity::class.java)
        context?.startActivity(intent)
        activity?.finish()
    }
}