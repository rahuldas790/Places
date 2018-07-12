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

class TagRecyclerAdapter(val context: Context, val list: Array<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private final val TAG = TagRecyclerAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(LayoutInflater.from(context).inflate(R.layout.item_tag, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder1: RecyclerView.ViewHolder, position: Int) {
        val holder = holder1 as Holder
        holder.title.text = list[position]
        Log.i(TAG, "tag is "+list[position])
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.tag)
    }


}