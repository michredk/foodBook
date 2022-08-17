package com.example.spoonacularapp.bindingadapters

import android.graphics.Color
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spoonacularapp.adapters.FavoriteRecipesAdapter
import com.example.spoonacularapp.data.database.entities.FavoritesEntity

class FavoriteRecipesBinding {

    companion object {

        @BindingAdapter("setVisibility", "setData", "setGroupId", requireAll = false)
        @JvmStatic
        fun setVisibility(
            view: View,
            favoritesEntity: List<FavoritesEntity>?,
            mAdapter: FavoriteRecipesAdapter?,
            groupId: Int
        ) {
            val filtered = favoritesEntity?.filter { it.groupId == groupId }
            when (view) {
                is RecyclerView -> {
                    val dataCheck = filtered.isNullOrEmpty()
                    view.isInvisible = dataCheck
                    if (!dataCheck) {
                        filtered?.let {
                            mAdapter?.setData(it)
                        }
                    }
                }
                else -> view.isVisible = filtered.isNullOrEmpty()
            }
        }

        @BindingAdapter("parseBackgroundColor")
        @JvmStatic
        fun parseBackgroundColor(view: View, color: String) {
            view.setBackgroundColor(Color.parseColor(color))
        }

    }
}