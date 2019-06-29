package com.openclassrooms.realestatemanager.controllers.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import android.view.Window
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.controllers.activities.MainActivity
import com.openclassrooms.realestatemanager.utils.ItemClickSupport
import com.openclassrooms.realestatemanager.view.adapter.AddPropertyPictureAdapter
import com.openclassrooms.realestatemanager.view.adapter.MainFragmentAdapter
import kotlinx.android.synthetic.main.fragment_add_property_description.view.*
import kotlinx.android.synthetic.main.fragment_add_property_edit_picture.*
import kotlinx.android.synthetic.main.fragment_add_property_edit_picture.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main_item.view.*
import kotlinx.android.synthetic.main.fragment_take_picture.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddPropertyFragment : Fragment(){

    lateinit var property: Property
    lateinit var propertyViewModel: PropertyViewModel
    lateinit var pictureDescription: String
    lateinit var adapter: AddPropertyPictureAdapter
    private var iterator: Long = 0
    private val arrayClickOnPicture = arrayOf("Edit","Delete")

    //PICTURE
    private val PERMS = Manifest.permission.READ_EXTERNAL_STORAGE
    private val RC_PICTURE_PERMS = 100
    private val RC_CHOOSE_PHOTO = 200
    private var uriPictureSelected: Uri? = null
    private var uriPictureSelectedEdit: Uri? = null
    private lateinit var picture: Picture
    private var pictureList: MutableList<Picture> = mutableListOf<Picture>()
    private lateinit var pictureAction: String
    val REQUEST_IMAGE_CAPTURE = 1
    private val PERMISSION_REQUEST_CODE_CAPTURE: Int = 101

    companion object {
        fun newInstance(): AddPropertyFragment {
            return AddPropertyFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureViewModel()
        configureRecyclerView()
        configureOnClickRecyclerView()

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
            pictureAction = "insert"
        }

        add_property_btn_takePicture.setOnClickListener {
            if (checkPermission()){
                pictureAction = "insert"
                takePicture()
            } else requestPermission()
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

    @SuppressLint("WrongConstant")
    private fun configureRecyclerView() {
        this.adapter = AddPropertyPictureAdapter()
        this.add_property_fragment_recyclerView.adapter = this.adapter
        this.add_property_fragment_recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)

    }

    private fun configureOnClickRecyclerView() {
        ItemClickSupport.addTo(add_property_fragment_recyclerView, R.layout.fragment_add_property)
                .setOnItemClickListener { recyclerView, position, v ->
                    val response = adapter.getPicture(position)
                    clickOnPicture(response.id.toInt())
                }
    }

    private fun updatePropertyList(picture: List<Picture>) {
        this.adapter.updateData(picture)
    }

    // -------------------
    // PERMISSION
    // -------------------

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

        when (requestCode) {
            PERMISSION_REQUEST_CODE_CAPTURE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    takePicture()
                } else { context!!.toast("Permission Denied") }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE_CAPTURE)
    }


    // --------------------
    // HANDLER
    // --------------------

    private fun handleResponse(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (pictureAction == "insert"){
                    this.uriPictureSelected = data.data
                    insertDescriptionToPictureAndAddPictureToList()
                }

                if (pictureAction == "replace"){
                    this.uriPictureSelectedEdit = data.data
                }


                } else {
                context!!.toast("No picture selected")
            }
        }
    }

    // ---------------------
    // UTILS
    // ---------------------

    //INSERT
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

        this.propertyViewModel.insertPropertyAndPicture(property, setGoodIdToPictureList(pictureList))
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

    //PICTURE
    private fun addPictureToList(description: String){
        picture = Picture(iterator, uriPictureSelected.toString(), description, 0)
        pictureList.add(picture)
        updatePropertyList(pictureList)
        iterator++
    }

    private fun setGoodIdToPictureList(pictureList : List<Picture>): List<Picture>{
        for (picture in pictureList ){
            picture.id = 0
        }
        return pictureList
    }

    private fun insertDescriptionToPictureAndAddPictureToList(){
        val dialogBuilder = AlertDialog.Builder(context!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_add_property_description, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Enter picture description")

        dialogBuilder.setPositiveButton("save") { dialog, id ->

            this.pictureDescription = dialogView.fragment_add_property_description_editText.text.toString()
            addPictureToList(pictureDescription)
        }
        dialogBuilder.setNegativeButton("cancel") { dialog, which -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    //IMPORT PICTURE
    private fun choosePictureFromPhoneAndAddToList() {
        if (!EasyPermissions.hasPermissions(context!!, PERMS)) {
            EasyPermissions.requestPermissions(this, "Title picture", RC_PICTURE_PERMS, PERMS)
            return
        }
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RC_CHOOSE_PHOTO)
    }

    //EDIT PICTURE
    private fun clickOnPicture(pictureId : Int){
        val builder = AlertDialog.Builder(context!!)
                .setItems(arrayClickOnPicture
                ) { dialog, which ->
                    when(which){
                        0 -> editPicture(pictureId)
                        1 -> deletePicture(pictureId)
                    }
                }
        builder.create().show()
    }

    private fun editPicture (pictureId : Int){
        val dialogBuilder = AlertDialog.Builder(context!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_add_property_edit_picture, null)
        dialogBuilder.setView(dialogView)

        //INIT
        context!!.toast("dialogInit")
        Glide.with(dialogView).load(pictureList[pictureId].url).into(dialogView.fragment_add_property_edit_picture_pic)
        dialogView.fragment_add_property_edit_picture_editText_description.setText(pictureList[pictureId].description)
        uriPictureSelectedEdit = Uri.parse(pictureList[pictureId].url)

        //ACTION
        dialogView.fragment_add_property_edit_btn_importPicture.setOnClickListener {
            pictureAction = "replace"
            this.choosePictureFromPhoneAndAddToList()
            Glide.with(dialogView).load(resources.getDrawable(R.drawable.click)).into(dialogView.fragment_add_property_edit_picture_pic)
        }

        dialogView.fragment_add_property_edit_btn_takePicture.setOnClickListener {
            pictureAction = "replace"
            if (checkPermission()){ takePicture() } else requestPermission()
            Glide.with(dialogView).load(resources.getDrawable(R.drawable.click)).into(dialogView.fragment_add_property_edit_picture_pic)
        }

        dialogView.fragment_add_property_edit_picture_pic.setOnClickListener {
            Glide.with(dialogView).load(uriPictureSelectedEdit).into(dialogView.fragment_add_property_edit_picture_pic)
        }

        dialogBuilder.setPositiveButton("save") { dialog, id ->
            pictureList[pictureId].description = dialogView.fragment_add_property_edit_picture_editText_description.text.toString()
            this.pictureDescription = dialogView.fragment_add_property_edit_picture_editText_description.text.toString()
            replacePicture(pictureId)
            adapter.notifyDataSetChanged()
        }

        dialogBuilder.setNegativeButton("cancel") { dialog, which -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun replacePicture(pictureId: Int){
        pictureList[pictureId].url = uriPictureSelectedEdit.toString()
    }

    private fun deletePicture(pictureId: Int){
        pictureList.removeAt(pictureId)
        adapter.notifyDataSetChanged()
    }

    //TAKE PICTURE
    private fun takePicture() {
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        uriPictureSelected = FileProvider.getUriForFile(
                activity!!,
                "com.openclassrooms.realestatemanager.fileprovider",
                file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPictureSelected)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

        if (pictureAction == "insert"){
            insertDescriptionToPictureAndAddPictureToList()
        }

        if (pictureAction == "replace"){
            this.uriPictureSelectedEdit = uriPictureSelected
        }
    }

    @Throws(IOException::class)
    private fun createFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )
    }

    //LAUNCH
    private fun launchMainFragment() {
        val mainFragment = MainFragment()
        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_frame, mainFragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
    }
}