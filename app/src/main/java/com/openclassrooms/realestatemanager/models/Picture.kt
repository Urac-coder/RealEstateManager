package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//@Entity(foreignKeys = ForeignKey(entity = Property::class, parentColumns = "id", childColumns = "propertyId"))
data class Picture(@PrimaryKey(autoGenerate = true) var id: Long, var url: String, var description: String)