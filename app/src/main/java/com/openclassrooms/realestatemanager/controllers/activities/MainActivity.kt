package com.openclassrooms.realestatemanager.controllers.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.OneShotPreDrawListener.add
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.controllers.fragments.AddPropertyFragment
import com.openclassrooms.realestatemanager.controllers.fragments.MainFragment
import com.openclassrooms.realestatemanager.controllers.fragments.MapViewFragment
import com.openclassrooms.realestatemanager.utils.addFragment
import com.openclassrooms.realestatemanager.utils.replaceFragment
import com.openclassrooms.realestatemanager.utils.toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mainFrameLayout: Int = R.id.main_activity_frame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        addFragment(MainFragment.newInstance(), mainFrameLayout)
        configureBottomView()
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    private fun configureBottomView() {
        activity_main_bottom_navigation.setOnNavigationItemSelectedListener { item -> updateMainFragment(item.itemId) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    // ---------------------
    // ACTION
    // ---------------------

    private fun updateMainFragment(integer: Int): Boolean {
        when (integer) {
            R.id.action_list -> replaceFragment(MainFragment.newInstance(), mainFrameLayout)
            R.id.action_add -> replaceFragment(AddPropertyFragment.newInstance(), mainFrameLayout)
            R.id.action_map -> replaceFragment(MapViewFragment.newInstance(), mainFrameLayout)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_toolbar_search -> applicationContext.toast("search")
        }
        return super.onOptionsItemSelected(item)
    }
}
