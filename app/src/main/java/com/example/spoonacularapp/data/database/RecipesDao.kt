package com.example.spoonacularapp.data.database

import androidx.room.*
import com.example.spoonacularapp.data.database.entities.CalendarEntity
import com.example.spoonacularapp.data.database.entities.FavoritesEntity
import com.example.spoonacularapp.data.database.entities.FavoritesGroupsEntity
import com.example.spoonacularapp.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritesGroup(favoritesGroupsEntity: FavoritesGroupsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipeToCalendar(calendarEntity: CalendarEntity)

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM favorites_groups_table ORDER BY id ASC")
    fun readFavoritesGroups(): Flow<List<FavoritesGroupsEntity>>

    @Query("SELECT * FROM calendar_recipes_table ORDER BY id ASC")
    fun readCalendarRecipes(): Flow<List<CalendarEntity>>

    @Query("SELECT * FROM favorites_groups_table WHERE id = :id")
    fun readFavoritesGroupById(id: Int): FavoritesGroupsEntity

    @Query("DELETE FROM favorite_recipes_table WHERE groupId = :id")
    suspend fun deleteFavoriteRecipesByGroupId(id: Int)

    @Query("DELETE FROM favorites_groups_table WHERE id = :id")
    suspend fun deleteFavoritesGroup(id: Int)

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Delete
    suspend fun deleteRecipeFromCalendar(calendarEntity: CalendarEntity)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()
}