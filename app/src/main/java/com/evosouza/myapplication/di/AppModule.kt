package com.evosouza.myapplication.di

import android.content.Context
import androidx.room.Room
import com.evosouza.myapplication.db.RunnigDatabase
import com.evosouza.myapplication.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunnigDatabase::class.java,
        Constants.RUNNING_DATABASE_NAME
    ).build()


    @Singleton
    @Provides
    fun providesRunningDao(db: RunnigDatabase) = db.getRunDao()
}