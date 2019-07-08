package com.openclassrooms.realestatemanager.controllers.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.OneShotPreDrawListener.add
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.controllers.fragments.*
import com.openclassrooms.realestatemanager.utils.addFragment
import com.openclassrooms.realestatemanager.utils.isTablet
import com.openclassrooms.realestatemanager.utils.replaceFragment
import com.openclassrooms.realestatemanager.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_loan_simulator.*

class MainActivity : AppCompatActivity() {

    private var mainFrameLayout: Int = R.id.main_activity_frame
    private var mainFrameLayoutTablet: Int = R.id.main_activity_frame_tablet
    private var mainFrameLayoutLeft: Int = R.id.main_activity_frame_left
    private var mainFrameLayoutRight: Int = R.id.main_activity_frame_right


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        if (isTablet(this)) {
            mainFrameLayout =  mainFrameLayoutTablet
            addFragment(MainFragment.newInstance(), mainFrameLayoutLeft)
        } else{
            addFragment(MainFragment.newInstance(), mainFrameLayout)
        }
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
            R.id.action_list -> {
                if (isTablet(this)){
                    main_activity_frame_tablet.visibility =  View.INVISIBLE
                    replaceFragment(MainFragment.newInstance(), mainFrameLayoutLeft)
                }  else replaceFragment(MainFragment.newInstance(), mainFrameLayout)
            }
            R.id.action_add ->{
                
                if (isTablet(this)) main_activity_frame_tablet.visibility =  View.VISIBLE
                    replaceFragment(AddPropertyFragment.newInstance(), mainFrameLayout)
            }
            R.id.action_map ->{
                if (isTablet(this)) main_activity_frame_tablet.visibility =  View.VISIBLE
                replaceFragment(MapViewFragment.newInstance(), mainFrameLayout)
            }
            R.id.action_simulator -> {
                if (isTablet(this)) main_activity_frame_tablet.visibility =  View.VISIBLE
                replaceFragment(LoanSimulatorFragment(), mainFrameLayout)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_toolbar_search -> {
                if (isTablet(this)) main_activity_frame_tablet.visibility =  View.VISIBLE
                replaceFragment(SearchPropertyFragment(), mainFrameLayout)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
