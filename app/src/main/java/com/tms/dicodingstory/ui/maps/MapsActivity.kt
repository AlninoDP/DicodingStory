package com.tms.dicodingstory.ui.maps

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.tms.dicodingstory.R
import com.tms.dicodingstory.data.Result
import com.tms.dicodingstory.data.remote.response.ListStoryItem
import com.tms.dicodingstory.databinding.ActivityMapsBinding
import com.tms.dicodingstory.ui.ViewModelFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = ViewModelFactory.getInstance(this)
        mapsViewModel = viewModels<MapsViewModel> { viewModelFactory }.value

        mapsViewModel.getStoryWithLocation()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Maps Menu
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        // Set Custom Map Style
        setMapStyle()


        mapsViewModel.storyList.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(result.state)
                is Result.Success -> {
                    val data = result.data
                    addStoryMarkers(data)
                    Log.d("AA","DATANYA $data")
                }

                is Result.Failure -> {
                    showToast(result.throwable.message.toString())
                }
            }

        }


    }

    private fun addStoryMarkers(storyList: List<ListStoryItem?>) {
        storyList.forEach { story ->
            val storyLat = story?.lat?.toDouble() ?: 0.0
            val storyLng = story?.lon?.toDouble() ?: 0.0
            val latLng = LatLng(storyLat, storyLng)
            boundsBuilder.include(latLng)

            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story?.name)
            )
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                200
            )
        )
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e("MapsActivity", "Style Parsing Failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "File Not Found, Error: ", e)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mapsProgressBar.visibility = View.VISIBLE
        } else {
            binding.mapsProgressBar.visibility = View.GONE
        }
    }

}