package com.bylinsoftware.lurk.di

import android.content.Context
import androidx.room.Room
import com.bylinsoftware.lurk.db.MainDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DatabaseModule {
    @Provides @Singleton
    fun provideMainDataBase (appContext: Context) : MainDatabase
        =
        Room.
        databaseBuilder(appContext, MainDatabase::class.java, "database")
        .fallbackToDestructiveMigration()
            // if we go too migration that's function clear all data for new struct
        .allowMainThreadQueries()
            .build()
}