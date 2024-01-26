package com.example.foodfoodapp.ui.scanner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.classifier.Classifications
import javax.inject.Inject

data class ScannerUiState(
    val label: String? = null,
    val score: String? = null,
)

// https://developer.android.com/topic/libraries/architecture/viewmodel
// https://stackoverflow.com/questions/55350567/dynamically-create-choice-chip-in-android
@HiltViewModel
class ScannerViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()



    fun updateResults(listClassifications: List<Classifications>?) {
        var highestScoreCategory: Category? = null
        listClassifications?.firstOrNull()?.let { classification ->
            val sortedCategories = classification.categories.sortedBy { it?.index }
            highestScoreCategory = sortedCategories.firstOrNull()
        }
        highestScoreCategory?.let {category ->
            _uiState.update {uiState ->
                uiState.copy(
                    label = category.label,
                    score = String.format("%.2f", category.score)
                )
            }
        }
    }
}

