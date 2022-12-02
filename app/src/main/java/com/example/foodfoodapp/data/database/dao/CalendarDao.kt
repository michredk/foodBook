package com.example.foodfoodapp.data.database.dao

import androidx.room.*
import com.example.foodfoodapp.data.database.entities.CalendarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipeToCalendar(calendarEntity: CalendarEntity)

    @Query("SELECT * FROM calendar_recipes_table ORDER BY id ASC")
    fun readCalendarRecipes(): Flow<List<CalendarEntity>>

    @Delete
    suspend fun deleteRecipeFromCalendar(calendarEntity: CalendarEntity)
}