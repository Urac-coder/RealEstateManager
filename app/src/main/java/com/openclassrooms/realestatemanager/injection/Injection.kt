package com.openclassrooms.realestatemanager.injection

import android.content.Context
import com.openclassrooms.realestatemanager.database.PropertyDatabase
import com.openclassrooms.realestatemanager.database.repositories.PropertyDataRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object Injection {

    fun providePropertyDataSource(context: Context): PropertyDataRepository {
        var database : PropertyDatabase = PropertyDatabase.getInstance(context)!!
        return PropertyDataRepository(database.propertyDao())
    }

    fun provideExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val dataSourceProperty = providePropertyDataSource(context)
        val executor = provideExecutor()
        return ViewModelFactory(dataSourceProperty, executor)
    }
}