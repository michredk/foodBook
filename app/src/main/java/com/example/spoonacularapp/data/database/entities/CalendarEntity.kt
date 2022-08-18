package com.example.spoonacularapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spoonacularapp.model.Result
import com.example.spoonacularapp.util.Constants

@Entity(tableName = Constants.CALENDAR_TABLE)
class CalendarEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result,
    var date: Int
)