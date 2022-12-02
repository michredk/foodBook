package com.example.foodfoodapp.di

import android.content.Context
import androidx.room.Room
import com.example.foodfoodapp.data.database.RecipesDatabase
import com.example.foodfoodapp.util.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RecipesDatabase::class.java,
        DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideRecipesDao(database: RecipesDatabase) = database.recipesDao()

    @Singleton
    @Provides
    fun provideFavoritesDao(database: RecipesDatabase) = database.favoritesDao()

    @Singleton
    @Provides
    fun provideCalendarDao(database: RecipesDatabase) = database.calendarDao()

}