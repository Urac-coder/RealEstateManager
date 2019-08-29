package com.openclassrooms.realestatemanager.utils

import android.content.SharedPreferences
import android.app.Activity
import android.content.Context


object SharedPref {

    private var mSharedPref: SharedPreferences? = null
    var currentPositionLat = "currentPositionLat"
    var currentPositionLng = "currentPositionLng"
    var radius = "radius"
    var notificationAllow = "notificationAllow"
    var getNotificationActived = "getNotificationActived"
    var currentLanguage = "currentLanguage"
    var dayRestaurant = "dayRestaurant"
    var notificationRestaurantName = "notificationRestaurantName"
    var notificationRestaurantAddress = "notificationRestaurantAddress"
    var notificationHour = "notificationHour"
    var notificationMin = "notificationMin"

    val currentPosition: String
        get() = read(currentPositionLat, "") + "," + read(currentPositionLng, "")

    fun init(context: Context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    }

    fun read(key: String, defValue: String): String? {
        return mSharedPref!!.getString(key, defValue)
    }

    fun write(key: String, value: String) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.commit()
    }

    fun read(key: String, defValue: Boolean): Boolean {
        return mSharedPref!!.getBoolean(key, defValue)
    }

    fun write(key: String, value: Boolean) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.commit()
    }

    fun read(key: String, defValue: Int): Int {
        return mSharedPref!!.getInt(key, defValue)
    }

    fun write(key: String, value: Int?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putInt(key, value!!)
        prefsEditor.commit()
    }
}