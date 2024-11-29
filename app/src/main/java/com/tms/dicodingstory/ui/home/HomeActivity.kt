package com.tms.dicodingstory.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.tms.dicodingstory.MainActivity
import com.tms.dicodingstory.R
import com.tms.dicodingstory.StoriesAdapter
import com.tms.dicodingstory.data.Result
import com.tms.dicodingstory.databinding.ActivityHomeBinding
import com.tms.dicodingstory.ui.ViewModelFactory
import com.tms.dicodingstory.ui.addstory.AddStoryActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLoading(true)
                homeViewModel.logOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                showLoading(false)
                true
            }
            R.id.action_language -> {
                startActivity(Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setUpAppbar()
        val viewModelFactory = ViewModelFactory.getInstance(this)
        homeViewModel = viewModels<HomeViewModel> { viewModelFactory }.value

        homeViewModel.getStories()
        val adapter = StoriesAdapter()

        homeViewModel.storyList.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(result.state)
                is Result.Success -> {
                    val stories = result.data
                    adapter.submitList(stories)
                    binding.homeRv.adapter = adapter
                    binding.homeRv.layoutManager = LinearLayoutManager(this@HomeActivity)
                }

                is Result.Failure -> {
                    showToast(result.throwable.message.toString())
                }
            }
        }

        binding.homeFab.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

    }

    private fun setUpAppbar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.topAppBar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.homeProgressBar.visibility = View.VISIBLE
            binding.homeRv.visibility = View.GONE
        } else {
            binding.homeProgressBar.visibility = View.GONE
            binding.homeRv.visibility = View.VISIBLE
        }
    }
}