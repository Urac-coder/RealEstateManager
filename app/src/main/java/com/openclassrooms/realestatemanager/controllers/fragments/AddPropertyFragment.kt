package com.openclassrooms.realestatemanager.controllers.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.utils.toast
import com.openclassrooms.realestatemanager.view.PropertyViewModel
import kotlinx.android.synthetic.main.fragment_add_property.*
import kotlinx.android.synthetic.main.fragment_add_property_description.*
import androidx.lifecycle.Observer
import android.net.Uri
import com.bumptech.glide.Glide
import android.content.Intent
import pub.devrel.easypermissions.EasyPermissions
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ClipDescription
import android.provider.MediaStore
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.utils.longToast
import android.widget.TimePicker
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.DialogInterface
import android.view.Window
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.fragment_add_property_description.view.*


class AddPropertyFragment : Fragment(){

    lateinit var property: Property
    lateinit var propertyViewModel: PropertyViewModel
    lateinit var pictureDescription: String

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
            this.choosePictureFromPhoneAndAddToList()
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
                insertDescriptionToPictureAndAddPictureToList()
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
                pictureList[0].url,
                add_property_editText_address.text.toString(),
                add_property_editText_city.text.toString(),
                add_property_editText_zipCode.text.toString().toInt(),
                add_property_editText_pointOfInterest.text.toString(), true, "00/00/0000", "00/00/0000",
                add_property_editText_realEstateAgent.text.toString())

        //DEBUG
        /*property = Property(0,
                "a",
                1,
                1,
                1,
                1,
                "a",
                "null",
                "a",
                "a",
                1,
                "a", true, "00/00/0000", "00/00/0000",
                "a")*/

        this.propertyViewModel.insertPropertyAndPicture(property, pictureList)
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

    private fun choosePictureFromPhoneAndAddToList() {
        if (!EasyPermissions.hasPermissions(context!!, PERMS)) {
            EasyPermissions.requestPermissions(this, "Title picture", RC_PICTURE_PERMS, PERMS)
            return
        }
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RC_CHOOSE_PHOTO)
    }

    private fun addPictureToList(description: String){
        picture = Picture(0, uriPictureSelected.toString(), description, 0)
        pictureList.add(picture)
    }

    private fun insertDescriptionToPictureAndAddPictureToList(){
        val dialogBuilder = AlertDialog.Builder(context!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_add_property_description, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Enter picture description")

        dialogBuilder.setPositiveButton("save") { dialog, id ->

            this.pictureDescription = dialogView.fragment_add_property_description_editText.text.toString()
            add_property_display_picDescription.text = pictureDescription
            Glide.with(this).load(this.uriPictureSelected).into(this.add_property_display_pic)
            addPictureToList(pictureDescription)
        }
        dialogBuilder.setNegativeButton("cancel") { dialog, which -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun launchMainFragment() {
        val mainFragment = MainFragment()
        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_frame, mainFragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
    }
}