package com.example.rahulkumardas.places.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.rahulkumardas.places.R
import com.example.rahulkumardas.places.RestAdapterAPI
import com.example.rahulkumardas.places.util.LocationUtil
import com.example.rahulkumardas.places.util.NetworkUtil
import com.example.rahulkumardas.places.util.PreferenceUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.item_tab.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.math.ln

class LocationActivity : AppCompatActivity(), TextWatcher, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private val CODE_LOCATION = 12
    private val could_not_fetch = "Could not fetch"

    internal var results: MutableList<String> = ArrayList()
    internal var resultIds: MutableList<String> = ArrayList()
    private val BASE = "https://maps.googleapis.com/"
    private val COMPONENT = "country:in"
    private val TYPE = "geocode"
    private var jsonResult: Call<JsonObject>? = null
    private lateinit var adapter: ArrayAdapter<String>

    //for gps locations
    private lateinit var locationManager: LocationManager
    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var result: PendingResult<LocationSettingsResult>? = null
    internal val REQUEST_LOCATION = 199
    private var name:String?=null
    private var lat:String?=null
    private var lng:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        checkPermissionAndStart()

        search.addTextChangedListener(this)

        adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, results)
        listView.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        listView.setOnItemClickListener { parent, view, position, id ->
            val selected = results[position]
            getLatLongFromId(position, selected)
        }
        select.setOnClickListener {
            PreferenceUtil.saveLocation(this@LocationActivity, name!!, lat!!, lng!!)
            val i = Intent()
            setResult(Activity.RESULT_OK, i)
            finish()
        }

        findViewById<View>(R.id.back).setOnClickListener{
            val i = Intent()
            setResult(Activity.RESULT_CANCELED, i)
            finish()
        }

    }

    private fun getLatLongFromId(position: Int, name: String) {
        val dialog = ProgressDialog(this)
        dialog.setMessage("Please wait")
        dialog.show()
        val placeid = resultIds[position]
        val api = NetworkUtil.getRestAdapter(this)
        val result = api.getPlaceDetails(placeid, getString(R.string.key)) as Call<JsonObject>
        result.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                t!!.printStackTrace()
                Toast.makeText(this@LocationActivity, "Failed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
                val result = response!!.body()!!.get("result").asJsonObject
                val geometry = result.get("geometry").asJsonObject
                val location = geometry.get("location").asJsonObject
                val lat = location.get("lat").asString
                val lng = location.get("lng").asString
                PreferenceUtil.saveLocation(this@LocationActivity, name, lat, lng)
                dialog.dismiss()

                val i = Intent()
                setResult(Activity.RESULT_OK, i)
                finish()
            }
        })
    }

    private fun checkPermissionAndStart() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    CODE_LOCATION)
        } else {
            //listen the location
            current.text = getString(R.string.fetching_current_location)
            checkForServices()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //listen the location
                current.text = getString(R.string.fetching_current_location)
                checkForServices()
            } else {
                current.text = could_not_fetch
                Toast.makeText(this, "Location permission not granted!",
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun checkForServices() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager == null) {
            //no manager return
            current.text = could_not_fetch
            Toast.makeText(this, "Location service not available",
                    Toast.LENGTH_SHORT).show()
            return
        }
        val providers = locationManager.getAllProviders()
        if (providers == null) {
            // no location provider return
            current.text = could_not_fetch
            Toast.makeText(this, "Location service not available",
                    Toast.LENGTH_SHORT).show()
            return
        }
        //check for gps provider or network provider
        if (providers!!.contains(LocationManager.GPS_PROVIDER)) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //ask to enable gps
                mGoogleApiClient = GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this).build()
                mGoogleApiClient!!.connect()
            } else {
                getLocation()
            }
        } else if (providers!!.contains(LocationManager.NETWORK_PROVIDER)) {
            getLocation()

        } else {
            current.text = could_not_fetch
        }
    }

    private fun getLocation() {
        current.setText(R.string.fetching_current_location)
        val locationResult = object : LocationUtil.LocationResult() {
            override fun gotLocation(location: Location?) {
                //Got the location!
                if (location != null) {
                    val address = getLocationForLatLng(
                            this@LocationActivity,
                            location.latitude,
                            location.longitude)
                    runOnUiThread {
                        current.text = address
                    }
                } else {
                    runOnUiThread { current.text = could_not_fetch }
                }
            }
        }
        val locationUtil = LocationUtil()
        val `val` = locationUtil.getLocation(locationManager, locationResult)
        if (!`val`) {
            current.text = could_not_fetch
        }
    }

    fun getLocationForLatLng(context: Context, lat: Double, lng: Double): String {
        this.lat = "$lat"
        this.lng = "$lng"
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            //        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName

            name = "$city,$state,$country"
            select.visibility = View.VISIBLE

            return name!!
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }

    }

    private fun getSuggestions(query: String) {
        results.clear()
        adapter.notifyDataSetChanged()
        jsonResult = NetworkUtil.getRestAdapter(this).getSuggestion(getString(R.string.key), COMPONENT, query, TYPE)
        jsonResult!!.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                try {
                    val array = response.body()!!.get("predictions").asJsonArray
                    for (i in 0 until array.size()) {
                        results.add(parseResult(array.get(i)))
                        resultIds.add(parseId(array.get(i)))
                    }
                    adapter.notifyDataSetChanged()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun parseId(jsonElement: JsonElement): String {
        try {
            val `object` = jsonElement.asJsonObject
            return `object`.get("place_id").asString
        } catch (e: Exception) {
            return ""
        }

    }

    private fun parseResult(jsonElement: JsonElement): String {
        try {
            val `object` = jsonElement.asJsonObject
            return `object`.get("description").asString
        } catch (e: Exception) {
            return ""
        }

    }

    override fun afterTextChanged(s: Editable?) {
        //do nothing
        System.out.print(s)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //do nothing
        System.out.print(s)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s!!.length >= 2) {
            if (jsonResult != null && jsonResult!!.isExecuted())
                jsonResult!!.cancel()
            getSuggestions(s.toString())
        }
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.interval = (30 * 1000).toLong()
        mLocationRequest!!.fastestInterval = (5 * 1000).toLong()

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())

        result!!.setResultCallback(ResultCallback { result ->
            val status = result.status
            //final LocationSettingsStates state = result.getLocationSettingsStates();
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                                this@LocationActivity,
                                REQUEST_LOCATION)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }// All location settings are satisfied. The client can initialize location
            // requests here.
            //...
            // Location settings are not satisfied. However, we have no way to fix the
            // settings so we won't show the dialog.
            //...
        })
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOCATION) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // All required changes were successfully made
                    getLocation()
                }
                Activity.RESULT_CANCELED -> {
                    // The user was asked to change settings, but chose not to
                    current.setText(could_not_fetch)
                    Toast.makeText(this@LocationActivity,
                            "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show()
                }
                else -> {
                }
            }
        }
    }
}
