package com.tms.dicodingstory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tms.dicodingstory.ui.ViewModelFactory
import com.tms.dicodingstory.ui.auth.login.LoginActivity
import com.tms.dicodingstory.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewModelFactory = ViewModelFactory.getInstance(this)
        val mainViewModel by viewModels<MainViewModel> { viewModelFactory }

        mainViewModel.getUserToken().observe(this) {user ->
            Log.d("MainActivity", "Token LOG: ${user.token}")
            if(user.token.isNotEmpty() ){
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


    }
}