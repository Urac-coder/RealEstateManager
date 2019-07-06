package com.openclassrooms.realestatemanager.controllers.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
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
import androidx.lifecycle.Observer
import android.net.Uri
import com.bumptech.glide.Glide
import android.content.Intent
import pub.devrel.easypermissions.EasyPermissions
import android.app.Activity.RESULT_OK
import android.content.Context
import android.provider.MediaStore
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.utils.longToast
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Environment
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.controllers.activities.MainActivity
import com.openclassrooms.realestatemanager.utils.ItemClickSupport
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.view.adapter.AddPropertyPictureAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_property_date_picker.*
import kotlinx.android.synthetic.main.fragment_add_property_description.view.*
import kotlinx.android.synthetic.main.fragment_add_property_edit_picture.view.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_add_property_date_picker.view.*
import kotlinx.android.synthetic.main.fragment_display_property.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS


class AddPropertyFragment : Fragment(){

    lateinit var property: Property
    lateinit var propertyViewModel: PropertyViewModel
    lateinit var pictureDescription: String
    lateinit var adapter: AddPropertyPictureAdapter
    private var iterator: Long = 0
    private val arrayClickOnPicture = arrayOf("Edit","Delete")
    private var propertyToEditId: Long = 0 // 0 = CREATE PROPERTY else MODIFY PROPERTY
    private var saleDate: String = "null"
    private var  propertyAvailable: Boolean = true

    //PICTURE
    private val PERMS = Manifest.permission.READ_EXTERNAL_STORAGE
    private val RC_PICTURE_PERMS = 100
    private val RC_CHOOSE_PHOTO = 200
    private var uriPictureSelected: Uri? = null
    private lateinit var picture: Picture
    private var pictureList: MutableList<Picture> = mutableListOf<Picture>()
    private lateinit var pictureAction: String
    private val REQUEST_IMAGE_CAPTURE = 1
    private val PERMISSION_REQUEST_CODE_CAPTURE: Int = 101
    private var pictureToDeleteIdList: MutableList<Long> = mutableListOf<Long>()
    private var allowPictureDelete: Boolean = false

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

        if (propertyToEditId.toInt() != 0){
            getPropertyToEdit()
            getPictureListToEdit()
            add_property_btn_sale.visibility = View.VISIBLE
        } else{
            (add_property_btn_sale.parent as ViewGroup).removeView(add_property_btn_sale)
        }

        add_property_btn_save.setOnClickListener{
            insertProperty()
            launchMainFragment()

            if (propertyToEditId == 0L) {
                displayNotificationAfterAddProperty()
            } else{
                if (allowPictureDelete) {
                    for (pictureToDeleteId  in pictureToDeleteIdList){
                        this.propertyViewModel.deletePicture(pictureToDeleteId)
                    }
                    allowPictureDelete = false
                }
            }
        }

        add_property_btn_sale.setOnClickListener {
            selectSaleDate()
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
        val bundle = this.arguments
        if (bundle != null) {
            propertyToEditId = bundle.getLong("PROPERTY_ID", propertyToEditId)
        }

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
                    context!!.toast(response.id.toString())
                    clickOnPicture(getGoodPicture(response.id.toInt()), response.id)
                }
    }

    private fun updatePictureList(picture: List<Picture>) {
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
                this.uriPictureSelected = data.data
                if (pictureAction == "insert") insertDescriptionToPictureAndAddPictureToList()
            } else {context!!.toast("No picture selected")}
        }
    }

    // ---------------------
    // UTILS
    // ---------------------

    private fun selectSaleDate(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val datePicker = DatePickerDialog(activity, R.style.DatePickerTheme ,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var month: Int = monthOfYear + 1

                    saleDate = ("$dayOfMonth/$month/$year")
                    add_property_btn_sale.text = "Sold : $saleDate"
                    propertyAvailable = false
                }, year, month, day)

        datePicker.show()
    }

    //INSERT
    private fun insertProperty(){
        property = Property(propertyToEditId,
                propertyInfoEmptyString(add_property_spinner_type.selectedItem.toString()),
                propertyInfoEmptyInt(add_property_editText_price.text.toString()),
                propertyInfoEmptyInt(add_property_editText_surface.text.toString()),
                propertyInfoEmptyInt(add_property_editText_roomNumber.text.toString()),
                propertyInfoEmptyInt(add_property_editText_bedrooms.text.toString()),
                propertyInfoEmptyString(add_property_editText_description.text.toString()),
                propertyInfoEmptyPicture(pictureList),
                propertyInfoEmptyString(add_property_editText_address.text.toString()),
                propertyInfoEmptyString(add_property_editText_city.text.toString()),
                propertyInfoEmptyInt(add_property_editText_zipCode.text.toString()),
                propertyInfoEmptyString(add_property_editText_pointOfInterest.text.toString()),
                propertyAvailable,
                Utils.getTodayDate(),
                saleDate,
                propertyInfoEmptyString(add_property_editText_realEstateAgent.text.toString()))

        if (propertyToEditId == 0L){
            this.propertyViewModel.insertPropertyAndPicture(property, setGoodIdToPictureList(pictureList))
        } else{
            this.propertyViewModel.insertPropertyAndPicture(property, pictureList)
        }
    }

    private fun propertyInfoEmptyString(value: String): String{
        return if (value.isEmpty()) "null" else value
    }

    private fun propertyInfoEmptyInt(value: String): Int{
        return if (value.isEmpty()) 0 else value.toInt()
    }

    private fun propertyInfoEmptyPicture(value: List<Picture>): String{
        return if (value.isEmpty()) "null" else value[0].url
    }

    //PICTURE
    private fun addPictureToList(description: String){
        picture = Picture(iterator, uriPictureSelected.toString(), description, 0)
        pictureList.add(picture)
        updatePictureList(pictureList)
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
    private fun clickOnPicture(pictureId : Int, realId: Long){
        val builder = AlertDialog.Builder(context!!)
                .setItems(arrayClickOnPicture
                ) { dialog, which ->
                    when(which){
                        0 -> editPicture(pictureId)
                        1 -> deletePicture(pictureId, realId)
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
        Glide.with(dialogView).load(pictureList[pictureId].url).into(dialogView.fragment_add_property_edit_picture_pic)
        dialogView.fragment_add_property_edit_picture_editText_description.setText(pictureList[pictureId].description)
        uriPictureSelected = Uri.parse(pictureList[pictureId].url)

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
            Glide.with(dialogView).load(uriPictureSelected).into(dialogView.fragment_add_property_edit_picture_pic)
        }

        //DIALOG BUTTON
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

    private fun getGoodPicture(pictureId: Int): Int{
        var result: Int = pictureId
        for (picture in pictureList){
            if (picture.id.toInt() == pictureId) result = pictureList.indexOf(picture)
        }
        return result
    }

    private fun replacePicture(pictureId: Int){
        pictureList[pictureId].url = uriPictureSelected.toString()
    }

    private fun deletePicture(pictureId: Int, realId: Long){
        pictureList.removeAt(pictureId)
        adapter.notifyDataSetChanged()
        pictureToDeleteIdList.add(realId)
        allowPictureDelete = true
    }

    //TAKE PICTURE
    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        uriPictureSelected = FileProvider.getUriForFile(activity!!, "com.openclassrooms.realestatemanager.fileprovider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPictureSelected)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

        if (pictureAction == "insert") insertDescriptionToPictureAndAddPictureToList()
    }

    @Throws(IOException::class)
    private fun createFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    //EDIT PROPERTY
    private fun getPropertyToEdit(){
        this.propertyViewModel.getProperty(propertyToEditId).observe(this, Observer<Property> { property -> savePropertyToEdit(property) })
    }

    private fun getPictureListToEdit(){
        this.propertyViewModel.getPicture(propertyToEditId).observe(this, Observer<List<Picture>> {
            pictureList  ->  saveAndDisplayPictureListToEdit(pictureList)
        })
    }

    private fun saveAndDisplayPictureListToEdit(list: List<Picture>){
        for (picture in list ){
            this.pictureList.add(picture)
        }
        updatePictureList(pictureList)
    }

    private fun savePropertyToEdit(property: Property){
        this.property = property
        initInputWithPropertyToEdit(property)
    }

    private fun initInputWithPropertyToEdit(property: Property){
        add_property_spinner_type.setSelection(getGoodItemOfSpinner(property.type))
        add_property_editText_price.setText(property.price.toString())
        add_property_editText_surface.setText(property.area.toString())
        add_property_editText_roomNumber.setText(property.nbRooms.toString())
        add_property_editText_bedrooms.setText(property.bedrooms.toString())
        add_property_editText_description.setText(property.description)
        add_property_editText_address.setText(property.address)
        add_property_editText_city.setText(property.city)
        add_property_editText_zipCode.setText(property.zipCode.toString())
        add_property_editText_pointOfInterest.setText(property.pointOfInterest)
        add_property_editText_realEstateAgent.setText(property.realEstateAgent)

        if (property.saleDate == "null") add_property_btn_sale.text = "Sale" else add_property_btn_sale.text = "Sold : ${property.saleDate}"
        add_property_btn_save.text = "Save modification"
    }

    private fun getGoodItemOfSpinner(item: String):Int{
        var value: Int = 0

        when(item){
            "Loft" -> value = 1
            "Manor" -> value = 2
            "House" -> value = 3
            "Residence" -> value = 4
            "Hotel" -> value = 5
            "Flat" -> value = 6
            "Duplex" -> value = 7
        }
        return value
    }

    //NOTIFICATION
    private fun displayNotificationAfterAddProperty() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val datePicker = DatePickerDialog(activity, R.style.DatePickerTheme ,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var month: Int = monthOfYear + 1

                    saleDate = ("$dayOfMonth/$month/$year")
                    add_property_btn_sale.text = "Sold : $saleDate"
                    propertyAvailable = false
                }, year, month, day)

        datePicker.show()
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