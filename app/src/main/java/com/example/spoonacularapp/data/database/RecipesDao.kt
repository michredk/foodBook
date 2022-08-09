package com.example.spoonacularapp.data.database

import androidx.room.*
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

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM favorites_groups_table ORDER BY id ASC")
    fun readFavoritesGroups(): Flow<List<FavoritesGroupsEntity>>

    @Delete
    suspend fun deleteFavoritesGroup(favoritesGroupsEntity: FavoritesGroupsEntity)

//    @Query("DELETE FROM favorite_recipes_table WHERE groupId = id")
//    suspend fun deleteAllFavoriteRecipesFromGroup(favoritesGroupsEntity: FavoritesGroupsEntity)

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()
}