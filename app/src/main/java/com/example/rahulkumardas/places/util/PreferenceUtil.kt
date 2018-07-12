package com.example.rahulkumardas.places.util

import android.content.Context
import android.location.Location

public class PreferenceUtil{
    companion object {

        private val name = "login"
        private val locationName = "name"
        private val latitude = "lat"
        private val longitude = "long"

        fun saveLocation(context: Context, locationName: String, latitude:String, longitude:String) {
            val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(this.locationName, locationName)
            editor.putString(this.latitude, latitude)
            editor.putString(this.longitude, longitude)
            editor.apply()
        }

        fun getLocationName(context: Context): String? {
            val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            return prefs.getString(locationName, null)
        }

        fun getLocationLat(context: Context): String {
            val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            return prefs.getString(latitude, null)
        }

        fun getLocationLong(context: Context):String{
            val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            return prefs.getString(longitude, null)
        }

    }
}