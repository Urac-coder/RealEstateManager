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
import com.openclassrooms.realestatemanager.utils.ItemClickSupport
import com.openclassrooms.realestatemanager.utils.toast
import com.openclassrooms.realestatemanager.view.PropertyViewModel
import com.openclassrooms.realestatemanager.view.adapter.MainFragmentAdapter
import kotlinx.android.synthetic.main.fragment_main.*
import android.R.attr.key
import android.graphics.Color
import android.view.Menu
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_add_property.*
import kotlinx.android.synthetic.main.fragment_display_property.*
import kotlinx.android.synthetic.main.fragment_display_property_info.*
import java.text.DecimalFormat

class DisplayPropertyFragment : Fragment(){

    lateinit var propertyViewModel: PropertyViewModel
    var propertyId: Long = 0
    private val decimalFormat = DecimalFormat("#,###,###")

    companion object {
        fun newInstance() : DisplayPropertyFragment{
            return DisplayPropertyFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureViewModel()
        getProperty()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val bundle = this.arguments
        if (bundle != null) {
            propertyId = bundle.getLong("PROPERTY_ID", propertyId)
        }

        return inflater.inflate(R.layout.fragment_display_property, container, false)
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

    private fun displayProperty(property: Property){
        Glide.with(context!!).load(property.photoUrl).into(display_property_pic)
        println(property.photoUrl)
        display_property_textView_description.text = property.description
        display_property_textView_location.text = property.address + ", " + property.city
        display_property_textView_type.text = property.type
        display_property_textView_price.text = decimalFormat.format(property.price) + "$"
        display_property_textView_area.text = property.area.toString() + "m2"
        display_property_textView_nbRoom.text = property.nbRooms.toString()
        display_property_textView_bedroom.text = property.bedrooms.toString()
        display_property_textView_pointOfInterest.text = property.pointOfInterest
        propertyAlwaysAvailable(property.statusAvailable)
        display_property_textView_RealEstateAgent.text = property.realEstateAgent
        display_property_textView_entryDate.text = property.entryDate
        display_property_textView_saleDate.text = property.saleDate
    }

    private fun propertyAlwaysAvailable(available: Boolean){
        if (available){
            display_property_textView_alwaysAvailable.setTextColor(Color.parseColor("#00b800"))
            display_property_textView_alwaysAvailable.text = "The property is always available"
        } else{
            display_property_textView_alwaysAvailable.setTextColor(Color.RED)
            display_property_textView_alwaysAvailable.text = "The property is no longer available"

        }
    }

    private fun getProperty() {
        this.propertyViewModel.getProperty(propertyId).observe(this, Observer<Property> { property -> displayProperty(property) })
    }
}