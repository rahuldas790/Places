package com.example.rahulkumardas.places.ui

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rahulkumardas.places.GridAutofitLayoutManager
import com.example.rahulkumardas.places.R
import com.example.rahulkumardas.places.R.string.key
import com.example.rahulkumardas.places.RestAdapterAPI
import com.example.rahulkumardas.places.adapter.PhotoRecyclerAdapter
import com.example.rahulkumardas.places.adapter.TagRecyclerAdapter
import com.example.rahulkumardas.places.model.Place
import com.example.rahulkumardas.places.util.NetworkUtil
import com.example.rahulkumardas.places.util.PreferenceUtil
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsActivity : AppCompatActivity() {

    private final val TAG = DetailsActivity::class.java.simpleName
    private lateinit var place: Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val b = intent.extras
        place = b.getParcelable<Place>("place")
        name.text = place.name
        addr.text = place.addr
        if (place.photos.isNotEmpty()) {
            val placeUrl = RestAdapterAPI.END_POINT +
                    RestAdapterAPI.PLACE_PHOTO_URL_LARGE +
                    "&photoreference=${place.photos[0]}&key=${getString(key)}"
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.mipmap.image_break)
            requestOptions.error(R.mipmap.image_break)
            Log.i(TAG, placeUrl)
            Glide.with(this)
                    .load(placeUrl)
                    .apply(requestOptions)
                    .into(image)
        } else {
            Glide.with(this)
                    .load(place.icon)
                    .into(image)
        }

        getDetails()

        tagRecycler.layoutManager = GridLayoutManager(this, 2)
        tagRecycler.adapter = TagRecyclerAdapter(this, place.types)
    }

    private fun getDetails() {
        val api = NetworkUtil.getRestAdapter(this)
        val result = api.getPlaceDetails(place.id, getString(key)) as Call<JsonObject>
        result.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                t!!.printStackTrace()
                photoLayout.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                userPhotos.text = "Failed to load user photos."
            }

            override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
                val result = response!!.body()!!.get("result").asJsonObject


                if (result.has("photos")) {
                    val photos = result.get("photos") as JsonArray
                    val userPhotos = Array<String>(photos.size()) { "it = $it" }
                    var i = 0
                    for (photo in photos) {
                        userPhotos[i] = (photo as JsonObject).get("photo_reference").asString
                        i++
                    }
                    photoRecycler.layoutManager = GridLayoutManager(this@DetailsActivity, 2)
                    val adapter = PhotoRecyclerAdapter(this@DetailsActivity, userPhotos)
                    photoRecycler.adapter = adapter
                    setRecyclerViewHeight(adapter, userPhotos.size)

                }
                progressBar.visibility = View.GONE
                photoLayout.visibility = View.VISIBLE
            }
        })
        back.setOnClickListener { finish() }
    }

    fun setRecyclerViewHeight(adapter: PhotoRecyclerAdapter, size: Int) {
        if (dpsToPx(200) * size > resources.displayMetrics.heightPixels) {
            photoRecycler.getLayoutParams().height = resources.displayMetrics.heightPixels
        } else {
            photoRecycler.getLayoutParams().height = dpsToPx(200) * size
        }
    }

    fun dpsToPx(size: Int): Int {
        return (resources.displayMetrics.density * size).toInt()
    }
}
