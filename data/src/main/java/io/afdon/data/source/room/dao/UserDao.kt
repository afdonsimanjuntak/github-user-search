package io.afdon.data.source.room.dao

import androidx.room.*
import io.afdon.data.source.room.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(user: UserEntity)

    @Query("SELECT * FROM user")
    suspend fun getAll(): List<UserEntity>

    @Query("SELECT user.id FROM user")
    suspend fun getAllIds(): List<Int>

    @Query("SELECT * FROM user WHERE id LIKE :id")
    suspend fun findById(id: Int): UserEntity

    @Delete
    suspend fun delete(user: UserEntity)
}