package com.openclassrooms.realestatemanager.database

import androidx.room.OnConflictStrategy
import android.content.ContentValues
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.annotation.NonNull
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.ClipData.Item
import android.content.Context
import androidx.room.Database
import com.openclassrooms.realestatemanager.database.dao.PictureDao
import com.openclassrooms.realestatemanager.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.models.Property


@Database(entities = [Property::class, Picture::class], version = 1, exportSchema = false)
abstract class PropertyDatabase : RoomDatabase() {

    // --- DAO ---
    abstract fun propertyDao(): PropertyDao
    abstract fun pictureDao(): PictureDao

    companion object {

        // --- SINGLETON ---
        @Volatile
        private var INSTANCE: PropertyDatabase? = null

        // --- INSTANCE ---
        fun getInstance(context: Context): PropertyDatabase? {
            if (INSTANCE == null) {
                synchronized(PropertyDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                PropertyDatabase::class.java, "MyDatabase.db")
                                .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}