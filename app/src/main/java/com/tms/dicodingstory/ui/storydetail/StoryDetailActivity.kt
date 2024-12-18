package com.tms.dicodingstory.ui.storydetail

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.tms.dicodingstory.R
import com.tms.dicodingstory.data.local.entity.StoryEntity
import com.tms.dicodingstory.databinding.ActivityStoryDetailBinding
import com.tms.dicodingstory.utils.capitalizeWords
import java.util.Locale

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val story = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_STORY, StoryEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_STORY)
        }

        setUpAppbar()
        story?.let {
            setUpStoryDetail(story)
        } ?: showError()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setUpStoryDetail(story: StoryEntity) {
        binding.apply {
            Glide.with(this@StoryDetailActivity)
                .load(story.photoUrl)
                .into(storyDetailImage)

            val formattedLat =
                story.lat?.let { String.format(Locale.getDefault(), "%.4f", it.toFloat()) } ?: "-"
            val formattedLon =
                story.lon?.let { String.format(Locale.getDefault(), "%.4f", it.toFloat()) } ?: "-"

            storyDetailDescription.text = capitalizeWords(story.description)
            storyDetailUserName.text =
                capitalizeWords(getString(R.string.stories_user_name, story.name))
            storyDetailLatLng.text =
                getString(R.string.story_detail_location_detail, formattedLat, formattedLon)
        }
    }

    private fun setUpAppbar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topAppBar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
    }

    private fun showError() {
        Toast.makeText(this@StoryDetailActivity, "Story not found", Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}