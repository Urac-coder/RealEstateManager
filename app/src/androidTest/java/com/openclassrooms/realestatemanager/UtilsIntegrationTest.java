package com.openclassrooms.realestatemanager;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.openclassrooms.realestatemanager.utils.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UtilsIntegrationTest {

    @Test
    public void isInternetAvailable()throws Exception{
        assertTrue(Utils.isInternetAvailable(InstrumentationRegistry.getInstrumentation().getContext()));
    }
}
