package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Picture",
        foreignKeys = [ForeignKey(entity = Property::class, parentColumns = arrayOf("id"), childColumns = arrayOf("propertyId"))])
data class Picture(@PrimaryKey(autoGenerate = true) var id: Long, var url: String, var description: String, var propertyId: Long)