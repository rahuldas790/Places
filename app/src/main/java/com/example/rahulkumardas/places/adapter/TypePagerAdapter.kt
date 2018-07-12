package com.example.rahulkumardas.places.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.rahulkumardas.places.ui.TypeFragment


class TypePagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    val type_code = arrayOf("bar","bakery","meal_delivery","meal_takeaway",
            "cafe", "museum", "night_club", "restaurant", "spa")

    val type = arrayOf("Bar","Bakery","Meal Delivery","Meal Takeaway",
            "Cafe", "Museum", "Night Club", "Restaurant", "Spa")

    override fun getItem(position: Int): Fragment {
        return TypeFragment.getInstance(type_code[position])
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return type[position]
    }

    override fun getCount(): Int {
        return type_code.size;
    }
}