package com.example.spoonacularapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spoonacularapp.data.database.entities.CalendarEntity
import com.example.spoonacularapp.data.database.entities.FavoritesEntity
import com.example.spoonacularapp.data.database.entities.FavoritesGroupsEntity
import com.example.spoonacularapp.data.database.entities.RecipesEntity

@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class, FavoritesGroupsEntity::class, CalendarEntity::class],
    version = 9,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun recipesDao(): RecipesDao

}