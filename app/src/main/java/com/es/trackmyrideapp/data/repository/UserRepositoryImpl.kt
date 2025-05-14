package com.es.trackmyrideapp.data.repository

import com.es.trackmyrideapp.data.remote.api.UserApi
import com.es.trackmyrideapp.data.remote.dto.UserUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.data.remote.mappers.toDomainModel
import com.es.trackmyrideapp.domain.model.User
import com.es.trackmyrideapp.domain.repository.UserRepository
import com.es.trackmyrideapp.utils.safeApiCall
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getAllUsers(): Resource<List<User>> = safeApiCall {
        userApi.getAllUsers().map { it.toDomainModel() }
    }

    override suspend fun getUserById(id: String): Resource<User> = safeApiCall {
        userApi.getUserById(id).toDomainModel()
    }

    override suspend fun updateUser(id: String, userUpdateDTO: UserUpdateDTO): Resource<User> = safeApiCall {
        userApi.updateUser(id, userUpdateDTO).toDomainModel()
    }

    override suspend fun deleteUser(id: String): Resource<Unit> = safeApiCall {
        userApi.deleteUser(id)
    }
}