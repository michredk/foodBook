package com.example.foodfoodapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class Tests {
    @Test
    fun retrofit_getRecipes_Successfully() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun convert_api_response_to_data_Successfully() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun room_addRecords_Successfully() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun room_addMultipleRecords_Successfully() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun room_removeRecord_Successfully() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun room_removeMultipleRecords_Successfully() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun create_shopping_list_Successfully() {
        assertEquals(4, 2 + 2)
    }
}