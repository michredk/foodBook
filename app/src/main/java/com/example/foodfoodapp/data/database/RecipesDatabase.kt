package com.example.foodfoodapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodfoodapp.data.database.dao.CalendarDao
import com.example.foodfoodapp.data.database.dao.FavoritesDao
import com.example.foodfoodapp.data.database.dao.RecipesDao
import com.example.foodfoodapp.data.database.entities.CalendarEntity
import com.example.foodfoodapp.data.database.entities.FavoritesEntity
import com.example.foodfoodapp.data.database.entities.FavoritesGroupsEntity
import com.example.foodfoodapp.data.database.entities.RecipesEntity

@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class, FavoritesGroupsEntity::class, CalendarEntity::class],
    version = 9,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun recipesDao(): RecipesDao

    abstract fun favoritesDao(): FavoritesDao

    abstract fun calendarDao(): CalendarDao

}