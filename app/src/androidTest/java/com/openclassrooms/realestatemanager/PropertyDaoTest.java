package com.openclassrooms.realestatemanager;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.openclassrooms.realestatemanager.database.PropertyDatabase;
import com.openclassrooms.realestatemanager.models.Property;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PropertyDaoTest {

    // FOR DATA
    private PropertyDatabase database;
    private static long PROPERTY_ID = 1;
    private static Property PROPERTY_DEMO = new Property(PROPERTY_ID,"Maison", 100000, 30, 14, 3,"Super petite maisonette", "null",
            "34 route du pont", "Paris",00000,"Ecole, Carrefour, Auchan", false, "12/04/1959", "05/08/1959",
            "Pierre Monier", 0);

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void initDb() throws Exception {
        this.database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext(),
                PropertyDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() throws Exception {
        database.close();
    }

    @Test
    public void insertAndGetProperty()throws InterruptedException{
        this.database.propertyDao().insertProperty(PROPERTY_DEMO);

        Property property = LiveDataTestUtil.getValue(this.database.propertyDao().getProperty(PROPERTY_ID));
        assertTrue(property.getAddress().equals(PROPERTY_DEMO.getAddress()) && property.getId() == PROPERTY_ID);

    }
}
