package com.rosyid.storysubmission.ui.story

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rosyid.storysubmission.R
import com.rosyid.storysubmission.data.remote.Result
import com.rosyid.storysubmission.databinding.ActivityAddStoryBinding
import com.rosyid.storysubmission.ui.ViewModelFactory
import com.rosyid.storysubmission.ui.getImageUri
import com.rosyid.storysubmission.ui.home.MainActivity
import com.rosyid.storysubmission.ui.home.MainViewModel
import com.rosyid.storysubmission.ui.reduceFileImage
import com.rosyid.storysubmission.ui.uriToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddStoryBinding
    private val viewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(this) }
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_LONG).show()
                startCamera()
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddStoryBinding.inflate(layoutInflater )
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        supportActionBar?.show()
        supportActionBar?.title = getString(R.string.add_story)

        viewModel.currentImageUri.observe(this) {
            binding.previewImageView.setImageURI(it)
        }
        
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadStory() }
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestLocationPermission()
            } else {
                latitude = null
                longitude = null
            }
        }

    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            binding.previewImageView.setImageURI(uri)
            viewModel.setCurrentImageUri(uri)
        } else {
            binding.previewImageView.setImageResource(R.drawable.baseline_image_24)
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        when {
            allPermissionsGranted() -> {
                viewModel.setCurrentImageUri(getImageUri(this))
                launcherIntentCamera.launch(viewModel.currentImageUri.value!!)
            }
            shouldShowRequestPermissionRationale(REQUIRED_PERMISSION) -> {
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            }
            else -> {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission)
            .setMessage(R.string.permission_message)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            binding.previewImageView.setImageURI(viewModel.currentImageUri.value)
            showImage()
        } else {
            viewModel.setCurrentImageUri(null)
            binding.previewImageView.setImageResource(R.drawable.baseline_image_24)
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: ")
        }
    }

    private fun uploadStory() {
        viewModel.currentImageUri.value?.let { uri ->
            lifecycleScope.launch {
                binding.progressIndicator.visibility = View.VISIBLE

                val imageFile = withContext(Dispatchers.IO) {
                    uriToFile(uri, this@AddStoryActivity).reduceFileImage()
                }

                Log.d("Image File", "showImage: ${imageFile.path}")
                val description = binding.etDesc.text.toString()

                viewModel.uploadStory(imageFile, description, latitude, longitude).observe(this@AddStoryActivity) { result ->
                    when(result) {
                        is Result.Error -> {
                            binding.progressIndicator.visibility = View.GONE
                            Toast.makeText(this@AddStoryActivity, result.error, Toast.LENGTH_SHORT).show()
                        }
                        is Result.Loading -> {
                            binding.progressIndicator.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressIndicator.visibility = View.GONE
                            if (result.data.error == true) {
                                Toast.makeText(this@AddStoryActivity, result.data.message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@AddStoryActivity, result.data.message, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }

                    }
                }
            }
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLastKnownLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showLocationPermissionRationaleDialog()
            }
            else -> {
                requestPermissionLauncherForLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private val requestPermissionLauncherForLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_LONG).show()
                getLastKnownLocation()
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
            }
        }

    private fun showLocationPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission)
            .setMessage(R.string.location_permission_message)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    Log.d("AddStoryActivity", "Location fetched: lat=$latitude, lon=$longitude")
                } else {
                    Log.d("AddStoryActivity", "Location is null")
                }
            }
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}