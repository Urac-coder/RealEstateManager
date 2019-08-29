package com.openclassrooms.realestatemanager.controllers.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.controllers.fragments.*
import com.openclassrooms.realestatemanager.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import com.openclassrooms.realestatemanager.controllers.fragments.MapViewFragment
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private var mainFrameLayout: Int = R.id.main_activity_frame
    private var mainFrameLayoutTablet: Int = R.id.main_activity_frame_tablet
    private var mainFrameLayoutLeft: Int = R.id.main_activity_frame_left
    private lateinit var toolbar: Toolbar
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureToolbar()
        configureBottomView()
        SharedPref.init(this)

        //INITIALIZE FRAGMENT SHOW DEPENDING RESTART BY MAPFRAGMENT
        if (isTablet(this)) {
            mainFrameLayout =  mainFrameLayoutTablet
            addFragment(MainFragment.newInstance(), mainFrameLayoutLeft)
        } else{
            addFragment(MainFragment.newInstance(), mainFrameLayout)
        }
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    private fun configureToolbar() {
        this.toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = "Acceuil"
        setSupportActionBar(toolbar)
    }

    private fun configureBottomView() {
        activity_main_bottom_navigation.setOnNavigationItemSelectedListener { item -> updateMainFragment(item.itemId) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        displayConnection(menu!!, this, 1)

        if (SharedPref.read(PREF_DEVICE, "EURO")!! == "EURO"){
            menu.getItem(0).icon = ContextCompat.getDrawable(this, R.drawable.ic_euro_symbol_24)
        } else{
            menu.getItem(0).icon = ContextCompat.getDrawable(this, R.drawable.ic_attach_money_24)
        }

        return true
    }

    // ---------------------
    // ACTION
    // ---------------------

    private fun updateMainFragment(integer: Int): Boolean {
        when (integer) {
            R.id.action_list -> {
                displayConnection(menu!!, this, 1)
                if (isTablet(this)){
                    main_activity_frame_tablet.visibility =  View.INVISIBLE
                    replaceFragment(MainFragment.newInstance(), mainFrameLayoutLeft)
                }  else replaceFragment(MainFragment.newInstance(), mainFrameLayout)
            }
            R.id.action_add ->{
                displayConnection(menu!!, this, 1)
                if (isTablet(this)) main_activity_frame_tablet.visibility =  View.VISIBLE
                replaceFragment(AddPropertyFragment.newInstance(), mainFrameLayout)
            }
            R.id.action_map ->{
                displayConnection(menu!!, this, 1)
                if (isTablet(this)) main_activity_frame_tablet.visibility =  View.VISIBLE
                replaceFragment(MapViewFragment.newInstance(), mainFrameLayout)
            }
            R.id.action_simulator -> {
                displayConnection(menu!!, this, 1)
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
            R.id.menu_toolbar_changeDevice -> {
                if (SharedPref.read(PREF_DEVICE, "EURO")!! == "EURO") SharedPref.write(PREF_DEVICE, "USD") else SharedPref.write(PREF_DEVICE, "EURO")
                finish()
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // ---------------------
    // LIFE CYCLE
    // ---------------------

    override fun onResume() {
        super.onResume()
        if (menu != null) displayConnection(menu!!, this, 1)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
