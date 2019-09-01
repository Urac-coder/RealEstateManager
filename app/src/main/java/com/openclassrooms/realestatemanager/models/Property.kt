package com.openclassrooms.realestatemanager.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.content.ClipData.Item
import android.content.ContentValues

@Entity (tableName = "Property")
data class Property(@PrimaryKey (autoGenerate = true) var id: Long,
                    @ColumnInfo (name = "type") var type: String,
                    @ColumnInfo (name = "price") var price: Int,
                    @ColumnInfo (name = "surface") var surface: Int,
                    @ColumnInfo (name = "nbRooms")var nbRooms: Int,
                    @ColumnInfo (name = "bedrooms")var bedrooms: Int,
                    @ColumnInfo (name = "description") var description: String,
                    @ColumnInfo (name = "picture") var picture: String,
                    @ColumnInfo (name = "address") var address: String,
                    @ColumnInfo (name = "city") var city: String,
                    @ColumnInfo (name = "zipCode") var zipCode: Int,
                    @ColumnInfo (name = "pointOfInterest") var pointOfInterest: String,
                    @ColumnInfo (name = "statusAvailable") var statusAvailable: Boolean,
                    @ColumnInfo (name = "entryDate") var entryDate: String,
                    @ColumnInfo (name = "saleDate") var saleDate: String,
                    @ColumnInfo (name = "realEstateAgent") var realEstateAgent: String,
                    @ColumnInfo (name = "nbOfPicture") var nbOfPicture: Int)

fun fromContentValues(values: ContentValues): Property {
        val property: Property = Property(0, "", 0, 0, 0, 0, "", "", "", "", 0,
                                            "", false, "", "", "", 0)

        if (values.containsKey("type")) property.type = values.getAsString("type")
        if (values.containsKey("price")) property.price = values.getAsInteger("price")
        if (values.containsKey("area")) property.surface = values.getAsInteger("isSelected")
        if (values.containsKey("nbRooms")) property.nbRooms = values.getAsInteger("nbRooms")
        if (values.containsKey("bedrooms")) property.bedrooms = values.getAsInteger("bedrooms")
        if (values.containsKey("description")) property.description = values.getAsString("description")
        if (values.containsKey("picture")) property.picture = values.getAsString("picture")
        if (values.containsKey("address")) property.address = values.getAsString("address")
        if (values.containsKey("city")) property.city = values.getAsString("city")
        if (values.containsKey("zipCode")) property.zipCode = values.getAsInteger("zipCode")
        if (values.containsKey("pointOfInterest")) property.pointOfInterest = values.getAsString("pointOfInterest")
        if (values.containsKey("statusAvailable")) property.statusAvailable = values.getAsBoolean("statusAvailable")
        if (values.containsKey("entryDate")) property.entryDate = values.getAsString("entryDate")
        if (values.containsKey("saleDate")) property.saleDate = values.getAsString("saleDate")
        if (values.containsKey("realEstateAgent")) property.realEstateAgent = values.getAsString("realEstateAgent")
        if (values.containsKey("nbOfPicture")) property.nbOfPicture = values.getAsInteger("nbOfPicture")
        return property
    }