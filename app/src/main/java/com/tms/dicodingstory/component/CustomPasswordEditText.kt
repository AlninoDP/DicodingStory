package com.tms.dicodingstory.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.tms.dicodingstory.R
import com.tms.dicodingstory.databinding.CustomPasswordEditTextBinding
import androidx.core.widget.addTextChangedListener


class CustomPasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: CustomPasswordEditTextBinding = CustomPasswordEditTextBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private var isPasswordValid = false

    init {
        binding.passwordEditText.addTextChangedListener(afterTextChanged = { _ ->
            validatePassword()
        })
    }

    fun checkPassword(): Boolean = isPasswordValid

    private fun validatePassword() {
        val password = binding.passwordEditText.text.toString().trim()
        if (password.length < MIN_PASSWORD_LENGTH) {
            binding.passwordInputLayout.error = context.getString(R.string.invalid_password)
            isPasswordValid = false
        } else {
            binding.passwordInputLayout.error = null
            isPasswordValid = true
        }

    }

    fun getPassword(): String = binding.passwordEditText.text.toString().trim()

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}


