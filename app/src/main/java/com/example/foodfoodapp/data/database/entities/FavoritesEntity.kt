package com.example.foodfoodapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodfoodapp.model.Result
import com.example.foodfoodapp.util.Constants

@Entity(tableName = Constants.FAVORITES_TABLE)
class FavoritesEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result,
    var groupId: Int
)