package com.example.foodfoodapp.bindingadapters

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfoodapp.adapters.CalendarRecipesAdapter
import com.example.foodfoodapp.data.database.entities.CalendarEntity

class CalendarBinding {

    companion object {

        @BindingAdapter("setVisibility", "setData", "setDate", requireAll = false)
        @JvmStatic
        fun setVisibilityCalendar(
            view: View,
            calendarEntities: List<CalendarEntity>?,
            mAdapter: CalendarRecipesAdapter?,
            date: Int?
        ) {
            val filtered = calendarEntities?.filter { it.date == date }
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
    }
}
