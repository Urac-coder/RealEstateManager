package com.openclassrooms.realestatemanager.controllers.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.PropertyDatabase
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.utils.toast
import com.openclassrooms.realestatemanager.view.PropertyViewModel
import kotlinx.android.synthetic.main.fragment_add_property.*
import androidx.annotation.Nullable
import androidx.lifecycle.Observer


class AddPropertyFragment : Fragment(){

    lateinit var property: Property
    lateinit var propertyViewModel: PropertyViewModel


    companion object {
        fun newInstance(): AddPropertyFragment {
            return AddPropertyFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureViewModel()

        add_property_btn_save.setOnClickListener{
            if(allComplete()){
                createProperty()
                launchMainFragment()
            } else { context?.toast("You must first select all fields") }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_property, container, false)
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

    private fun getProperty(propertyId: Long) {
        this.propertyViewModel.getProperty(propertyId).observe(this, Observer<Property> { property -> println(property.description) })
    }

    private fun createProperty(){
        property = Property(2,add_property_spinner_type.selectedItem.toString(),
                add_property_editText_price.text.toString().toInt(),
                add_property_editText_surface.text.toString().toInt(),
                add_property_editText_roomNumber.text.toString().toInt(),
                add_property_editText_description.text.toString(), "null",
                add_property_editText_address.text.toString(),
                add_property_editText_pointOfInterest.text.toString(), true, "00/00/0000", "00/00/0000",
                add_property_editText_realEstateAgent.text.toString())

        this.propertyViewModel.insertProperty(property)
    }

    private fun allComplete(): Boolean{
        var allComplete = false

        if (add_property_spinner_type.selectedItem.toString() != "Property type" && !add_property_editText_price.text.isEmpty() &&
                !add_property_editText_surface.text.isEmpty() && !add_property_editText_roomNumber.text.isEmpty() &&
                !add_property_editText_description.text.isEmpty() && !add_property_editText_address.text.isEmpty() &&
                !add_property_editText_pointOfInterest.text.isEmpty() && !add_property_editText_realEstateAgent.text.isEmpty()) {
            allComplete = true
        }
        return allComplete
    }

    private fun launchMainFragment() {
        val mainFragment = MainFragment()
        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_frame, mainFragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
    }
}