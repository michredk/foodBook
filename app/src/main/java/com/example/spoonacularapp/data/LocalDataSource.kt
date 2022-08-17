package com.example.spoonacularapp.data

import com.example.spoonacularapp.data.database.RecipesDao
import com.example.spoonacularapp.data.database.entities.CalendarEntity
import com.example.spoonacularapp.data.database.entities.FavoritesEntity
import com.example.spoonacularapp.data.database.entities.FavoritesGroupsEntity
import com.example.spoonacularapp.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {

    fun readRecipes(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return recipesDao.readFavoriteRecipes()
    }

    fun readFavoritesGroups(): Flow<List<FavoritesGroupsEntity>>{
        return recipesDao.readFavoritesGroups()
    }

    fun readCalendarRecipes(): Flow<List<CalendarEntity>>{
        return recipesDao.readCalendarRecipes()
    }

    fun readGroupById(id: Int): FavoritesGroupsEntity {
        return recipesDao.readFavoritesGroupById(id)
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity){
        recipesDao.insertRecipes(recipesEntity)
    }

    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    suspend fun insertFavoritesGroup(favoritesGroupsEntity: FavoritesGroupsEntity){
        recipesDao.insertFavoritesGroup(favoritesGroupsEntity)
    }

    suspend fun insertRecipeToCalendar(calendarEntity: CalendarEntity){
        recipesDao.insertRecipeToCalendar(calendarEntity)
    }

    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteFavoritesGroup(favoritesGroupId: Int){
        recipesDao.deleteFavoriteRecipesByGroupId(favoritesGroupId)
        recipesDao.deleteFavoritesGroup(favoritesGroupId)
    }

    suspend fun deleteRecipeFromCalendar(calendarEntity: CalendarEntity){
        recipesDao.deleteRecipeFromCalendar(calendarEntity)
    }

    suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavoriteRecipes()
    }
}