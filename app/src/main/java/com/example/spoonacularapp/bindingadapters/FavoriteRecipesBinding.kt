package com.example.spoonacularapp.bindingadapters

import android.graphics.Color
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

class FavoriteRecipesBinding {

    companion object{

        @BindingAdapter("setVisibility")
        @JvmStatic
        fun <T> setVisibility(view: View, listOfEntities: List<T>?){
            when(view) {
                is RecyclerView -> {
                    val dataCheck = listOfEntities.isNullOrEmpty()
                    view.isInvisible = dataCheck
                }
                else -> view.isVisible = listOfEntities.isNullOrEmpty()
            }
        }

        @BindingAdapter("parseBackgroundColor")
        @JvmStatic
        fun parseBackgroundColor(view: View, color: String){
            view.setBackgroundColor(Color.parseColor(color))
        }

    }
}