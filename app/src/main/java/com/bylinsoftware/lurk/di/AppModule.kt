package com.bylinsoftware.lurk.di

import android.content.Context
import com.bylinsoftware.lurk.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import dagger.Module
import dagger.Provides
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class])
class AppModule (val context: Context) {
    @Provides @Singleton fun provideAppContext () : Context
        = context

    @Provides @Singleton fun providePicasso (appContext: Context) : RequestCreator
        = Picasso.with(appContext).load(R.drawable.goodfon).centerCrop().fit()
}