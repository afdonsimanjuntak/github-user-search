package io.afdon.favourite.repo

import io.afdon.data.source.room.dao.UserDao
import io.afdon.favourite.mapper.RoomUserToDomainUserMapper
import io.afdon.favourite.mapper.UserToRoomUserMapper
import io.afdon.favourite.model.RequestResult
import io.afdon.favourite.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val roomUserToDomainUserMapper: RoomUserToDomainUserMapper,
    private val userToRoomUserMapper: UserToRoomUserMapper
): FavouriteRepository {

    override fun getFavourites(): Flow<RequestResult<List<User>>> = flow {
        emit(RequestResult.Loading(true))
        try {
            val users = userDao.getAll().mapNotNull {
                roomUserToDomainUserMapper.map(it)
            }
            emit(RequestResult.Success(users))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(RequestResult.Error("Error getting data"))
        } finally {
            emit(RequestResult.Loading(false))
        }
    }

    override fun deleteFavourite(user: User): Flow<RequestResult<User>> = flow {
        emit(RequestResult.Loading(true))
        try {
            userToRoomUserMapper.map(user)?.let {
                userDao.delete(it)
                emit(RequestResult.Success(user))
            } ?: emit(RequestResult.Error("Error saving user"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(RequestResult.Error("Error getting data"))
        } finally {
            emit(RequestResult.Loading(false))
        }
    }
}