package com.openclassrooms.realestatemanager.controllers.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.os.Bundle
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Property
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
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.RingtoneManager
import android.os.Build
import android.os.Environment
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.controllers.activities.MainActivity
import com.openclassrooms.realestatemanager.utils.*
import com.openclassrooms.realestatemanager.view.adapter.AddPropertyPictureAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_property_description.view.*
import kotlinx.android.synthetic.main.fragment_add_property_edit_picture.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class AddPropertyFragment : BaseFragment(){

    lateinit var property: Property
    lateinit var pictureDescription: String
    lateinit var adapter: AddPropertyPictureAdapter
    private var iterator: Long = 0
    private val arrayClickOnPicture = arrayOf("Modifier","Supprimer")
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
    private var pictureAction: String = ""
    private val REQUEST_IMAGE_CAPTURE = 1
    private val PERMISSION_REQUEST_CODE_CAPTURE: Int = 101
    private var pictureToDeleteIdList: MutableList<String> = mutableListOf<String>()
    private var allowPictureDelete: Boolean = false

    companion object {
        fun newInstance(): AddPropertyFragment {
            return AddPropertyFragment()
        }
    }

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_add_property
    }

    override fun addToOnCreateView(rootView: View, savedInstanceState: Bundle?) {
        val bundle = this.arguments
        if (bundle != null) {
            propertyToEditId = bundle.getLong(BUNDLE_PROPERTY_ID, propertyToEditId)
        }
    }

    override fun addToOnViewCreated() {
        configureRecyclerView()
        configureOnClickRecyclerView()

        if (propertyToEditId.toInt() != 0){
            getPropertyToEdit()
            getPictureListToEdit()
            add_property_btn_sale.visibility = View.VISIBLE
            if (!isTablet(context!!)) activity!!.activity_main_bottom_navigation.visibility = View.GONE
            setToolbarTitle(activity!!, "Modifier une propriété")
        } else{
            setToolbarTitle(activity!!, "Ajouter une propriété")
            add_property_btn_sale.visibility = View.GONE
        }

        if (SharedPref.read(PREF_DEVICE, "") == "EURO"){
            add_property_title_price.text = "Prix €"
        } else{
            add_property_title_price.text = "Prix $"
        }

        add_property_btn_save.setOnClickListener{
            insertProperty()

            if (propertyToEditId == 0L) {
                displayNotificationAfterAddProperty()
                launchMainFragment()
            } else{
                if (allowPictureDelete) {
                    for (pictureToDeleteId  in pictureToDeleteIdList){
                        this.propertyViewModel.deletePicture(pictureToDeleteId)
                    }
                    allowPictureDelete = false
                }
                launchMainFragment()
            }
        }

        add_property_btn_sale.setOnClickListener {
            selectSaleDate()
        }

        add_property_btn_importPicture.setOnClickListener{
            this.choosePictureFromPhone()
            pictureAction = "insert"
        }

        add_property_btn_takePicture.setOnClickListener {
            if (checkPermission()){
                pictureAction = "insert"
                takePicture()
            } else requestPermission()
        }
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

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
                    clickOnPicture(getGoodPicture(response.id.toInt()))
                }
    }

    // ---------------------
    // ACTION
    // ---------------------

    private fun updatePictureList(picture: List<Picture>) { this.adapter.updateData(picture) }

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
                uriPictureSelected = data.data
                context!!.grantUriPermission("com.openclassrooms.realestatemanager", uriPictureSelected, Intent.FLAG_GRANT_READ_URI_PERMISSION)

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
        var entryDate: String = Utils.getTodayDate()
        if(propertyToEditId != 0L){ entryDate = property.entryDate }

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
                entryDate,
                saleDate,
                propertyInfoEmptyString(add_property_editText_realEstateAgent.text.toString()),
                pictureList.size)

        this.propertyViewModel.insertPropertyAndPicture(property, pictureList)
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
    private fun addPictureToList(url: String, description: String){
        picture = Picture(0, url, description, 0)
        pictureList.add(picture)
        updatePictureList(pictureList)
        iterator++
    }

    private fun insertDescriptionToPictureAndAddPictureToList(){
        val dialogBuilder = AlertDialog.Builder(context!!)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_add_property_description, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Entre une déscription")

        dialogBuilder.setPositiveButton("save") { dialog, id ->

            this.pictureDescription = dialogView.fragment_add_property_description_editText.text.toString()
            addPictureToList(uriPictureSelected.toString(), pictureDescription)
        }
        dialogBuilder.setNegativeButton("cancel") { dialog, which -> }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    //IMPORT PICTURE FROM DEVICE
    private fun choosePictureFromPhone() {
        if (!EasyPermissions.hasPermissions(context!!, PERMS)) {
            EasyPermissions.requestPermissions(this, "Title picture", RC_PICTURE_PERMS, PERMS)
            return
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_CHOOSE_PHOTO)
    }

    private fun exifToDefrees(exifOrientation: Int): Int{
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)  return 90
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) return 180
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) return 270
        return 0
    }

    //TAKE PICTURE WITH DEVICE
    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        saveTakePicture(intent)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

        if (pictureAction == "insert") insertDescriptionToPictureAndAddPictureToList()
    }

    private fun saveTakePicture(intent: Intent){
        val file: File = createFile()

        uriPictureSelected = FileProvider.getUriForFile(activity!!, "com.openclassrooms.realestatemanager.fileprovider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPictureSelected)
    }

    @Throws(IOException::class)
    private fun createFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
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
        pictureAction = "replace"

        //INIT
        Glide.with(dialogView).load(pictureList[pictureId].url).into(dialogView.fragment_add_property_edit_picture_pic)
        dialogView.fragment_add_property_edit_picture_editText_description.setText(pictureList[pictureId].description)
        uriPictureSelected = Uri.parse(pictureList[pictureId].url)

        //ACTION
        dialogView.fragment_add_property_edit_btn_importPicture.setOnClickListener {
            this.choosePictureFromPhone()
            Glide.with(dialogView).load(resources.getDrawable(R.drawable.click)).into(dialogView.fragment_add_property_edit_picture_pic)
        }

        dialogView.fragment_add_property_edit_btn_takePicture.setOnClickListener {
            if (checkPermission()){ takePicture() } else requestPermission()
            Glide.with(dialogView).load(resources.getDrawable(R.drawable.click)).into(dialogView.fragment_add_property_edit_picture_pic)
        }

        dialogView.fragment_add_property_edit_picture_pic.setOnClickListener {
            Glide.with(dialogView).load(uriPictureSelected).into(dialogView.fragment_add_property_edit_picture_pic)
        }

        //DIALOG BUTTON
        dialogBuilder.setPositiveButton("Enregistrer") { dialog, id ->
            pictureAction = "replace"
            pictureList[pictureId].description = dialogView.fragment_add_property_edit_picture_editText_description.text.toString()
            this.pictureDescription = dialogView.fragment_add_property_edit_picture_editText_description.text.toString()
            replacePicture(pictureId)
            adapter.notifyDataSetChanged()
        }

        dialogBuilder.setNegativeButton("Annuler") { dialog, which -> }

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

    private fun deletePicture(pictureId: Int){
        pictureToDeleteIdList.add(pictureList[pictureId].url)
        pictureList.removeAt(pictureId)
        adapter.notifyDataSetChanged()
        allowPictureDelete = true
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
        add_property_editText_surface.setText(property.surface.toString())
        add_property_editText_roomNumber.setText(property.nbRooms.toString())
        add_property_editText_bedrooms.setText(property.bedrooms.toString())
        add_property_editText_description.setText(property.description)
        add_property_editText_address.setText(property.address)
        add_property_editText_city.setText(property.city)
        add_property_editText_zipCode.setText(property.zipCode.toString())
        add_property_editText_pointOfInterest.setText(property.pointOfInterest)
        add_property_editText_realEstateAgent.setText(property.realEstateAgent)

        if (property.saleDate == "null") add_property_btn_sale.text = "VENDU" else add_property_btn_sale.text = "Sold : ${property.saleDate}"
        add_property_btn_save.text = "ENREGISTRER"
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

        val NOTIFICATION_ID: Int = 7
        val NOTIFICATION_TAG: String = "REALESTATEMANAGER"

        // 1 - Create an Intent that will be shown when user will click on the Notification
        var intent: Intent = Intent(context, MainActivity::class.java)
        var pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        // 2 - Create a Style for the Notification
        var inboxStyle:  NotificationCompat.InboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle("Félicitation")
        inboxStyle.addLine("Vous avez ajouté un nouveau bien")

        // 3 - Create a Channel (Android 8)
        var channelId = "channel_id"

        // 4 - Build a Notification object
        var notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context!!, channelId)
                        .setSmallIcon(R.drawable.ic_add_24)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Félicitation")
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle)

        // 5 - Add the Notification to the Notification Manager and show it.
        var notificationManager: NotificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channelName: CharSequence = "Vous avez ajouté un nouveau bien"
            var importance: Int = NotificationManager.IMPORTANCE_HIGH
            var mChannel: NotificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        // 7 - Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build())
    }

    //LAUNCH
    private fun launchMainFragment() {
        var frameLayout: Int = R.id.main_activity_frame

        if (isTablet(context!!)) {
            activity!!.main_activity_frame_tablet.visibility =  View.GONE
            frameLayout = R.id.main_activity_frame_left
        }
        if(isLandscape(context!!) && !isTablet(context!!)){ frameLayout = R.id.main_activity_frame_land }

        val mainFragment = MainFragment()
        activity!!.supportFragmentManager.beginTransaction()
                .replace(frameLayout, mainFragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
    }

    // ---------------------
    // LIFE CYCLE
    // ---------------------

    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.activity_main_bottom_navigation.visibility = View.VISIBLE
    }
}