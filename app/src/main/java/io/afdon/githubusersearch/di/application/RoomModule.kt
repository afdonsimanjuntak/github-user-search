package io.afdon.githubusersearch.di.application

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import io.afdon.data.source.room.dao.UserDao
import io.afdon.data.source.room.db.AppDb
import javax.inject.Singleton

@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideAppDb(context: Context) : AppDb =
        Room.databaseBuilder(context, AppDb::class.java, "app_db").build()

    @Singleton
    @Provides
    fun provideUserDao(appDb: AppDb) : UserDao = appDb.userDao()
}