package com.openclassrooms.realestatemanager.controllers.activities

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

    /*private val fragment1: Fragment = MainFragment()
    private val fragment2: Fragment = AddPropertyFragment()
    private val fragment3: Fragment = MapViewFragment()
    private val fragment4: Fragment = LoanSimulatorFragment()
    private val fragment5: Fragment = SearchPropertyFragment()

    private val fm = supportFragmentManager
    private var active: Fragment = fragment1*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureToolbar()
        configureBottomView()
        //configureFragment()

        val bundle = intent.extras
        var bundleValue: String = ""
        if (bundle != null) bundleValue = bundle.getString("startParameter", "")

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
        displayConnection(menu!!, this, 0)
        return true
    }

    /*private fun configureFragment(){
        if (isTablet(this)) mainFrameLayout = mainFrameLayoutTablet
        fm.beginTransaction().add(mainFrameLayout, fragment5, "5").hide(fragment5).commit()
        fm.beginTransaction().add(mainFrameLayout, fragment4, "4").hide(fragment4).commit()
        fm.beginTransaction().add(mainFrameLayout, fragment3, "3").hide(fragment3).commit()
        fm.beginTransaction().add(mainFrameLayout, fragment2, "2").hide(fragment2).commit()
        if (isTablet(this)) {
            main_activity_frame_tablet.visibility = View.GONE
            fm.beginTransaction().add(mainFrameLayoutLeft, fragment1, "1").commit()
        } else fm.beginTransaction().add(mainFrameLayout, fragment1, "1").commit()
    }*/

    // ---------------------
    // ACTION
    // ---------------------

    private fun updateMainFragment(integer: Int): Boolean {
        when (integer) {
            R.id.action_list -> {
                displayConnection(menu!!, this, 0)
                if (isTablet(this)){
                    main_activity_frame_tablet.visibility =  View.INVISIBLE
                    replaceFragment(MainFragment.newInstance(), mainFrameLayoutLeft)
                }  else replaceFragment(MainFragment.newInstance(), mainFrameLayout)
            }
            R.id.action_add ->{
                displayConnection(menu!!, this, 0)
                if (isTablet(this)) main_activity_frame_tablet.visibility =  View.VISIBLE
                replaceFragment(AddPropertyFragment.newInstance(), mainFrameLayout)
            }
            R.id.action_map ->{
                displayConnection(menu!!, this, 0)
                if (isTablet(this)) main_activity_frame_tablet.visibility =  View.VISIBLE
                replaceFragment(MapViewFragment.newInstance(), mainFrameLayout)
            }
            R.id.action_simulator -> {
                displayConnection(menu!!, this, 0)
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

    // ---------------------
    // LIFE CYCLE
    // ---------------------

    override fun onResume() {
        super.onResume()
        if (menu != null) displayConnection(menu!!, this, 0)
    }
}
