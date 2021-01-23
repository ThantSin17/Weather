@file:Suppress("NAME_SHADOWING")

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
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder.*
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {



    private val progressBar by lazy {
        findViewById<ProgressBar>(R.id.progress_bar)
    }
    private val txtCity by lazy {
        findViewById<EditText>(R.id.txt_city)
    }
    private val txtTemp by lazy {
        findViewById<TextView>(R.id.txt_temp)
    }
    private val imgView by lazy {
        findViewById<ImageView>(R.id.imgView)
    }
    private val btnSearch by lazy{
        findViewById<Button>(R.id.btnSearch)
    }
    private val retrofit by lazy {
        RetrofitApiFactory().instance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()

        btnSearch.setOnClickListener {
            val cityName=txtCity.text.toString()
            executeNetworkCall(cityName)
        }

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
        val builder: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton(
            "GOTO SETTINGS"
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
    fun getLocation() {


        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        Log.d("Mainactivity.oncreate", location?.latitude.toString())

        Toast.makeText(this, location?.latitude.toString(), Toast.LENGTH_LONG).show()

        executeNetworkCall(
            latitude = location?.latitude.toString(),
            longitude = location?.longitude.toString()
        )
    }

    private fun showLoading() {
        progressBar.visibility= View.VISIBLE

        txtCity.visibility=View.GONE
        txtTemp.visibility=View.GONE
        imgView.visibility=View.GONE
        btnSearch.visibility=View.GONE

    }
    private fun showUI(city:String,temp:String,weatherIcon:String){
        progressBar.visibility= View.GONE
        txtCity.setText(city)
        txtTemp.text="$temp ℃"
        Glide.with(this).load(weatherIcon).into(imgView)

        btnSearch.visibility=View.VISIBLE
        txtCity.visibility=View.VISIBLE
        txtTemp.visibility=View.VISIBLE
        imgView.visibility=View.VISIBLE
    }

    private fun executeNetworkCall(latitude: String, longitude: String) {
        showLoading()

        val openWeatherMapApi=retrofit.create(OpenWeatherMapApi::class.java)

        openWeatherMapApi.getCoordinate(
            latitude,longitude
        ).enqueue(object :Callback<OpenWeatherMapResponse>{
            override fun onResponse(
                call: Call<OpenWeatherMapResponse>,
                response: Response<OpenWeatherMapResponse>
            ) {
                if (response.isSuccessful){
                    response.body()?.let { response->
                        val iconUrl=response.weather.getOrNull(0)?.icon ?: ""
                        val fullUrl="http://openweathermap.org/img/wn/$iconUrl@2x.png"
                        showUI(response.name,response.main.temp,fullUrl)
                        Log.i("response",response.toString())
                    }
                }
            }

            override fun onFailure(call: Call<OpenWeatherMapResponse>, t: Throwable) {
                Log.i("response",t.message.toString())
            }
        })


    }
    private fun executeNetworkCall(cityName:String) {
        showLoading()

        val openWeatherMapApi=retrofit.create(OpenWeatherMapApi::class.java)

        openWeatherMapApi.getCityByName(
            cityName
        ).enqueue(object :Callback<OpenWeatherMapResponse>{
            override fun onResponse(
                call: Call<OpenWeatherMapResponse>,
                response: Response<OpenWeatherMapResponse>
            ) {
                if (response.isSuccessful){
                    response.body()?.let { response->
                        val iconUrl=response.weather.getOrNull(0)?.icon ?: ""
                        val fullUrl="http://openweathermap.org/img/wn/$iconUrl@2x.png"
                        showUI(response.name,response.main.temp,fullUrl)
                        Log.i("response",response.toString())
                    }
                }
            }

            override fun onFailure(call: Call<OpenWeatherMapResponse>, t: Throwable) {
                Log.i("response",t.message.toString())
            }
        })


    }

}