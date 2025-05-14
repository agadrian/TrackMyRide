package com.es.trackmyrideapp.domain.repository

import com.es.trackmyrideapp.data.remote.dto.UserUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.User

interface UserRepository {
    suspend fun getAllUsers(): Resource<List<User>>
    suspend fun getUserById(id: String): Resource<User>
    suspend fun updateUser(id: String, userUpdateDTO: UserUpdateDTO): Resource<User>
    suspend fun deleteUser(id: String): Resource<Unit>
}