package io.afdon.search.repo

import io.afdon.core.logger.Logger
import io.afdon.data.source.api.GithubApi
import io.afdon.data.source.room.dao.UserDao
import io.afdon.search.mapper.GetUserToDomainUserMapper
import io.afdon.search.mapper.SearchItemToUserMapper
import io.afdon.search.mapper.UserToRoomUserMapper
import io.afdon.search.model.RequestResult
import io.afdon.search.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val githubApi: GithubApi,
    private val userDao: UserDao,
    private val searchItemToUserMapper: SearchItemToUserMapper,
    private val userToRoomUserMapper: UserToRoomUserMapper,
    private val getUserToDomainUserMapper: GetUserToDomainUserMapper,
    private val logger: Logger
) : SearchRepository {

    override fun searchUsers(q: String, page: Int, perPage: Int): Flow<RequestResult<List<User>>> =
        flow {
            emit(RequestResult.Loading(true))
            try {
                val response = githubApi.searchUsers(q, page, perPage)
                if (response.isSuccessful) {
                    val list = response.body()?.items?.mapNotNull {
                        searchItemToUserMapper.map(it)
                    }.orEmpty()
                    emit(RequestResult.Success(list, response.body()?.incompleteResults ?: true))
                } else {
                    emit(RequestResult.Error("Request failed!"))
                }
            } catch (e: Exception) {
                logger.log(e)
                emit(RequestResult.Error("Error getting data!"))
            } finally {
                emit(RequestResult.Loading(false))
            }
        }

    override fun getFavouriteUserIds(): Flow<RequestResult<Set<Int>>> = flow {
        emit(RequestResult.Loading(true))
        try {
            emit(RequestResult.Success(userDao.getAllIds().toSet()))
        } catch (e: Exception) {
            logger.log(e)
            emit(RequestResult.Error("Error getting data"))
        } finally {
            emit(RequestResult.Loading(false))
        }
    }

    override fun addFavourite(user: User): Flow<RequestResult<User>> = flow {
        emit(RequestResult.Loading(true))
        try {
            userToRoomUserMapper.map(user)?.let {
                userDao.add(it)
                emit(RequestResult.Success(user))
            } ?: emit(RequestResult.Error("Error saving user"))
        } catch (e: Exception) {
            logger.log(e)
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
            logger.log(e)
            emit(RequestResult.Error("Error getting data"))
        } finally {
            emit(RequestResult.Loading(false))
        }
    }

    override suspend fun getUser(login: String): Flow<RequestResult<User>> = flow  {
        emit(RequestResult.Loading(true))
        try {
            val response = githubApi.getUser(login)
            if (response.isSuccessful) {
                getUserToDomainUserMapper.map(response.body())?.let {
                    emit(RequestResult.Success(it))
                } ?: emit(RequestResult.Error("Empty result"))
            } else {
                emit(RequestResult.Error("Request failed!"))
            }
        } catch (e: Exception) {
            logger.log(e)
            emit(RequestResult.Error("Error getting data"))
        } finally {
            emit(RequestResult.Loading(false))
        }
    }
}