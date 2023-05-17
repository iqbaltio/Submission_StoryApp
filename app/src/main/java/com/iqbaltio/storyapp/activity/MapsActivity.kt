package com.iqbaltio.storyapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.iqbaltio.storyapp.R
import com.iqbaltio.storyapp.data.ViewModelFactory
import com.iqbaltio.storyapp.databinding.ActivityMapsBinding
import com.iqbaltio.storyapp.viewmodel.MainViewModel
import com.iqbaltio.storyapp.Result
import com.iqbaltio.storyapp.data.ListStory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mainViewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.mapstory_activity)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setDatamaps()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setDatamaps() {
        mainViewModel.getUser().observe(this){ it ->
            val token = "Bearer ${it.token}"
            mainViewModel.getStoryLocation(token).observe(this){
                when(it){
                    is Result.Loading -> {}
                    is Result.Success -> showMarker(it.data.ListStory)
                    is Result.Error -> Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showMarker(listStory: List<ListStory>) {
        for (story in listStory) {
            val latlng = LatLng(story.latitude, story.longitude)
            mMap.addMarker(
                MarkerOptions()
                    .position(latlng)
                    .snippet(story.description + " | " + story.createdAt.toString().removeRange(16,story.createdAt.toString().length))
                    .title(story.name)
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        getMyLoc()
        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }



    private fun getMyLoc() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            reqPermissionLoc.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private val reqPermissionLoc =
        registerForActivityResult(ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) { getMyLoc() }
        }

    companion object {
        private const val TAG = "MapsActivity"
    }
}