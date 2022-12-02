package com.example.foodfoodapp.bindingadapters

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfoodapp.adapters.FavoriteRecipesAdapter
import com.example.foodfoodapp.data.database.entities.FavoritesEntity

class FavoriteRecipesBinding {

    companion object {

        @BindingAdapter("setVisibilityByItemCount")
        @JvmStatic
        fun setVisibilitySimple(
            view: View,
            itemCount: Int
        ){
            when(view){
                is RecyclerView ->
                    view.isVisible = itemCount > 0
                else -> view.isVisible = itemCount == 0
            }
        }

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
        fun parseBackgroundColor(view: View, color: Int) {
            view.setBackgroundColor(ContextCompat.getColor(view.context, color))
        }

    }
}