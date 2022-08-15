package com.example.spoonacularapp.bindingadapters

import android.graphics.Color
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spoonacularapp.data.database.entities.FavoritesGroupsEntity

class FavoriteRecipesBinding {

    companion object{

        @BindingAdapter("setVisibility")
        @JvmStatic
        fun setVisibility(view: View, itemCount: Int) {
            view.isInvisible = itemCount > 0
        }

        @BindingAdapter("setVisibilityGroups")
        @JvmStatic
        fun setVisibilityGroups(view: View, favoritesEntity: List<FavoritesGroupsEntity>?) {
            when (view) {
                is RecyclerView -> {
                    val dataCheck = favoritesEntity.isNullOrEmpty()
                    view.isInvisible = dataCheck
                }
                else -> view.isVisible = favoritesEntity.isNullOrEmpty()
            }
        }

        @BindingAdapter("parseBackgroundColor")
        @JvmStatic
        fun parseBackgroundColor(view: View, color: String){
            view.setBackgroundColor(Color.parseColor(color))
        }

    }
}