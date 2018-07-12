package com.example.rahulkumardas.places.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.rahulkumardas.places.EndlessRecyclerViewScrollListener
import com.example.rahulkumardas.places.R
import com.example.rahulkumardas.places.adapter.PlacesAdapter
import com.example.rahulkumardas.places.model.Place
import com.example.rahulkumardas.places.util.NetworkUtil
import com.example.rahulkumardas.places.util.PreferenceUtil
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response

class TypeFragment : Fragment() {

    private lateinit var type: String
    private val placeList = ArrayList<Place>()
    private lateinit var placeAdapter: PlacesAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

    companion object {

        public fun getInstance(type: String): TypeFragment {
            val typeFragment = TypeFragment()
            val b = Bundle()
            b.putString("type", type)
            typeFragment.arguments = b
            return typeFragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments!!.getString("type")
        activity!!.registerReceiver(receiver, IntentFilter("value_change"))
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_type, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        progressBar = view.findViewById(R.id.progressBar)
        placeAdapter = PlacesAdapter(activity!!, placeList)
        recyclerView.adapter = placeAdapter
        initialiseScrollListener()
        recyclerView.addOnScrollListener(scrollListener)
        loadData("")
        return view
    }

    private fun initialiseScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(pageToken: String, totalItemsCount: Int, view: RecyclerView?) {
                loadData(pageToken)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            placeList.clear()
            progressBar.visibility = View.VISIBLE
            loadData("")
        }
    }


    override fun onDestroy() {
        activity!!.unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun loadData(nextPageToken: String) {
        val api = NetworkUtil.getRestAdapter(activity!!)
        val lat = PreferenceUtil.getLocationLat(activity!!)
        val lng = PreferenceUtil.getLocationLong(activity!!)
        val result = api.getPlaces("$lat,$lng", 5000, type,
                getString(R.string.key), nextPageToken) as Call<JsonObject>
        result.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                //do something here
                t!!.printStackTrace()
            }

            override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
                // do something here
                val body = response!!.body()
                val array = body!!.get("results").asJsonArray
                if (body.has("next_page_token")) {
                    val token = response.body()!!.get("next_page_token").asString
                    scrollListener.setNextPageToken(token)
                }
                for (item in array) {
                    parsePlace(item)
                }
                placeAdapter.setHasMore(scrollListener.hasMore)
                placeAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun parsePlace(item: JsonElement) {
        val place = item as JsonObject
        val id = place.get("place_id").asString
        val addr = place.get("formatted_address").asString
        val icon = place.get("icon").asString
        val name = place.get("name").asString
        val rating: Float
        if (place.has("rating")) {
            rating = place.get("rating").asFloat
        } else {
            rating = 0.0f
        }
        val types = place.get("types") as JsonArray
        val placeTypes = Array<String>(types.size()) { "it = $it" }
        var i = 0
        for (type in types) {
            placeTypes[i] = type.asString
            i++
        }
        val userPhotos = Array<String>(types.size()) { "it = $it" }
        if (place.has("photos")) {
            val photos = place.get("photos") as JsonArray
            i = 0
            for (photo in photos) {
                userPhotos[i] = (photo as JsonObject).get("photo_reference").asString
                i++
            }
        }
        placeList.add(Place(id, name, icon, addr, rating, userPhotos, placeTypes))
    }


}