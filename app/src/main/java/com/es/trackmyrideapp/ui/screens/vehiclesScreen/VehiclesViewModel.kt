package com.es.trackmyrideapp.ui.screens.vehiclesScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
import com.es.trackmyrideapp.core.states.UiState
import com.es.trackmyrideapp.data.remote.dto.VehicleUpdateDTO
import com.es.trackmyrideapp.data.remote.mappers.Resource
import com.es.trackmyrideapp.domain.model.Vehicle
import com.es.trackmyrideapp.domain.usecase.vehicles.CreateInitialVehiclesUseCase
import com.es.trackmyrideapp.domain.usecase.vehicles.GetAllVehiclesUseCase
import com.es.trackmyrideapp.domain.usecase.vehicles.UpdateVehicleUseCase
import com.es.trackmyrideapp.ui.components.VehicleFilter
import com.es.trackmyrideapp.ui.components.VehicleType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehiclesViewModel @Inject constructor(
    private val createInitialVehiclesUseCase: CreateInitialVehiclesUseCase,
    private val getAllVehiclesUseCase: GetAllVehiclesUseCase,
    private val updateVehicleUseCase: UpdateVehicleUseCase
) : ViewModel() {

    var name by mutableStateOf("")
        private set

    var brand by mutableStateOf("")
        private set

    var model by mutableStateOf("")
        private set

    var year by mutableStateOf("")
        private set

    var fuelType by mutableStateOf("")
        private set

    var tankCapacity by mutableStateOf("")
        private set

    var efficiency by mutableStateOf("")
        private set

    var notes by mutableStateOf("")
        private set

    var bikeType by mutableStateOf("")
        private set

    private val _selectedFilter = MutableStateFlow<VehicleFilter>(VehicleFilter.Type(VehicleType.CAR))
    val selectedFilter: StateFlow<VehicleFilter> = _selectedFilter.asStateFlow()

    // Estado para almacenar todos los vehículos
    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles

    // UI State
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // Ui Messages
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage

    fun consumeUiMessage() {
        _uiMessage.value = null
    }

    // Funciones para actualizar estados
    fun updateName(newName: String) { name = newName }
    fun updateBrand(newBrand: String) { brand = newBrand }
    fun updateModel(newModel: String) { model = newModel }
    fun updateYear(newYear: String) { year = newYear }
    fun updateFuelType(newFuelType: String) { fuelType = newFuelType }
    fun updateTankCapacity(newTankCapacity: String) { tankCapacity = newTankCapacity }
    fun updateEfficiency(newEfficiency: String) { efficiency = newEfficiency }
    fun updateNotes(newNotes: String) { notes = newNotes }
    fun updateBikeType(newBikeType: String) { bikeType = newBikeType }
    fun updateSelectedFilter(filter: VehicleFilter) {
        _selectedFilter.value = filter
        loadVehicleDataForSelectedType()
    }

    init {
        Log.d("VehiclesViewModel", "Init viewmodel ")
        loadUserVehicles()
    }

    private fun loadUserVehicles() {
        Log.d("flujotest", "loaduserbehicle llamado ")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Primero intentar obtener los vehiculos existentes
                when (val result = getAllVehiclesUseCase()) {
                    is Resource.Success -> {
                        if (result.data.isNotEmpty()) {
                            Log.d("VehiclesViewModel", "DATA: ${result.data} ")
                            _vehicles.value = result.data
                            loadVehicleDataForSelectedType()
                        } else {
                            Log.d("VehiclesViewModel", "createInitialVehicles CALLED ")
                            // Si no hay, crear los iniciales
                            createInitialVehicles()
                        }
                        _uiState.value = UiState.Idle
                    }
                    is Resource.Error -> {
                        _uiMessage.value = UiMessage("Error loading vehicles. Please try again later", MessageType.ERROR)
                        _uiState.value = UiState.Idle
                    }
                }
            } catch (e: Exception) {
                Log.d("VehiclesViewModel", "loadUserVehicles failed. ${e.message} ")
                _uiMessage.value = UiMessage(e.message ?: "Unknown error", MessageType.ERROR)
                _uiState.value = UiState.Idle
            }
        }
    }

    private suspend fun createInitialVehicles() {
        Log.d("flujotest", "createinnitialvehicles llamado ")
        when (val result = createInitialVehiclesUseCase()) {
            is Resource.Success -> {
                Log.d("VehiclesViewModel", "createInitialVehicles succes ")
                _vehicles.value = result.data
                loadVehicleDataForSelectedType()
                
            }
            is Resource.Error -> {
                Log.d("VehiclesViewModel", "createInitialVehicles error ${result.message} ")
                _uiState.value = UiState.Idle
            }
        }
    }

    private fun loadVehicleDataForSelectedType() {
        when (val filter = selectedFilter.value) {
            is VehicleFilter.Type -> {
                val vehicle = _vehicles.value.firstOrNull { it.type == filter.type }
                vehicle?.let {
                    name = it.name
                    brand = it.brand
                    model = it.model
                    year = it.year.toString()
                    notes = it.notes ?: ""

                    when (filter.type) {
                        VehicleType.CAR, VehicleType.MOTORCYCLE -> {
                            fuelType = it.fuelType ?: ""
                            tankCapacity = it.tankCapacity?.toString() ?: ""
                            efficiency = it.efficiency?.toString() ?: ""
                        }
                        VehicleType.BIKE -> {
                            bikeType = "" // No hay tipo específico en el modelo Vehicle actual
                        }
                    }
                } ?: run {
                    // Si no hay vehículo de este tipo, resetear campos
                    resetFormFields()
                }
            }
            VehicleFilter.All -> {
                // No hacer nada en este caso
            }
        }
    }

    private fun resetFormFields() {
        name = ""
        brand = ""
        model = ""
        year = ""
        fuelType = ""
        tankCapacity = ""
        efficiency = ""
        notes = ""
        bikeType = ""
    }


    fun updateVehicle() {
        Log.d("flujotest", "updatevehicle llamado ")
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val type = when (val filter = selectedFilter.value) {
                is VehicleFilter.Type -> filter.type
                VehicleFilter.All -> {
                    _uiState.value = UiState.Idle
                    _uiMessage.value = UiMessage("Please select a vehicle type", MessageType.ERROR)
                    
                    return@launch
                }
            }

            val vehicleId = _vehicles.value.firstOrNull { it.type == type }?.id
                ?: run {
                    _uiState.value = UiState.Idle
                    _uiMessage.value = UiMessage("Vehicle selected not found. Trya gain later", MessageType.ERROR)
                    return@launch
                }

            val updateData = VehicleUpdateDTO(
                name = name.ifBlank { null },
                brand = brand.ifBlank { null },
                model = model.ifBlank { null },
                year = year.ifBlank { null },
                type = type,
                fuelType = fuelType.ifBlank { null },
                tankCapacity = tankCapacity.toDoubleOrNull(),
                efficiency = efficiency.toDoubleOrNull(),
                notes = notes.ifBlank { null }
            )

            when (val result = updateVehicleUseCase(type, updateData)) {
                is Resource.Success -> {
                    // Actualizar la lista de vehiculos con los nuevos datos
                    _uiState.value = UiState.Idle
                    _vehicles.value = _vehicles.value.map {
                        if (it.id == vehicleId) result.data else it
                    }
                    
                    _uiMessage.value = UiMessage("Vehicle updated successfully", MessageType.INFO)
                }
                is Resource.Error -> {
                    _uiMessage.value = UiMessage("Vehicle update failed. Try again later.", MessageType.ERROR)
                    _uiState.value = UiState.Idle
                }
            }
        }
    }


    fun validateBeforeSave(): Boolean {
        val currentFilter = selectedFilter.value
        return when {
            name.isBlank() -> {
                _uiMessage.value = UiMessage("Name cannot be empty", MessageType.ERROR)
                false
            }
            brand.isBlank() -> {
                _uiMessage.value = UiMessage("Brand cannot be empty", MessageType.ERROR)
                false
            }
            model.isBlank() -> {
                _uiMessage.value = UiMessage("Model cannot be empty", MessageType.ERROR)
                false
            }
            year.isBlank() || year.toIntOrNull() == null -> {
                _uiMessage.value = UiMessage("Invalid year", MessageType.ERROR)
                false
            }
            currentFilter is VehicleFilter.Type &&
                    currentFilter.type != VehicleType.BIKE &&
                    (fuelType.isBlank() || tankCapacity.isBlank() || efficiency.isBlank()) -> {
                _uiMessage.value = UiMessage("Please fill all engine vehicle fields", MessageType.ERROR)
                false
            }
            currentFilter is VehicleFilter.Type &&
                    currentFilter.type == VehicleType.BIKE &&
                    bikeType.isBlank() -> {
                _uiMessage.value = UiMessage("Please select bike type", MessageType.ERROR)
                false
            }
            else -> true
        }
    }
}