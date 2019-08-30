package com.openclassrooms.realestatemanager.controllers.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.view.PropertyViewModel
import kotlinx.android.synthetic.main.fragment_add_property.*
import android.content.ClipData.Item
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.view.adapter.MainFragmentAdapter
import kotlinx.android.synthetic.main.fragment_main.*
import android.content.Intent
import android.R.attr.key
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_display_property.*
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE
import android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.widget.Toolbar
import android.os.Build
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.openclassrooms.realestatemanager.utils.*

class MainFragment : BaseFragment(){

    lateinit var adapter: MainFragmentAdapter

    companion object {
        fun newInstance() : MainFragment{
            return MainFragment()
        }
    }

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_main
    }

    override fun addToOnCreateView(rootView: View, savedInstanceState: Bundle?) {}

    override fun addToOnViewCreated() {
        setToolbarTitle(activity!!, "Accueil")
        getAllProperty()
        configureRecyclerView()
        configureOnClickRecyclerView()
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    private fun configureRecyclerView() {
        this.adapter = MainFragmentAdapter()
        this.main_fragment_recyclerView.adapter = this.adapter
        this.main_fragment_recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun configureOnClickRecyclerView() {
        ItemClickSupport.addTo(main_fragment_recyclerView, R.layout.fragment_main)
                .setOnItemClickListener { recyclerView, position, v ->
                    val response = adapter.getProperty(position)
                    launchDisplayPropertyFragment(response.id.toString().toLong())
                }
    }

    private fun updatePropertyList(properties: List<Property>) {
        if (!properties.isEmpty()) {
            try {
                main_fragment_addInfo.visibility  = View.GONE
            } catch (e: Exception){}
            this.adapter.updateData(properties)
        } else {
            main_fragment_addInfo.visibility = View.VISIBLE
        }
    }

    // ---------------------
    // UTILS
    // ---------------------

    private fun getAllProperty() {
        this.propertyViewModel.getAllProperty().observe(this, Observer<List<Property>> {propertyList  -> updatePropertyList(propertyList) })
    }

    private fun launchDisplayPropertyFragment(propertyId: Long) {
        var frameLayout: Int = R.id.main_activity_frame
        if (isTablet(context!!)) frameLayout = R.id.main_activity_frame_right
        if(isLandscape(context!!) && !isTablet(context!!)){ frameLayout = R.id.main_activity_frame_land }

        val fragment = DisplayPropertyFragment()
        val bundle = Bundle()
        bundle.putLong(BUNDLE_PROPERTY_ID, propertyId)
        fragment.arguments = bundle
            activity!!.supportFragmentManager.beginTransaction()
                    .replace(frameLayout, fragment, "findThisFragment")
                    .addToBackStack(null)
                    .commit()
    }
}