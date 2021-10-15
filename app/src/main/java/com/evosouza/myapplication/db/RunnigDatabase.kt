package com.evosouza.myapplication.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RunnigDatabase: RoomDatabase() {
    abstract fun getRunDao(): RunDao
}