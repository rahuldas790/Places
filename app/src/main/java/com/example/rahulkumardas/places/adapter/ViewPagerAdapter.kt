package com.example.rahulkumardas.places.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.rahulkumardas.places.ui.ExploreFragment
import com.example.rahulkumardas.places.ui.SearchFragment


class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        when (position){
            0 -> return ExploreFragment()
            1 -> return SearchFragment()
        }
        return ExploreFragment()
    }

    override fun getCount(): Int {
        return 2;
    }
}