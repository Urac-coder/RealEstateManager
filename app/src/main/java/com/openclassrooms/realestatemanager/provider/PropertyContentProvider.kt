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

class PropertyContentProvider : ContentProvider(){

    val AUTHORITY = "com.openclassrooms.realestatemanager.provider"
    val TABLE_NAME = Property::class.java.simpleName
    val URI_ITEM = Uri.parse("content://$AUTHORITY/$TABLE_NAME")

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

        /*if (context != null) {
            val id = PropertyDatabase.getInstance(context!!)!!.propertyDao().insertProperty(fromContentValues(contentValues))
            if (id != 0L) {
                context!!.contentResolver.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }
        }*/

        throw IllegalArgumentException("Failed to insert row into $uri")
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}