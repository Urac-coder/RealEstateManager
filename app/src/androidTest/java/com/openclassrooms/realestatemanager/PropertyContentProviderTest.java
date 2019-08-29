package com.openclassrooms.realestatemanager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.openclassrooms.realestatemanager.database.PropertyDatabase;
import com.openclassrooms.realestatemanager.provider.PropertyContentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class PropertyContentProviderTest {

    // FOR DATA
    private ContentResolver mContentResolver;

    // DATA SET FOR TEST
    private static long PROPERTY_ID = 1;

    @Before
    public void setUp() {
        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                PropertyDatabase.class)
                .allowMainThreadQueries()
                .build();
        mContentResolver = InstrumentationRegistry.getContext().getContentResolver();
    }

    @Test
    public void getPropertiesWhenNoPropertyInserted() {
        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(PropertyContentProvider.Companion.getURI_PROPERTY(), PROPERTY_ID), null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(0));
        cursor.close();
    }

    @Test
    public void insertAndGetProperty() {
        // BEFORE : Adding demo item
        final Uri userUri = mContentResolver.insert(PropertyContentProvider.Companion.getURI_PROPERTY(), generateProperty());
        // TEST
        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(PropertyContentProvider.Companion.getURI_PROPERTY(), PROPERTY_ID), null, null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(1));
        assertThat(cursor.moveToFirst(), is(true));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("type")), is("Maison"));
    }

    // ---

    private ContentValues generateProperty(){
        final ContentValues values = new ContentValues();
        values.put("type", "Maison");
        values.put("price", "100000");
        values.put("surface", "200");
        values.put("nbRooms", "8");
        values.put("bedrooms", "3");
        values.put("description", "Jolie petite maison");
        values.put("picture", "null");
        values.put("address", "23 rue du pont");
        values.put("city", "Paris");
        values.put("zipCode", "10200");
        values.put("pointOfInterest", "Ecole");
        values.put("stastusAvailable", "false");
        values.put("entryDate", "12/06/1962");
        values.put("saleDate", "04/01/1963");
        values.put("realEstateAgent", "Pierre Paul");
        values.put("nbOfPicture", "3");
        return values;
    }
}
