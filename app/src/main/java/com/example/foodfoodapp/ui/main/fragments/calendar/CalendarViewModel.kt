package com.example.foodfoodapp.ui.main.fragments.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {

    var date: MutableLiveData<Int> = MutableLiveData<Int>()

    init {
        val now = Calendar.getInstance()
        date.postValue(String.format("%d%02d%02d",
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH) + 1,
            now.get(Calendar.DAY_OF_MONTH)
        ).toInt())
    }

}