package com.stone.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder.*
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.moshi.Moshi


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
    }

    private fun checkPermission() {
        Dexter.withContext(this@MainActivity)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Log.i("MainActivity.onCreate", "permissionGranted")
                    getLocation()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    Log.i("MainActivity.onCreate", "PermissionShow")
                    p1?.continuePermissionRequest()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Log.i("MainActivity.onCreate", "PermissionDenied")
                    if (p0?.isPermanentlyDenied == true)
                        showSettingsDialog()
                }

            })
            .check()
    }

    private fun showSettingsDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS"
        ) { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    @SuppressLint("MissingPermission")
    fun getLocation(){
        val locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        Log.d("Mainactivity.oncreate", location?.latitude.toString())

        Toast.makeText(this, location?.latitude.toString(), Toast.LENGTH_LONG).show()

        val response="{\"coord\":{\"lon\":30,\"lat\":20},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01n\"}],\"base\":\"stations\",\"main\":{\"temp\":288.91,\"feels_like\":281.95,\"temp_min\":288.91,\"temp_max\":288.91,\"pressure\":1021,\"humidity\":33,\"sea_level\":1021,\"grnd_level\":986},\"visibility\":10000,\"wind\":{\"speed\":7.01,\"deg\":7},\"clouds\":{\"all\":0},\"dt\":1611162344,\"sys\":{\"country\":\"SD\",\"sunrise\":1611117459,\"sunset\":1611157442},\"timezone\":7200,\"id\":372801,\"name\":\"Karmah an Nuzul\",\"cod\":200}"

        val moshi=Moshi.Builder().build()
        val adapter=moshi.adapter(OpenWeatherMapResponse::class.java)

        val responseWeather=adapter.fromJson(response)
        Log.i("MainActivity", responseWeather.toString())
    }


}