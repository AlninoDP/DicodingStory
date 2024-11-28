package com.tms.dicodingstory.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.tms.dicodingstory.R
import com.tms.dicodingstory.data.Result
import com.tms.dicodingstory.databinding.ActivityAddStoryBinding
import com.tms.dicodingstory.ui.ViewModelFactory
import com.tms.dicodingstory.ui.home.HomeActivity
import com.tms.dicodingstory.utils.getImageUri
import com.tms.dicodingstory.utils.reduceFileImage
import com.tms.dicodingstory.utils.uriToFile

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    private lateinit var addStoryViewModel: AddStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val viewModelFactory = ViewModelFactory.getInstance(this)
        addStoryViewModel = viewModels<AddStoryViewModel> { viewModelFactory }.value

        requestPermissionsIfNeeded()
        showImage()

        addStoryViewModel.postStoryResponse.observe(this@AddStoryActivity){ result ->
            when(result){
                is Result.Loading -> showLoading(result.state)
                is Result.Success -> {
                    val response = result.data
                    if (!response.error) {
                        showToast(response.message)
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        showToast(response.message)
                    }
                }
                is Result.Failure -> showToast(result.throwable.message.toString())
            }
        }


        binding.apply {
            addStoryEdtDescription.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                addStoryViewModel.updateDescriptionText(text.toString())
            })

            addStoryGalleryBtn.setOnClickListener {
                startGallery()
            }

            addStoryCameraBtn.setOnClickListener {
                startCamera()
            }

            addGoalBtnUpload.setOnClickListener {
                val imageUri = addStoryViewModel.imageUri.value
                val description = addStoryViewModel.descriptionText.value

                imageUri?.let {
                    description?.let {
                        val imageFile = uriToFile(imageUri, this@AddStoryActivity).reduceFileImage()
                        addStoryViewModel.uploadStory(imageFile, description)
                    } ?: showToast(getString(R.string.empty_description))
                } ?: showToast(getString(R.string.empty_image))

            }

        }

    }

    // Camera
    private fun startCamera() {
        addStoryViewModel.setImageUri(getImageUri(this@AddStoryActivity))
        addStoryViewModel.imageUri.value?.let { launcherIntentCamera.launch(it) } ?: {
            showToast("Uh Oh Something Went Wrong")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            // Reset to placeholder if no image is captured
            addStoryViewModel.setImageUri(null)
            binding.addStoryImage.setImageResource(R.drawable.image_placeholder)
        }
    }

    // Gallery
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                addStoryViewModel.setImageUri(uri)
            } else {
                Log.d("Photo Picker", "No Media Selected")
            }
        }

    private fun showImage() {
        addStoryViewModel.imageUri.observe(this@AddStoryActivity) { uri ->
            uri?.let {
                binding.addStoryImage.setImageURI(it)
            }
        }
    }

    /// Request Permission
    private fun requestPermissionsIfNeeded() {
        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this@AddStoryActivity,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val message =
                if (isGranted) getString(R.string.permission_granted) else getString(R.string.permission_denied)
            showToast(message)
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.addStoryProgressBar.visibility = View.VISIBLE
            binding.addGoalBtnUpload.visibility = View.GONE
        } else {
            binding.addStoryProgressBar.visibility = View.GONE
            binding.addGoalBtnUpload.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

}