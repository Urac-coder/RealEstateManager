package com.openclassrooms.realestatemanager.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.view.PropertyViewModel
import kotlinx.android.synthetic.main.fragment_search_property.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.utils.ItemClickSupport
import com.openclassrooms.realestatemanager.utils.isTablet
import com.openclassrooms.realestatemanager.utils.setToolbarTitle
import com.openclassrooms.realestatemanager.utils.toast
import com.openclassrooms.realestatemanager.view.adapter.MainFragmentAdapter
import kotlinx.android.synthetic.main.fragment_display_property.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.Exception
import java.util.*


class SearchPropertyFragment: Fragment(){

    private lateinit var propertyViewModel: PropertyViewModel
    private lateinit var adapter: MainFragmentAdapter

    private var priceMin: Int = 0
    private var priceMax: Int = 100000000
    private var surfaceMin: Int = 0
    private var surfaceMax: Int = 10000
    private var minRoomNb: Int = 0
    private var minBedroomNb: Int = 0
    private var city: String = "null"
    private var pointOfInterest: String = "null"
    private var nbPicture: Int = 0
    private var available: Boolean = true

    private lateinit var propertyListSecondFilter: List<Property>
    private lateinit var propertyListThirdFilter: List<Property>

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
        configureRecyclerView()
        configureOnClickRecyclerView()
        setToolbarTitle(activity!!, "Recherche")

        search_property_btn.setOnClickListener {
            initValue()
            getAllProperty()
            getAllValue()
            search_property_input_container.visibility = View.GONE
        }
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

    private fun initValue(){
        priceMin = 0
        priceMax = 100000000
        surfaceMin = 0
        surfaceMax = 10000
        city = "null"
        pointOfInterest = "null"
        nbPicture = 0
        available = true
    }

    private fun getAllValue(){
        if (search_property_editText_priceMin.text.toString() != "") priceMin = search_property_editText_priceMin.text.toString().toInt()
        if (search_property_editText_priceMax.text.toString() != "") priceMax = search_property_editText_priceMax.text.toString().toInt()
        if (search_property_editText_surface_min.text.toString() != "") surfaceMin = search_property_editText_surface_min.text.toString().toInt()
        if (search_property_editText_surface_max.text.toString() != "") surfaceMax = search_property_editText_surface_max.text.toString().toInt()

        if (search_property_editText_roomNb.text.toString() != "") minRoomNb = search_property_editText_roomNb.text.toString().toInt()
        if (search_property_editText_bedroomNb.text.toString() != "") minBedroomNb = search_property_editText_bedroomNb.text.toString().toInt()

        if (search_property_editText_city.text.toString() != "") city = search_property_editText_city.text.toString()
        if (search_property_editText_pointsOfInterest.text.toString() != "")pointOfInterest = search_property_editText_pointsOfInterest.text.toString()
        if (search_property_editText_picNb.text.toString() != "") nbPicture = search_property_editText_picNb.text.toString().toInt()
        available = formatedAvailable(search_property_spinner_available.selectedItem.toString())
    }

    private fun formatedAvailable(value: String): Boolean{
        return value == "A vendre" //return true if  value == "A  vendre"
    }

    private fun getAllProperty(){
        this.propertyViewModel.getAllProperty().observe(this, Observer<List<Property>> { propertyList  -> searchProperty(propertyList) })
    }

    private fun filterProperty(propertyList: List<Property>): List<Property>{
        var listToReturn: List<Property>
        var propertyListFirstFilter = propertyList.filter { it.price  >= priceMin && it.price <= priceMax && it.surface >= surfaceMin && it.surface <=
                surfaceMax && it.nbRooms >= minRoomNb && it.bedrooms >= minBedroomNb && it.nbOfPicture >= nbPicture && it.statusAvailable == available }
        listToReturn = propertyListFirstFilter

        if (city != "null") {
            propertyListSecondFilter = propertyListFirstFilter.filter { it.city  == city }
            listToReturn = propertyListSecondFilter
        }

        if (pointOfInterest != "null"){
            propertyListThirdFilter = listToReturn.filter { it.pointOfInterest == pointOfInterest }
            listToReturn = propertyListThirdFilter
        }

        return listToReturn
    }

    private fun searchProperty(propertyList: List<Property>){
        updatePropertyList(filterProperty(propertyList))
    }

    private fun launchDisplayPropertyFragment(propertyId: Long) {
        var frameLayout: Int = R.id.main_activity_frame
        if (isTablet(context!!)) frameLayout = R.id.main_activity_frame_right

        val fragment = DisplayPropertyFragment()
        val bundle = Bundle()
        bundle.putLong("PROPERTY_ID", propertyId)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction()
                .replace(frameLayout, fragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
    }
}