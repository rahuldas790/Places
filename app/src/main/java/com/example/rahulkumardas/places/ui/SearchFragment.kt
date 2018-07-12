package com.example.rahulkumardas.places.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
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
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {


    private val list = ArrayList<Place>()
    private lateinit var adapter: PlacesAdapter
    private var result: Call<JsonObject>? = null
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private var query: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlacesAdapter(activity!!, list)
        recyclerView.adapter = adapter
        initialiseScrollListener()
        recyclerView.addOnScrollListener(scrollListener)
        val editText = view.findViewById<EditText>(R.id.search)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                System.out.print(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                System.out.print(s)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length > 2) {
                    if (result != null)
                        result!!.cancel()
                    list.clear()
                    query = "$s"
                    loadData("")
                }
            }

        })
        return view;
    }

    private fun initialiseScrollListener() {
        scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(pageToken: String, totalItemsCount: Int, view: RecyclerView?) {
                loadData(pageToken)
            }
        }
    }

    private fun loadData(pageToken: String) {
        val api = NetworkUtil.getRestAdapter(activity!!)
        val lat = PreferenceUtil.getLocationLat(activity!!)
        val lng = PreferenceUtil.getLocationLong(activity!!)
        result = api.searchPlaces("$lat,$lng", 5000, query,
                getString(R.string.key), pageToken) as Call<JsonObject>
        result!!.enqueue(object : Callback<JsonObject> {
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
                adapter.setHasMore(scrollListener.hasMore)
                adapter.notifyDataSetChanged()
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
        val userPhotos = Array<String>(1) { "it = $it" }
        if (place.has("photos")) {
            val photos = place.get("photos") as JsonArray
            i = 0
            for (photo in photos) {
                userPhotos[i] = (photo as JsonObject).get("photo_reference").asString
                i++
            }
        }
        list.add(Place(id, name, icon, addr, rating, userPhotos, placeTypes))
    }

}