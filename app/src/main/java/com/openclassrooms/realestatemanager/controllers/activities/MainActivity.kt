package com.openclassrooms.realestatemanager.controllers.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.OneShotPreDrawListener.add
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.controllers.fragments.AddPropertyFragment
import com.openclassrooms.realestatemanager.controllers.fragments.MainFragment
import com.openclassrooms.realestatemanager.utils.addFragment
import com.openclassrooms.realestatemanager.utils.replaceFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val mainFrameLayout: Int = R.id.main_activity_frame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        addFragment(MainFragment.newInstance(), mainFrameLayout)
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    // ---------------------
    // ACTION
    // ---------------------

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_toolbar_add -> replaceFragment(AddPropertyFragment.newInstance(), mainFrameLayout)
        }
        return super.onOptionsItemSelected(item)
    }
}
