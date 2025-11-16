package com.example.calculator

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LocationActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var tvLat: TextView
    private lateinit var tvLon: TextView
    private lateinit var tvAlt: TextView
    private lateinit var tvTime: TextView

    private val PERMISSION_ID = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        tvLat = findViewById(R.id.tv_lat)
        tvLon = findViewById(R.id.tv_lon)
        tvAlt = findViewById(R.id.tv_alt)
        tvTime = findViewById(R.id.tv_time)

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finish()
        }

        getLastLocation()
    }

    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                if (
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) return

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L,
                    1f,
                    this
                )

                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastLocation != null) {
                    updateUI(lastLocation)
                    saveToJson(lastLocation)
                }

            } else {
                Toast.makeText(this, "Включите геолокацию", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermissions()
        }
    }

    private fun updateUI(loc: Location) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        tvLat.text = "Широта: ${loc.latitude}"
        tvLon.text = "Долгота: ${loc.longitude}"
        tvAlt.text = "Высота: ${loc.altitude}"
        tvTime.text = "Время: $time"
    }

    private fun saveToJson(loc: Location) {
        val json = JSONObject()
        json.put("latitude", loc.latitude)
        json.put("longitude", loc.longitude)
        json.put("altitude", loc.altitude)
        json.put("time", System.currentTimeMillis())

        val file = File(filesDir, "location_log.json")
        file.appendText(json.toString() + "\n")
    }

    override fun onLocationChanged(location: Location) {
        updateUI(location)
        saveToJson(location)
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}