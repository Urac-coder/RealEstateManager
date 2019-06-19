package com.openclassrooms.realestatemanager.controllers.fragments

import android.Manifest
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
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity.RESULT_CANCELED
import android.net.Uri
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.Glide
import android.content.Intent
import pub.devrel.easypermissions.EasyPermissions
import android.app.Activity.RESULT_OK
import android.provider.MediaStore
import android.view.Menu
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.utils.longToast
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions.hasPermissions

class AddPropertyFragment : Fragment(){

    lateinit var property: Property
    lateinit var propertyViewModel: PropertyViewModel

    //PICTURE
    private val PERMS = Manifest.permission.READ_EXTERNAL_STORAGE
    private val RC_PICTURE_PERMS = 100
    private val RC_CHOOSE_PHOTO = 200
    private var uriPictureSelected: Uri? = null
    private lateinit var picture: Picture
    var pictureList: MutableList<Picture> = mutableListOf<Picture>()

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
                insertProperty()
                launchMainFragment()
                context!!.longToast("Congratulations you have successfully registered a new property")
                context!!.longToast("Congratulations you have successfully registered a new property")
            } else { context?.toast("You must first select all fields") }
        }

        add_property_btn_importPicture.setOnClickListener{
            this.choosePictureFromPhone()
            picture = Picture(uriPictureSelected.toString(), "Salle de baim")
            pictureList.add(picture)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null){
            this.handleResponse(requestCode, resultCode, data!!)
        }
    }

    // -------------------
    // PERMISSION
    // -------------------

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    // --------------------
    // HANDLER
    // --------------------

    private fun handleResponse(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                this.uriPictureSelected = data.data
                Glide.with(this).load(this.uriPictureSelected).into(this.add_property_display_pic)

            } else {
                context!!.toast("No picture selected")
            }
        }
    }

    // ---------------------
    // UTILS
    // ---------------------

    private fun insertProperty(){
        property = Property(0,
                add_property_spinner_type.selectedItem.toString(),
                add_property_editText_price.text.toString().toInt(),
                add_property_editText_surface.text.toString().toInt(),
                add_property_editText_roomNumber.text.toString().toInt(),
                add_property_editText_bedrooms.text.toString().toInt(),
                add_property_editText_description.text.toString(),
                uriPictureSelected.toString(),
                add_property_editText_address.text.toString(),
                add_property_editText_city.text.toString(),
                add_property_editText_zipCode.text.toString().toInt(),
                add_property_editText_pointOfInterest.text.toString(), true, "00/00/0000", "00/00/0000",
                add_property_editText_realEstateAgent.text.toString())

        this.propertyViewModel.insertProperty(property)
    }

    private fun allComplete(): Boolean{
        var allComplete = false

        if (add_property_spinner_type.selectedItem.toString() != "Property type" && !add_property_editText_price.text.isEmpty() &&
                !add_property_editText_surface.text.isEmpty() && !add_property_editText_roomNumber.text.isEmpty() && !add_property_editText_bedrooms.text.isEmpty() &&
                !add_property_editText_description.text.isEmpty() && uriPictureSelected != null && !add_property_editText_address.text.isEmpty() &&
                !add_property_editText_zipCode.text.isEmpty() && !add_property_editText_pointOfInterest.text.isEmpty() &&
                !add_property_editText_realEstateAgent.text.isEmpty()) {
            allComplete = true
        }
        return allComplete
    }

    private fun choosePictureFromPhone() {
        if (!EasyPermissions.hasPermissions(context!!, PERMS)) {
            EasyPermissions.requestPermissions(this, "Title picture", RC_PICTURE_PERMS, PERMS)
            return
        }
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RC_CHOOSE_PHOTO)
    }

    private fun launchMainFragment() {
        val mainFragment = MainFragment()
        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_frame, mainFragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
    }
}