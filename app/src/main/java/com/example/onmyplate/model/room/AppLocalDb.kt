package com.example.onmyplate.model.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.onmyplate.base.MyApplication

@Database(entities = [PartialPost::class], version=1)
abstract  class AppLocalDbRepository : RoomDatabase() {
    abstract fun partialPostData(): PartialPostDao
}

object AppLocalDb {
    val database: AppLocalDbRepository by lazy {
        val context = MyApplication.Globals.context ?: throw IllegalStateException("Application context is missing")

        Room.databaseBuilder(
            context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}