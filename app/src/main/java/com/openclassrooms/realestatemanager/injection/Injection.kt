package com.openclassrooms.realestatemanager.injection

import android.content.Context
import com.openclassrooms.realestatemanager.database.PropertyDatabase
import com.openclassrooms.realestatemanager.database.repositories.PictureDataRepository
import com.openclassrooms.realestatemanager.database.repositories.PropertyDataRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object Injection {

    fun providePropertyDataSource(context: Context): PropertyDataRepository {
        var database : PropertyDatabase = PropertyDatabase.getInstance(context)!!
        return PropertyDataRepository(database.propertyDao())
    }

    fun providePictureDataSource(context: Context): PictureDataRepository {
        val database : PropertyDatabase = PropertyDatabase.getInstance(context)!!
        return PictureDataRepository(database.pictureDao())
    }

    fun provideExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val dataSourceProperty = providePropertyDataSource(context)
        val dataSourcePicture = providePictureDataSource(context)
        val executor = provideExecutor()
        return ViewModelFactory(dataSourceProperty, dataSourcePicture, executor)
    }
}