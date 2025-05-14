package com.es.trackmyrideapp.domain.usecase.users

import com.es.trackmyrideapp.data.remote.dto.UserUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.User
import com.es.trackmyrideapp.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String, userUpdateDTO: UserUpdateDTO): Resource<User> {
        return userRepository.updateUser(id, userUpdateDTO)
    }
}