package com.openclassrooms.realestatemanager.database.repositories

import android.content.ClipData.Item
import android.net.Uri
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.PictureDao
import com.openclassrooms.realestatemanager.models.Picture


class PictureDataRepository(private val pictureDao: PictureDao) {

    // --- GET ---

    fun getPicture(propertyId: Long): LiveData<List<Picture>> {
        return this.pictureDao.getPicture(propertyId)
    }

    // --- CREATE ---

    fun insertPicture(picture: Picture) {
        pictureDao.insertPicture(picture)
    }

    // --- DELETE ---
    fun deletePicture(url: String) {
        pictureDao.deletePicture(url)
    }

    // --- UPDATE ---
    fun updatePicture(picture: Picture) {
        pictureDao.updatePicture(picture)
    }

}