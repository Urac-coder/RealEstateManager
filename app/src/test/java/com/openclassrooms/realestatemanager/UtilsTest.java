package com.openclassrooms.realestatemanager;

import com.openclassrooms.realestatemanager.utils.Utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UtilsTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void convertDollarToEuro_isCorrect(){
        int dollar = 10;
        int result;
        result = Utils.convertDollarToEuro(dollar);
        assertEquals(9, result);
    }

    @Test
    public void convertEuroToDollar_isCorrect(){
        int euro = 10;
        int result;
        result = Utils.convertEuroToDollar(euro);
        assertEquals(11, result);
    }

}