package com.bylinsoftware.lurk

import android.app.Application
import android.content.Context
import com.bylinsoftware.lurk.di.AppComponent
import com.bylinsoftware.lurk.di.AppModule
import com.bylinsoftware.lurk.di.DaggerAppComponent

class MainApp : Application () {
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        appComponent = DaggerAppComponent
            . builder()
            . appModule(AppModule(applicationContext))
            . build()

        super.onCreate()

    }
}

val Context.appComponent : AppComponent
    get() = when (this) {
        is MainApp -> appComponent
        else -> this.applicationContext.appComponent
    }

