package com.openclassrooms.realestatemanager.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.openclassrooms.realestatemanager.utils.ItemClickSupport
import com.openclassrooms.realestatemanager.utils.toast
import android.R.attr.key

class MainFragment : Fragment(){

    lateinit var adapter: MainFragmentAdapter
    //var propertyList = listOf<Property>()
    lateinit var propertyViewModel: PropertyViewModel

    companion object {
        fun newInstance() : MainFragment{
            return MainFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureViewModel()
        getAllProperty()
        configureRecyclerView()
        configureOnClickRecyclerView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    private fun configureViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(context!!)
        this.propertyViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PropertyViewModel::class.java)
    }

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

    private fun updateItemsList(properties: List<Property>) {
        this.adapter.updateData(properties)
    }

    // ---------------------
    // UTILS
    // ---------------------

    private fun getAllProperty() {
        this.propertyViewModel.getAllProperty().observe(this, Observer<List<Property>> {propertyList  -> updateItemsList(propertyList) })
    }

    private fun launchDisplayPropertyFragment(propertyId: Long) {
        val fragment = DisplayPropertyFragment()
        val bundle = Bundle()
        bundle.putLong("PROPERTY_ID", propertyId)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_frame, fragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
    }
}