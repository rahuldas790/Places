package com.example.rahulkumardas.places.ui

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.Preference
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.rahulkumardas.places.R
import com.example.rahulkumardas.places.adapter.ViewPagerAdapter
import com.example.rahulkumardas.places.util.PreferenceUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // while interacting with the UI.
        hideSystemUI()
        if (PreferenceUtil.getLocationName(this) == null)
            startActivityForResult(Intent(this, LocationActivity::class.java), 2)
        else
            init()

    }

    override fun onResume() {
        super.onResume()
        if (PreferenceUtil.getLocationName(this) != null)
            location.text = PreferenceUtil.getLocationName(this)
    }

    // This snippet hides the system bars.
    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().statusBarColor = Color.TRANSPARENT
        };
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    // Initialize all layout views
    private fun init() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        view_pager.adapter = adapter
        tab_layout.setupWithViewPager(view_pager)
        val tab1 = LayoutInflater.from(this).inflate(R.layout.item_tab, null)
        tab1.findViewById<ImageView>(R.id.image).setImageResource(R.mipmap.ic_explore)
        tab1.findViewById<TextView>(R.id.title).setText(R.string.explore)
        val tab2 = LayoutInflater.from(this).inflate(R.layout.item_tab, null)
        tab2.findViewById<ImageView>(R.id.image).setImageResource(R.mipmap.ic_search)
        tab2.findViewById<TextView>(R.id.title).setText(R.string.search)
        tab_layout.getTabAt(0)!!.customView = tab1
        tab_layout.getTabAt(1)!!.customView = tab2
        locationCard.setOnClickListener {
            startActivityForResult(Intent(this, LocationActivity::class.java), 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                sendBroadcast(Intent("value_change"))
            } else {
                if (PreferenceUtil.getLocationName(this) == null) {
                    AlertDialog.Builder(this).setTitle("Message")
                            .setMessage("No location selected.\nApp will close now")
                            .setPositiveButton("Ok") { dialog, which ->
                                finish()
                            }.setCancelable(false)
                            .show()
                }
            }
        }

    }
}
