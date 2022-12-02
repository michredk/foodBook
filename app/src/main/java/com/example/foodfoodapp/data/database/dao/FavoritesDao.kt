package com.example.foodfoodapp.data.database.dao

import androidx.room.*
import com.example.foodfoodapp.data.database.entities.FavoritesEntity
import com.example.foodfoodapp.data.database.entities.FavoritesGroupsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritesGroup(favoritesGroupsEntity: FavoritesGroupsEntity)

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM favorites_groups_table ORDER BY id ASC")
    fun readFavoritesGroups(): Flow<List<FavoritesGroupsEntity>>

    @Query("DELETE FROM favorite_recipes_table WHERE groupId = :id")
    suspend fun deleteFavoriteRecipesByGroupId(id: Int)

    @Query("DELETE FROM favorites_groups_table WHERE id = :id")
    suspend fun deleteFavoritesGroup(id: Int)

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)
}