package com.jihyun.compose_study.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jihyun.compose_study.dao.BookmarkDao
import com.jihyun.compose_study.model.Bookmark

@Database(entities = [Bookmark::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}
