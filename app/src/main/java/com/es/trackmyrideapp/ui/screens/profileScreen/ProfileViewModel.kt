package com.es.trackmyrideapp.ui.screens.profileScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.User
import com.es.trackmyrideapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val userState: StateFlow<UserUiState> = _userState.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _userState.value = UserUiState.Loading

            when (val result = userRepository.getUserById(userId)) {
                is Resource.Success -> {
                    result.data?.let {
                        _userState.value = UserUiState.Success(it)
                    } ?: run {
                        _userState.value = UserUiState.Error("Empty user data")
                    }
                }

                is Resource.Error -> {
                    _userState.value = UserUiState.Error(result.message ?: "Unknown error")
                }

                Resource.Loading -> TODO()
            }
        }
    }
}

