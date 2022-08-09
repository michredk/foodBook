package com.example.spoonacularapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spoonacularapp.util.Constants

@Entity(tableName = Constants.FAVORITES_GROUPS_TABLE)
class FavoritesGroupsEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var color: String
)