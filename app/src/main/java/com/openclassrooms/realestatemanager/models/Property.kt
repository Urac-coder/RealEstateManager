package com.openclassrooms.realestatemanager.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "Property")
data class Property(@PrimaryKey (autoGenerate = true) var id: Long,
                    @ColumnInfo (name = "type") var type: String,
                    @ColumnInfo (name = "price") var price: Int,
                    @ColumnInfo (name = "area") var area: Int, var nbRooms: Int,
                    @ColumnInfo (name = "description") var description: String,
                    @ColumnInfo (name = "photoUrl") var photoUrl: String,
                    @ColumnInfo (name = "address") var address: String,
                    @ColumnInfo (name = "city") var city: String,
                    @ColumnInfo (name = "pointOfInterest") var pointOfInterest: String,
                    @ColumnInfo (name = "statusAvailable") var statusAvailable: Boolean,
                    @ColumnInfo (name = "entryDate") var entryDate: String,
                    @ColumnInfo (name = "saleDate") var saleDate: String,
                    @ColumnInfo (name = "realEstateAgent") var realEstateAgent: String)