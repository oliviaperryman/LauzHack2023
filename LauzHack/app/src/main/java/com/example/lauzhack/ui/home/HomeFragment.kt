package com.example.lauzhack.ui.home

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.lauzhack.R
import com.example.lauzhack.databinding.FragmentHomeBinding
import com.google.android.material.chip.Chip
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>


    private var currentLocation: Location? = null
    private var currentLocationURL: String? = null
    lateinit var locationManager: LocationManager
    lateinit var locationByGps: Location
    lateinit var locationByNetwork: Location

    lateinit var last_uri: Uri

    companion object {
        var globalData = mutableListOf<Item>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val uploadButton: Button = binding.upload
        uploadButton.setOnClickListener { v -> this.uploadImg(v) }

        val postButton: Button = binding.post
        postButton.setOnClickListener { v -> this.addItem(v) }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Registers a photo picker activity launcher in single-select mode.
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                binding.imageView.setImageURI(uri)
                last_uri = uri
                binding.description.setText("")
                binding.chipGroup.removeAllViews()
                getLocation()
                binding.editTextTextPostalAddress.setText(currentLocationURL)
                detectObjs(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }


        locationManager = this.requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getLocation(){
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val gpsLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByGps= location
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        val networkLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByNetwork= location
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (isLocationPermissionGranted()) {
            if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    gpsLocationListener
                )
            }

            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    networkLocationListener
                )
            }

            val lastKnownLocationByGps =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastKnownLocationByGps?.let {
                locationByGps = lastKnownLocationByGps
            }

            val lastKnownLocationByNetwork =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocationByNetwork?.let {
                locationByNetwork = lastKnownLocationByNetwork
            }
            if (locationByGps != null && locationByNetwork != null) {
                if (locationByGps.accuracy > locationByNetwork!!.accuracy) {
                    currentLocation = locationByGps
                    val latitude = currentLocation!!.latitude
                    val longitude = currentLocation!!.longitude
                    // use latitude and longitude as per your need
                    currentLocationURL =
                        "https://www.google.com/maps/search/?api=1&query=$latitude%2C$longitude"
                } else {
                    currentLocation = locationByNetwork
                    val latitude = currentLocation!!.latitude
                    val longitude = currentLocation!!.longitude
                    // use latitude and longitude as per your need
                    currentLocationURL =
                        "https://www.google.com/maps/search/?api=1&query=$latitude%2C$longitude"
                }


            }
        }
    }

    fun detectObjs(uri: Uri){
         val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.7f)
            .build()
        val labeler = ImageLabeling.getClient(options)

        val image: InputImage = InputImage.fromFilePath(this.requireContext(), uri)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    val index = label.index

                    val chip = layoutInflater.inflate(R.layout.dynamicchip, binding.chipGroup, false) as Chip
                    chip.text = (text)
                    binding.chipGroup.addView(chip)
                    binding.description.text.append("$text ")
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                Log.e("Error", e.stackTraceToString())
            }

    }

    fun uploadImg(view: View) {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun addItem(view: View){
        binding.chipGroup.checkedChipIds
        val chips = (mutableListOf <String>())
        for (c in binding.chipGroup.checkedChipIds) {
            var chip = binding.chipGroup.findViewById(c) as Chip
            chips.add(chip.text.toString())
        }

        val newItem = Item(
            binding.description.text.toString(),
            currentLocationURL!!,
            chips,
            last_uri
        )
        globalData.add((newItem))
    }


    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            false
        } else {
            true
        }
    }

}