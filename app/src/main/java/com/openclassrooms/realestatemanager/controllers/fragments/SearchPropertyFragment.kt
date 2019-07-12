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
import kotlinx.android.synthetic.main.fragment_search_property.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.utils.setToolbarTitle
import java.util.*


class SearchPropertyFragment: Fragment(){

    var queryString = String()
    var args: List<Objects> = ArrayList()

    var iterator: Boolean = true
    lateinit var propertyViewModel: PropertyViewModel

    companion object {
        fun newInstance(): SearchPropertyFragment{
            return SearchPropertyFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.openclassrooms.realestatemanager.R.layout.fragment_search_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureViewModel()
        setToolbarTitle(activity!!, "Recherche")

        search_property_btn.setOnClickListener {
            searchProperty()
        }
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    private fun configureViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(context!!)
        this.propertyViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PropertyViewModel::class.java)
    }

    // ---------------------
    // UTILS
    // ---------------------

    private fun searchProperty(){
        this.propertyViewModel.searchProperty("Maison").observe(this, Observer<List<Property>> { propertyList  -> displaySearchProperty(propertyList) })
    }

    private fun displaySearchProperty(propertyList: List<Property>){
        for (property in propertyList){
            println(property.price)
        }
    }
}