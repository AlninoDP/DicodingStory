package com.tms.dicodingstory.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tms.dicodingstory.R
import com.tms.dicodingstory.data.Result
import com.tms.dicodingstory.databinding.ActivityRegisterBinding
import com.tms.dicodingstory.ui.ViewModelFactory
import com.tms.dicodingstory.ui.auth.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        playAnimation()

        val viewModelFactory = ViewModelFactory.getInstance(this)
        val registerViewModel by viewModels<RegisterViewModel> { viewModelFactory }

        binding.apply {
            btnGoToLogin.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                // Prevents the creation of multiple instances
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }

            btnSignUp.setOnClickListener {
                val username = nameEditText.text.toString()
                val email = registerEmailForm.getEmail()
                val password = registerPasswordForm.getPassword()

                if (nameEditText.text.toString().isEmpty()) {
                    nameEditText.error = "Name is required"
                    showToast(getString(R.string.empty_name))
                }
                if (!registerEmailForm.checkEmail()) {
                    showToast(getString(R.string.invalid_email))
                } else if (!registerPasswordForm.checkPassword()) {
                    showToast(getString(R.string.invalid_password))
                } else {
                    registerViewModel.registerUser(username, email, password)
                }
            }
        }

        // Observe Response
        registerViewModel.registerResponse.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(result.state)
                is Result.Success -> {
                    val response = result.data
                    if (!response.error) {
                        showToast(response.message)
                    } else {
                        showToast(response.message)
                    }
                }
                is Result.Failure -> showToast(result.throwable.message.toString())

            }
        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgDicodingRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val animationDuration = 500L

        val tvHeaderRegister =
            ObjectAnimator.ofFloat(binding.tvHeaderRegister, View.ALPHA, 1f)
                .setDuration(animationDuration)
        val nameForm = ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 1f)
            .setDuration(animationDuration)
        val emailForm = ObjectAnimator.ofFloat(binding.registerEmailForm, View.ALPHA, 1f)
            .setDuration(animationDuration)
        val passwordForm =
            ObjectAnimator.ofFloat(binding.registerPasswordForm, View.ALPHA, 1f)
                .setDuration(animationDuration)
        val btnSignUp =
            ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(animationDuration)
        val textHasAcc = ObjectAnimator.ofFloat(binding.tvHasAccount, View.ALPHA, 1f)
            .setDuration(animationDuration)
        val btnGoToLogin =
            ObjectAnimator.ofFloat(binding.btnGoToLogin, View.ALPHA, 1f)
                .setDuration(animationDuration)

        val together = AnimatorSet().apply {
            playTogether(nameForm, emailForm, passwordForm)
        }

        AnimatorSet().apply {
            playSequentially(tvHeaderRegister, together, btnSignUp, textHasAcc, btnGoToLogin)
            start()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.registerProgressBar.visibility = View.VISIBLE
            binding.btnSignUp.visibility = View.GONE
        } else {
            binding.registerProgressBar.visibility = View.GONE
            binding.btnSignUp.visibility = View.VISIBLE
        }
    }
}