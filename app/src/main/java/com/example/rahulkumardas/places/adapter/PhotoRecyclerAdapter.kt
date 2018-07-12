package com.example.rahulkumardas.places.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rahulkumardas.places.R
import com.example.rahulkumardas.places.RestAdapterAPI
import com.example.rahulkumardas.places.ui.DetailsActivity
import kotlinx.android.synthetic.main.activity_details.*

class PhotoRecyclerAdapter(val context: Context, val list: Array<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private final val TAG = PhotoRecyclerAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder1: RecyclerView.ViewHolder, position: Int) {
        val holder = holder1 as Holder
        val placeUrl = RestAdapterAPI.END_POINT +
                RestAdapterAPI.PLACE_PHOTO_URL_LARGE +
                "&photoreference=${list[position]}&key=${context.getString(R.string.key)}"
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.mipmap.image_break)
        requestOptions.error(R.mipmap.image_break)
        Log.i(TAG, placeUrl)
        Glide.with(context)
                .load(placeUrl)
                .apply(requestOptions)
                .into(holder.imageView)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.image)
    }


}