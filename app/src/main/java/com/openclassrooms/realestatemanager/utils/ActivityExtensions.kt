package com.openclassrooms.realestatemanager.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.openclassrooms.realestatemanager.R

// ---------------------
// CONSTANT
// ---------------------

val BUNDLE_PROPERTY_ID = "PROPERTY_ID"
val BUNDLE_START_PARAMETER = "startParameter"
val PREF_DEVICE = "device"

// ---------------------
// FUNCTION
// ---------------------

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int){
    supportFragmentManager.inTransaction { add(frameId, fragment) }
}


fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction{replace(frameId, fragment)}
}

/*fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}*/

fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Context.longToast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun isTablet(context: Context): Boolean {
    return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}

fun isLandscape(context: Context): Boolean {
    return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

fun isPortrait(context: Context): Boolean {
    return context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

fun setToolbarTitle(activity: Activity, title: String){
    val toolbar: Toolbar = activity!!.findViewById<View>(R.id.toolbar) as Toolbar
    toolbar.title = title
}

fun displayConnection(menu: Menu, context: Context, menuItem: Int){
    var ic: Int = if (Utils.isInternetAvailable(context)) R.drawable.ic_wifi_on else R.drawable.ic_wifi_off
    menu.getItem(menuItem).icon = ContextCompat.getDrawable(context, ic)
}

