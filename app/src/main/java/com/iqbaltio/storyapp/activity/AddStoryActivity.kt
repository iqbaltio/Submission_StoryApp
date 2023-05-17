package com.iqbaltio.storyapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.iqbaltio.storyapp.*
import com.iqbaltio.storyapp.data.ViewModelFactory
import com.iqbaltio.storyapp.databinding.ActivityAddStoryBinding
import com.iqbaltio.storyapp.viewmodel.MainViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddStoryBinding
    private lateinit var photoPath: String
    private lateinit var fusedLoc: FusedLocationProviderClient
    private var getFile: File? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private val mainViewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.addstory_activity)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)

        if (!permissionStatus()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        with(binding) {
            btnAddCamera.setOnClickListener { ambilFoto() }
            btnAddGallery.setOnClickListener { bukaGaleri() }
            buttonAdd.setOnClickListener { uploadStory() }
        }
        getMyLocation()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLoc = LocationServices.getFusedLocationProviderClient(this)
            fusedLoc.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    Toast.makeText(this, "Saved Location", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Tidak ada lokasi", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) getMyLocation()
    }

    private fun permissionStatus() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(photoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.ivAddPreview.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            binding.ivAddPreview.setImageURI(selectedImg)
        }
    }

    private fun bukaGaleri() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun ambilFoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.iqbaltio.storyapp",
                it
            )
            photoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun uploadStory() {
        val file = getFile ?: return Toast.makeText(this, "Pick an Image", Toast.LENGTH_SHORT).show()
        mainViewModel.getUser().observe(this) { it ->
            val token = "Bearer ${it.token}"
            val reqImgFile = reduceImageSize(file).asRequestBody("image/*".toMediaTypeOrNull())
            val desc = "${binding.edAddDescription.text}".toRequestBody("text/plain".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, reqImgFile)
            mainViewModel.addStory(token, imageMultipart, desc, lat, lon).observe(this@AddStoryActivity) {
                when (it) {
                    is Result.Success -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is Result.Loading -> {
                        Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        Toast.makeText(this, "${it.error}\nUpload Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}