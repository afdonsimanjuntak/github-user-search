package io.afdon.data.source.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.afdon.data.source.room.dao.UserDao
import io.afdon.data.source.room.entity.UserEntity

@Database(entities = [UserEntity::class], version = 2)
abstract class AppDb : RoomDatabase() {

    abstract fun userDao(): UserDao
}