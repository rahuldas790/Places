package com.example.rahulkumardas.places.ui

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rahulkumardas.places.R
import com.example.rahulkumardas.places.adapter.TypePagerAdapter


class ExploreFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_explore, null)

        val viewPage = view.findViewById<ViewPager>(R.id.view_pager)
        viewPage.adapter = TypePagerAdapter(childFragmentManager)
        view.findViewById<TabLayout>(R.id.tabs).setupWithViewPager(viewPage)
        return view;
    }

}