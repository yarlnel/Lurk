package com.bylinsoftware.lurk.di

import com.bylinsoftware.lurk.HistoryActivity
import com.bylinsoftware.lurk.MainActivity
import com.bylinsoftware.lurk.SavedArticleActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject (mainActivity: MainActivity)
    fun inject (savedArticleActivity: SavedArticleActivity)
    fun inject (historyActivity: HistoryActivity)
}