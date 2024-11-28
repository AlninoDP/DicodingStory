package com.tms.dicodingstory.ui.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tms.dicodingstory.R
import com.tms.dicodingstory.data.Result
import com.tms.dicodingstory.databinding.ActivityLoginBinding
import com.tms.dicodingstory.ui.ViewModelFactory
import com.tms.dicodingstory.ui.auth.register.RegisterActivity
import com.tms.dicodingstory.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewModelFactory = ViewModelFactory.getInstance(this)
        val loginViewModel by viewModels<LoginViewModel> { viewModelFactory }



        binding.apply {
            btnGoToRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                // Prevents the creation of multiple instances
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }

            btnSignIn.setOnClickListener {
                val email = loginEmailForm.getEmail()
                val password = loginPasswordForm.getPassword()

                if (!loginEmailForm.checkEmail()) {
                    showToast(getString(R.string.invalid_email))
                } else if (!loginPasswordForm.checkPassword()) {
                    showToast(getString(R.string.invalid_password))
                } else {
                    loginViewModel.login(email, password)
                }
            }
        }

        // Observe Response
        loginViewModel.loginResponse.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(result.state)
                is Result.Success -> {
                    val response = result.data
                    if (!response.error) {
//                        showToast(response.message)
                        startActivity(Intent(this, HomeActivity::class.java))
                    } else {
                        showToast(response.message)
                    }
                }
                is Result.Failure -> showToast(result.throwable.message.toString())

            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loginProgressBar.visibility = View.VISIBLE
            binding.btnSignIn.visibility = View.GONE
        } else {
            binding.loginProgressBar.visibility = View.GONE
            binding.btnSignIn.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}