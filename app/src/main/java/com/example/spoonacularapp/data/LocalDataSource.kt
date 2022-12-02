package com.example.spoonacularapp.data

import com.example.spoonacularapp.data.database.dao.CalendarDao
import com.example.spoonacularapp.data.database.dao.FavoritesDao
import com.example.spoonacularapp.data.database.dao.RecipesDao
import com.example.spoonacularapp.data.database.entities.CalendarEntity
import com.example.spoonacularapp.data.database.entities.FavoritesEntity
import com.example.spoonacularapp.data.database.entities.FavoritesGroupsEntity
import com.example.spoonacularapp.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao,
    private val favoritesDao: FavoritesDao,
    private val calendarDao: CalendarDao
) {

    fun readRecipes(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return favoritesDao.readFavoriteRecipes()
    }

    fun readFavoritesGroups(): Flow<List<FavoritesGroupsEntity>>{
        return favoritesDao.readFavoritesGroups()
    }

    fun readCalendarRecipes(): Flow<List<CalendarEntity>>{
        return calendarDao.readCalendarRecipes()
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity){
        recipesDao.insertRecipes(recipesEntity)
    }

    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        favoritesDao.insertFavoriteRecipe(favoritesEntity)
    }

    suspend fun insertFavoritesGroup(favoritesGroupsEntity: FavoritesGroupsEntity){
        favoritesDao.insertFavoritesGroup(favoritesGroupsEntity)
    }

    suspend fun insertRecipeToCalendar(calendarEntity: CalendarEntity){
        calendarDao.insertRecipeToCalendar(calendarEntity)
    }

    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        favoritesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteFavoritesGroup(favoritesGroupId: Int){
        favoritesDao.deleteFavoriteRecipesByGroupId(favoritesGroupId)
        favoritesDao.deleteFavoritesGroup(favoritesGroupId)
    }

    suspend fun deleteRecipeFromCalendar(calendarEntity: CalendarEntity){
        calendarDao.deleteRecipeFromCalendar(calendarEntity)
    }
}