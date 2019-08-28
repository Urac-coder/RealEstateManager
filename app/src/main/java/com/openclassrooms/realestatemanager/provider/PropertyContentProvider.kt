package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.models.Property
import android.content.ContentUris
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.openclassrooms.realestatemanager.database.PropertyDatabase
import androidx.room.RoomMasterTable.TABLE_NAME
import android.content.ClipData.Item
import com.openclassrooms.realestatemanager.models.fromContentValues

class PropertyContentProvider : ContentProvider(){

    companion object {
        var AUTHORITY = "com.openclassrooms.realestatemanager.provider"
        var TABLE_NAME = Property::class.java.simpleName
        var URI_PROPERTY = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

    override fun onCreate(): Boolean { return true }

    @Nullable
    override fun query(uri: Uri, @Nullable projection: Array<String>?, @Nullable selection: String?, @Nullable selectionArgs: Array<String>?, @Nullable sortOrder: String?): Cursor? {

        if (context != null) {
            val propertyId = ContentUris.parseId(uri)
            val cursor = PropertyDatabase.getInstance(context!!)!!.propertyDao().getPropertyWhitCursor(propertyId)
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }

        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {

        if (context != null) {
            val id = PropertyDatabase.getInstance(context!!)!!.propertyDao().insertProperty(fromContentValues(contentValues!!))
            if (id != 0L) {
                context!!.contentResolver.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }
        }

        throw IllegalArgumentException("Failed to insert row into $uri")
    }


    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        if (context != null) {
            val count: Int = PropertyDatabase.getInstance(context!!)!!.propertyDao().deleteProperty(ContentUris.parseId(uri)).toString().toInt()
            context!!.contentResolver.notifyChange(uri, null)
            return count
        }
        throw IllegalArgumentException("Failed to delete row into $uri")
    }

    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int {
        if (context != null) {
            val count = PropertyDatabase.getInstance(context!!)!!.propertyDao().updateProperty(fromContentValues(contentValues!!)).toString().toInt()
            context!!.contentResolver.notifyChange(uri, null)
            return count
        }
        throw IllegalArgumentException("Failed to update row into $uri")
    }
}