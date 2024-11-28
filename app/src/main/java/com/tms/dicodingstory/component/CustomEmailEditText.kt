package com.tms.dicodingstory.component

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.tms.dicodingstory.R
import com.tms.dicodingstory.databinding.CustomEmailEditTextBinding

class CustomEmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: CustomEmailEditTextBinding = CustomEmailEditTextBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private var isEmailValid = false

    init {
        binding.emailEditText.addTextChangedListener(afterTextChanged = { _ ->
            validateEmail()
        })
    }

    fun checkEmail(): Boolean = isEmailValid

    private fun validateEmail() {
        val email = binding.emailEditText.text.toString().trim()
        if (email.isEmpty()) {
            binding.emailInputLayout.error = context.getString(R.string.invalid_email)
            isEmailValid = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
            binding.emailInputLayout.error = context.getString(R.string.invalid_email)
            isEmailValid = false
        } else {
            binding.emailInputLayout.error = null
            isEmailValid = true
        }
    }

    fun getEmail(): String = binding.emailEditText.text.toString().trim()




}