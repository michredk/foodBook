package com.example.spoonacularapp.ui.main.fragments.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.spoonacularapp.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesGroupsViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
    ): AndroidViewModel(application) {

    fun addFavoriteGroup(name: String) {
        repository
    }

}