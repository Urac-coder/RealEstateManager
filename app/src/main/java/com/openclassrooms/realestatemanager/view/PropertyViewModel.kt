package com.openclassrooms.realestatemanager.view

import android.content.ClipData.Item
import androidx.annotation.Nullable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.database.repositories.PictureDataRepository
import com.openclassrooms.realestatemanager.database.repositories.PropertyDataRepository
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.models.Property
import java.util.concurrent.Executor

class PropertyViewModel(private val propertyDataSource: PropertyDataRepository,private val pictureDataSource: PictureDataRepository,
                        private val executor: Executor) : ViewModel() {

    // -------------
    // FOR PROPERTY
    // -------------

    fun getProperty(propertyId: Long): LiveData<Property> {
        return propertyDataSource.getProperty(propertyId)
    }

    /*fun searchProperty(query: String): LiveData<List<Property>>{
        return propertyDataSource.searchProperty(query)
    }*/

    fun getAllProperty(): LiveData<List<Property>> {
        return propertyDataSource.getAllProperty()
    }

    fun insertPropertyAndPicture(property: Property, pictureList: List<Picture>) {
        executor.execute {
            val id = propertyDataSource.insertProperty(property)

            for (picture in pictureList ){
                picture.propertyId = id
                pictureDataSource.insertPicture(picture)
            }
        }
    }

    fun deleteProperty(propertyId: Long) {
        executor.execute { propertyDataSource.deleteProperty(propertyId) }
    }

    fun updateProperty(property: Property) {
        executor.execute { propertyDataSource.updateProperty(property) }
    }

    // -------------
    // FOR PICTURE
    // -------------

    fun getPicture(propertyId: Long): LiveData<List<Picture>> {
        return pictureDataSource.getPicture(propertyId)
    }

    fun insertPicture(picture: Picture) {
        executor.execute { pictureDataSource.insertPicture(picture) }
    }

    fun deletePicture(pictureId: Long) {
        executor.execute { pictureDataSource.deletePicture(pictureId) }
    }

    fun updatePicture(picture: Picture) {
        executor.execute { pictureDataSource.updatePicture(picture) }
    }
}