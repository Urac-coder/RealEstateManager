package com.openclassrooms.realestatemanager.database.dao

import android.content.ClipData.Item
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.models.Picture


@Dao
interface PictureDao {

    @Query("SELECT * FROM Picture WHERE propertyId = :propertyId")
    fun getPicture(propertyId: Long): LiveData<List<Picture>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPicture(picture: Picture)

    @Update
    fun updatePicture(picture: Picture)

    @Query("DELETE FROM Picture WHERE id = :pictureId")
    fun deletePicture(pictureId: Long)
}