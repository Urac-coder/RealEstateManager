package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.models.Picture
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AddPropertyFragmentTest {

    //DATA
    private lateinit var picture: Picture
    private var pictureList: MutableList<Picture> = mutableListOf<Picture>()
    private var uriPictureSelected: String = "uriPictureSelected"

    // ---------------------
    // FUNCTION TO TEST
    // ---------------------

    private fun propertyInfoEmptyString(value: String): String{
        return if (value.isEmpty()) "null" else value
    }

    private fun propertyInfoEmptyInt(value: String): Int{
        return if (value.isEmpty()) 0 else value.toInt()
    }

    private fun propertyInfoEmptyPicture(value: List<Picture>): String{
        return if (value.isEmpty()) "null" else value[0].url
    }

    //PICTURE
    private fun addPictureToList(url: String, description: String){
        picture = Picture(0, url, description, 0)
        pictureList.add(picture)
    }

    private fun getGoodPicture(pictureId: Int): Int{
        var result: Int = pictureId
        for (picture in pictureList){
            if (picture.id.toInt() == pictureId) result = pictureList.indexOf(picture)
        }
        return result
    }

    private fun replacePicture(pictureId: Int){
        pictureList[pictureId].url = uriPictureSelected.toString()
    }

    private fun getGoodItemOfSpinner(item: String):Int{
        var value: Int = 0

        when(item){
            "Loft" -> value = 1
            "Manor" -> value = 2
            "House" -> value = 3
            "Residence" -> value = 4
            "Hotel" -> value = 5
            "Flat" -> value = 6
            "Duplex" -> value = 7
        }
        return value
    }

    // ---------------------
    // TEST
    // ---------------------

    @Test
    fun propertyInfoEmptyString_isCorrect() {
        var valueTest: String = ""
        valueTest = propertyInfoEmptyString(valueTest)

        assertEquals("null", valueTest)
    }

    @Test
    fun propertyInfoEmptyInt_isCorrect() {
        var valueTest: String = ""
        var result: Int = propertyInfoEmptyInt(valueTest)

        assertEquals(0, result)
    }

    @Test
    fun propertyInfoEmptyPicture_isCorrect() {
        var result: String = propertyInfoEmptyPicture(pictureList)

        assertEquals("null", result)
    }

    @Test
    fun addPictureToList_isCorrect(){
        addPictureToList("url", "description")

        assertEquals("url", pictureList[0].url)
        assertEquals("description", pictureList[0].description)
    }

    @Test
    fun getGoodPicture_isCorrect(){
        pictureList.add(Picture(4,"url1","description1", 1))
        pictureList.add(Picture(3,"url2","description2", 1))
        pictureList.add(Picture(1,"url3","description3", 1))
        pictureList.add(Picture(2,"url4","description4", 1))

        var result: Int = getGoodPicture(1)

        assertEquals(2, result)
    }

    @Test
    fun replacePicture_isCorrect(){
        pictureList.add(Picture(1,"url1","description1", 1))

        replacePicture(0)

        assertEquals("uriPictureSelected", pictureList[0].url)
    }

    @Test
    fun getGoodItemOfSpinner_isCorrect(){
        var result: Int = getGoodItemOfSpinner("Loft")

        assertEquals(1, result)
    }
}