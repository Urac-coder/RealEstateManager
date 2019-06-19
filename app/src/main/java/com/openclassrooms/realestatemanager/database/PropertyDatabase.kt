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
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                PropertyDatabase::class.java, "MyDatabase.db")
                                .addCallback(prepopulateDatabase())
                                .build()
                    }
                }
            }
            return INSTANCE
        }

        // ---

        private fun prepopulateDatabase(): RoomDatabase.Callback {
            return object : RoomDatabase.Callback() {

                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    val contentValues = ContentValues()
                    contentValues.put("id", "0")
                    contentValues.put("type", "Maison")
                    contentValues.put("price", "100000")
                    contentValues.put("area", 30)
                    contentValues.put("nbRooms", 14)
                    contentValues.put("bedrooms", 4)
                    contentValues.put("description", "Super petite maison")
                    contentValues.put("picture", "https://media-cdn.tripadvisor.com/media/photo-s/08/b9/80/bf/angurukaramulla-temple.jpg")
                    contentValues.put("address", "34 route du pont")
                    contentValues.put("city", "Paris")
                    contentValues.put("zipCode", "62240")
                    contentValues.put("pointOfInterest", "Ecole, carrefour, auchan")
                    contentValues.put("statusAvailable", false)
                    contentValues.put("entryDate", "12/04/1959")
                    contentValues.put("saleDate", "05/08/1959")
                    contentValues.put("RealEstateAgent", "Pierre Monier")

                    db.insert("Property", OnConflictStrategy.IGNORE, contentValues)
                }
            }
        }
    }
}