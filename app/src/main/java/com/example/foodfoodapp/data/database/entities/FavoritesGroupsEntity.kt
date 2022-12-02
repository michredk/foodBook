package com.example.foodfoodapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodfoodapp.util.Constants

@Entity(tableName = Constants.FAVORITES_GROUPS_TABLE)
class FavoritesGroupsEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var color: Int
)