package com.example.spoonacularapp.util

fun intDateToPrsssoperString(m: Int): String{
    val toMm: Int = m+1
    var mm: String
    mm = "0$toMm"
    if (toMm > 9){
        mm = toMm.toString()
    }
    return mm
}