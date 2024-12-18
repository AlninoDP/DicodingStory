package com.tms.dicodingstory.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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

        setUpAppbar()
        requestCameraPermissionsIfNeeded()
        showImage()

        addStoryViewModel.postStoryResponse.observe(this@AddStoryActivity) { result ->
            when (result) {
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

            addLocationSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLocation()
                }
            }

            addGoalBtnUpload.setOnClickListener {
                val imageUri = addStoryViewModel.imageUri.value
                val description = addStoryViewModel.descriptionText.value
                val currentLocation = addStoryViewModel.currentLocation.value
                val lat = currentLocation?.latitude?.toFloat()
                val lng = currentLocation?.longitude?.toFloat()

                imageUri?.let {
                    val imageFile = uriToFile(imageUri, this@AddStoryActivity).reduceFileImage()
                    description?.let {
                        if (addLocationSwitch.isChecked) {
                            addStoryViewModel.uploadStory(
                                uriToFile(
                                    imageUri,
                                    this@AddStoryActivity
                                ), description, lat, lng
                            )
                        } else {
                            addStoryViewModel.uploadStory(imageFile, description)
                        }
                    } ?: showToast(getString(R.string.empty_description))
                } ?: showToast(getString(R.string.empty_image))

            }

        }

    }

    private fun getMyLocation() {
        if (LOCATION_PERMISSION.all { checkPermission(it) }) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    addStoryViewModel.updateCurrentLocation(location)
                } else {
                    showToast(getString(R.string.location_not_found))
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(
                LOCATION_PERMISSION
            )
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
    private fun requestCameraPermissionsIfNeeded() {
        if (!checkPermission(CAMERA_PERMISSION)) {
            requestCameraPermissionLauncher.launch(CAMERA_PERMISSION)
        }
    }

    private fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            this@AddStoryActivity,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLocation()
                }

                else -> {
                    showToast(getString(R.string.location_permission_denied))
                }
            }
        }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val message =
                if (isGranted) getString(R.string.camera_permission_granted) else getString(R.string.camera_permission_denied)
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

    private fun setUpAppbar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topAppBar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

        private val LOCATION_PERMISSION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    }

}