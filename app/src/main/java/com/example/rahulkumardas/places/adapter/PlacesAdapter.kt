package com.example.rahulkumardas.places.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rahulkumardas.places.R
import com.example.rahulkumardas.places.RestAdapterAPI
import com.example.rahulkumardas.places.model.Place
import com.example.rahulkumardas.places.ui.DetailsActivity
import android.support.v4.util.Pair
import android.widget.ProgressBar


public class PlacesAdapter(val context: Context, val list: ArrayList<Place>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private final val TAG = PlacesAdapter::class.java.simpleName
    private var hasMore: Boolean = false
    val key = context.resources.getString(R.string.key)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1)
            return Holder(LayoutInflater.from(context).inflate(R.layout.place_item_list, parent, false))
        else
            return Holder2(LayoutInflater.from(context).inflate(R.layout.layout_loading, parent, false))
    }

    override fun getItemCount(): Int {
        if (hasMore)
            return list.size + 1
        else
            return list.size
    }

    public fun setHasMore(hasMore: Boolean) {
        this.hasMore = hasMore
    }

    override fun onBindViewHolder(holder1: RecyclerView.ViewHolder, position: Int) {
        if (position < list.size) {
            val holder = holder1 as Holder
            val place = list[position]
            holder.title.text = place.name
            holder.desc.text = place.addr
            if (place.photos.isNotEmpty()) {
                val placeUrl = RestAdapterAPI.END_POINT +
                        RestAdapterAPI.PLACE_PHOTO_URL +
                        "&photoreference=${place.photos[0]}&key=${key}"
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.mipmap.image_break)
                requestOptions.error(R.mipmap.image_break)
                Glide.with(context)
                        .load(placeUrl)
                        .apply(requestOptions)
                        .into(holder.icon)
            } else {
                Glide.with(context)
                        .load(place.icon)
                        .into(holder.icon)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position < list.size)
            return 1
        else return 2
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.name)
        val desc = itemView.findViewById<TextView>(R.id.desc)
        val icon = itemView.findViewById<ImageView>(R.id.icon)

        init {
            itemView.setOnClickListener {
                val place = list[adapterPosition]
                val intent = Intent(context, DetailsActivity::class.java)
                val b = Bundle()
                b.putParcelable("place", place)
                intent.putExtras(b)
                beginTransition(intent, itemView, icon, title, desc)
            }
        }
    }

    inner class Holder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
    }

    private fun beginTransition(intent: Intent, vararg views: View) {
        val optionsCompat: ActivityOptionsCompat
        if (SDK_INT >= LOLLIPOP && views != null) {
            val length = views.size
            val pairs = arrayOfNulls<Pair<View, String>>(length)
            var i = 0
            for (view in views) {
                pairs[i] = Pair.create(view, view.transitionName)
                i++
            }
            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, pairs[0], pairs[1], pairs[2])
            context.startActivity(intent, optionsCompat.toBundle())
            return
        }

        context.startActivity(intent)
    }

}