package com.example.foodfoodapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodfoodapp.util.Constants.Companion.DEFAULT_CUISINE_TYPE
import com.example.foodfoodapp.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foodfoodapp.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foodfoodapp.util.Constants.Companion.PREFERENCES_BACK_ONLINE
import com.example.foodfoodapp.util.Constants.Companion.PREFERENCES_CUISINE_TYPE
import com.example.foodfoodapp.util.Constants.Companion.PREFERENCES_CUISINE_TYPE_ID
import com.example.foodfoodapp.util.Constants.Companion.PREFERENCES_DIET_TYPE
import com.example.foodfoodapp.util.Constants.Companion.PREFERENCES_DIET_TYPE_ID
import com.example.foodfoodapp.util.Constants.Companion.PREFERENCES_MEAL_TYPE
import com.example.foodfoodapp.util.Constants.Companion.PREFERENCES_MEAL_TYPE_ID
import com.example.foodfoodapp.util.Constants.Companion.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

@ViewModelScoped // because used in recipesViewModel
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context){

    private val dataStore: DataStore<Preferences> = context.dataStore

    private object PreferenceKeys {
        val selectedMealType = stringPreferencesKey(PREFERENCES_MEAL_TYPE)
        val selectedMealTypeId = intPreferencesKey(PREFERENCES_MEAL_TYPE_ID)
        val selectedDietType = stringPreferencesKey(PREFERENCES_DIET_TYPE)
        val selectedDietTypeId = intPreferencesKey(PREFERENCES_DIET_TYPE_ID)
        val selectedCuisineType = stringPreferencesKey(PREFERENCES_CUISINE_TYPE)
        val selectedCuisineTypeId = intPreferencesKey(PREFERENCES_CUISINE_TYPE_ID)

        val backOnline = booleanPreferencesKey(PREFERENCES_BACK_ONLINE)
    }

    suspend fun saveTypes(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int, cuisineType: String, cuisineTypeId: Int){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectedMealType] = mealType
            preferences[PreferenceKeys.selectedMealTypeId] = mealTypeId
            preferences[PreferenceKeys.selectedDietType] = dietType
            preferences[PreferenceKeys.selectedDietTypeId] = dietTypeId
            preferences[PreferenceKeys.selectedCuisineType] = cuisineType
            preferences[PreferenceKeys.selectedCuisineTypeId] = cuisineTypeId
        }
    }

    suspend fun saveBackOnline(backOnline: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }

    val readTypes: Flow<MealDietCuisineType> = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val selectedMealType = preferences[PreferenceKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE
            val selectedMealTypeId = preferences[PreferenceKeys.selectedMealTypeId] ?: 0
            val selectedDietType = preferences[PreferenceKeys.selectedDietType] ?: DEFAULT_DIET_TYPE
            val selectedDietTypeId = preferences[PreferenceKeys.selectedDietTypeId] ?: 0
            val selectedCuisineType = preferences[PreferenceKeys.selectedCuisineType] ?: DEFAULT_CUISINE_TYPE
            val selectedCuisineTypeId = preferences[PreferenceKeys.selectedCuisineTypeId] ?: 0
            MealDietCuisineType(
                selectedMealType,
                selectedMealTypeId,
                selectedDietType,
                selectedDietTypeId,
                selectedDietType,
                selectedCuisineTypeId
            )
        }

    val readBackOnline: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val backOnline = preferences[PreferenceKeys.backOnline] ?: false
            backOnline
        }

}

data class MealDietCuisineType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int,
    val selectedCuisineType: String,
    val selectedCuisineTypeId: Int
)