package com.es.trackmyrideapp.ui.screens.adminScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.User
import com.es.trackmyrideapp.domain.usecase.users.DeleteUserUseCase
import com.es.trackmyrideapp.domain.usecase.users.GetAllUsersUseCase
import com.es.trackmyrideapp.domain.usecase.users.ToggleUserPremiumByAdminUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val toggleUserPremiumByAdminUseCase: ToggleUserPremiumByAdminUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
) : ViewModel() {

    private val _users = mutableStateOf<List<User>>(emptyList())
    val users: State<List<User>> = _users

    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Idle)
    val uiState: StateFlow<AdminUiState> = _uiState

    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage

    init {
        loadUsers()
    }

    private fun loadUsers() {
        _uiState.value = AdminUiState.Loading
        viewModelScope.launch {
            when (val result = getAllUsersUseCase()) {
                is Resource.Success -> {
                    _uiState.value = AdminUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = AdminUiState.Idle
                    _uiMessage.value = UiMessage(result.message, MessageType.ERROR)
                }

                Resource.Loading -> TODO()
            }
        }
    }

    fun togglePremium(userId: String) {
        _uiState.value = AdminUiState.Loading
        viewModelScope.launch {
            when (val result = toggleUserPremiumByAdminUseCase(userId)) {
                is Resource.Success -> {
                    _uiMessage.value = UiMessage("Premium state updated successfully", MessageType.INFO)
                    loadUsers()
                }
                is Resource.Error -> {
                    _uiState.value = AdminUiState.Idle
                    _uiMessage.value = UiMessage(result.message, MessageType.ERROR)
                }

                Resource.Loading -> TODO()
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            when(val result = deleteUserUseCase(userId)) {
                is Resource.Success -> {
                    // Recarga usuarios o actualiza el estado para reflejar cambio
                    loadUsers()
                    _uiMessage.value = UiMessage("User deleted successfully", MessageType.INFO)
                }
                is Resource.Error -> {
                    _uiMessage.value = UiMessage(result.message ?: "Error deleting user", MessageType.ERROR)
                }

                Resource.Loading -> TODO()
            }
            _uiState.value = AdminUiState.Idle
        }
    }

    fun consumeUiMessage() {
        _uiMessage.value = null
    }
}