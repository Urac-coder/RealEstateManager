package com.openclassrooms.realestatemanager.controllers.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.location.*
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.view.PropertyViewModel
import kotlinx.android.synthetic.main.fragment_map_view.view.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_map_view.*
import pub.devrel.easypermissions.EasyPermissions
import android.content.Intent
import com.openclassrooms.realestatemanager.controllers.activities.MainActivity

class MapViewFragment : BaseFragment(), OnMapReadyCallback, com.google.android.gms.location.LocationListener, GoogleMap.OnMarkerClickListener{

    private lateinit var mMap: GoogleMap
    private var mLocationManager: LocationManager? = null

    private lateinit var menu: Menu

    private val PERMS = Manifest.permission.ACCESS_FINE_LOCATION
    private val LOCATION_PERMS = 100

    private var currentPosition: LatLng? = null


    companion object {
        fun newInstance(): MapViewFragment {
            return MapViewFragment()
        }
    }

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_map_view
    }


    override fun addToOnCreateView(rootView: View, savedInstanceState: Bundle?) {
        rootView.mapView.onCreate(savedInstanceState)
        rootView.mapView.onResume() // needed to get the map to display immediately

        rootView.mapView.getMapAsync(this)

        configureViewDependingConnection(rootView)

        rootView.fragment_map_view_txt_noConnextion.setOnClickListener {
            configureViewDependingConnection(rootView)
            getAllPropertyAndDisplayWithMarker()
        }

        rootView.myLocationButton.setOnClickListener {
            askPermissionsAndShowMyLocation()
            getCurrentLocationAndZoomOn()
            getAllPropertyAndDisplayWithMarker()
        }
    }

    override fun addToOnViewCreated() {
        setToolbarTitle(activity!!, "Map")
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.setOnMarkerClickListener(this)

        askPermissionsAndShowMyLocation()
        getCurrentLocationAndZoomOn()
        getAllPropertyAndDisplayWithMarker()
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    private fun configureViewDependingConnection(view: View){
        if (Utils.isInternetAvailable(context)){
            view.fragment_map_view_container.visibility = View.VISIBLE
            view.fragment_map_view_txt_noConnextion.visibility = View.INVISIBLE
        } else {
            view.fragment_map_view_container.visibility = View.INVISIBLE
            view.fragment_map_view_txt_noConnextion.visibility = View.VISIBLE
        }
    }

    // --------------------
    // MARKER
    // --------------------

    override fun onMarkerClick(mMarker: Marker): Boolean {
        if (mMarker == mMarker)
            launchDisplayPropertyFragment(mMarker.title.toLong())
        mMarker.hideInfoWindow()
        return true
    }
    // --------------------
    // CURRENT LOCATION
    // --------------------

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val criteria = Criteria()
        mLocationManager = context!!.getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = mLocationManager!!.getBestProvider(criteria, true)

        if (!EasyPermissions.hasPermissions(context!!, PERMS)) {
            EasyPermissions.requestPermissions(this, "Accès à la location", LOCATION_PERMS, PERMS)
            return
        }

        val location = mLocationManager!!.getLastKnownLocation(provider)

        if (location != null) {
            currentPosition = LatLng(location.latitude, location.longitude)
        }
    }

    private fun getCurrentLocationAndZoomOn() {
        try {
            getCurrentLocation()

            val cameraPosition = CameraPosition.Builder()
                    .target(currentPosition)
                    .zoom(17f)
                    .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        } catch (ignored: Exception) {}
    }

    override fun onLocationChanged(p0: Location?) {}

    // --------------------
    // PERMISSION
    // --------------------

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("MissingPermission")
    private fun askPermissionsAndShowMyLocation() {

        if (!EasyPermissions.hasPermissions(context!!, PERMS)) {
            EasyPermissions.requestPermissions(this, "Accès à la location", LOCATION_PERMS, PERMS)
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
    }

    // ---------------------
    // UTILS
    // ---------------------

    private fun getLatLngToAddress(location: String): Address? {
        var address: Address? = null

        var gc: Geocoder  = Geocoder(context)
        var addresses: List<Address> = gc.getFromLocationName(location, 1) // get the found Address Objects

        for (a: Address in addresses) {
            if (a.hasLatitude() && a.hasLongitude()) address = a
        }
        return address
    }

    private fun getAllPropertyAndDisplayWithMarker() {
        this.propertyViewModel.getAllProperty().observe(this, Observer<List<Property>> {propertyList  -> displayAllPropertyWithMarker(propertyList) })
    }

    private fun displayAllPropertyWithMarker(propertyList: List<Property>){
        var address: Address?

        for (property in propertyList){
            if (property.address != "null" || property.city != "null"){

                try {
                    address = getLatLngToAddress(property.address + "," + property.city)
                    if (address != null){
                        val addressToDisplay = LatLng(address.latitude, address.longitude)
                        mMap.addMarker(MarkerOptions().position(addressToDisplay).title(property.id.toString()))
                    }
                }catch (e: java.lang.Exception) {
                    if (Utils.isInternetAvailable(context)) restartMainActivityAndStartMap() else context!!.longToast("Connexion insuffisante pour afficher les propriétées")
                }
            }
        }
    }

    // ---------------------
    // LAUNCH
    // ---------------------

    private fun launchDisplayPropertyFragment(propertyId: Long) {
        var frameLayout: Int = R.id.main_activity_frame
        if (isTablet(context!!)){
            activity!!.main_activity_frame_tablet.visibility = View.VISIBLE
            frameLayout = com.openclassrooms.realestatemanager.R.id.main_activity_frame_tablet
        }
        if(isLandscape(context!!) && !isTablet(context!!)){ frameLayout = R.id.main_activity_frame_land }

        val fragment = DisplayPropertyFragment()
        val bundle = Bundle()
        bundle.putLong(BUNDLE_PROPERTY_ID, propertyId)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction()
                .replace(frameLayout, fragment, "findThisFragment")
                .addToBackStack(null)
                .commit()
    }

    private fun restartMainActivityAndStartMap() {
        val myIntent = Intent(context, MainActivity::class.java)
        val bundle = Bundle()

        bundle.putString(BUNDLE_START_PARAMETER, "launchMap")
        myIntent.putExtras(bundle)

        this.startActivity(myIntent)
    }
}