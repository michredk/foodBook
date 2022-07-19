package com.example.spoonacularapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spoonacularapp.util.Constants.Companion.FAVORITES_GROUPS_TABLE

@Entity(tableName = FAVORITES_GROUPS_TABLE)
class FavoritesGroupsEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String
)