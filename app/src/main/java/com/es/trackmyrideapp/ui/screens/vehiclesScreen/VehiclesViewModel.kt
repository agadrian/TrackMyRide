package com.es.trackmyrideapp.ui.screens.vehiclesScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.trackmyrideapp.core.states.MessageType
import com.es.trackmyrideapp.core.states.UiMessage
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
    private val _uiState = MutableStateFlow<VehicleUiState>(VehicleUiState.Idle)
    val uiState: StateFlow<VehicleUiState> = _uiState


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
        viewModelScope.launch {
            _uiState.value = VehicleUiState.Loading
            try {
                // Primero intentar obtener los vehiculos existentes
                when (val result = getAllVehiclesUseCase()) {
                    is Resource.Success -> {
                        Log.d("VehiclesViewModel", "Vehicles loaded successfully. ")
                        if (result.data.isNotEmpty()) {
                            Log.d("VehiclesViewModel", "DATA: ${result.data} ")
                            _vehicles.value = result.data
                            Log.d("VehiclesViewModel", "loadVehicleDataForSelectedType CALLED ")
                            loadVehicleDataForSelectedType()
                            _uiState.value = VehicleUiState.Success
                        } else {
                            Log.d("VehiclesViewModel", "createInitialVehicles CALLED ")
                            // Si no hay, crear los iniciales
                            createInitialVehicles()
                        }
                    }
                    is Resource.Error -> {
                        _uiState.value = VehicleUiState.Error(result.message )
                    }
                    Resource.Loading -> Unit // Ya estamos en estado loading
                }
            } catch (e: Exception) {
                Log.d("VehiclesViewModel", "loadUserVehicles failed. ${e.message} ")
                _uiState.value = VehicleUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun createInitialVehicles() {
        when (val result = createInitialVehiclesUseCase()) {
            is Resource.Success -> {
                Log.d("VehiclesViewModel", "createInitialVehicles succes ")
                _vehicles.value = result.data
                loadVehicleDataForSelectedType()
                _uiState.value = VehicleUiState.Success
            }
            is Resource.Error -> {
                Log.d("VehiclesViewModel", "createInitialVehicles error ${result.message} ")
                _uiState.value = VehicleUiState.Error(result.message ?: "Error creating initial vehicles")
            }
            Resource.Loading -> Unit // Ya estamos en estado loading
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
        viewModelScope.launch {
            _uiState.value = VehicleUiState.Loading

            val type = when (val filter = selectedFilter.value) {
                is VehicleFilter.Type -> filter.type
                VehicleFilter.All -> {
                    _uiState.value = VehicleUiState.Error("Please select a vehicle type")
                    return@launch
                }
            }

            val vehicleId = _vehicles.value.firstOrNull { it.type == type }?.id
                ?: run {
                    _uiState.value = VehicleUiState.Error("Vehicle not found")
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
                    _vehicles.value = _vehicles.value.map {
                        if (it.id == vehicleId) result.data else it
                    }
                    _uiState.value = VehicleUiState.Success
                    _uiMessage.value = UiMessage("Vehicle updated successfully", MessageType.INFO)
                }
                is Resource.Error -> {
                    _uiState.value = VehicleUiState.Error(result.message )
                }
                Resource.Loading -> Unit // Ya estamos en estado loading
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
                _uiState.value = VehicleUiState.Error("Brand cannot be empty")
                false
            }
            model.isBlank() -> {
                _uiState.value = VehicleUiState.Error("Model cannot be empty")
                false
            }
            year.isBlank() || year.toIntOrNull() == null -> {
                _uiState.value = VehicleUiState.Error("Invalid year")
                false
            }
            currentFilter is VehicleFilter.Type &&
                    currentFilter.type != VehicleType.BIKE &&
                    (fuelType.isBlank() || tankCapacity.isBlank() || efficiency.isBlank()) -> {
                _uiState.value = VehicleUiState.Error("Please fill all engine vehicle fields")
                false
            }
            currentFilter is VehicleFilter.Type &&
                    currentFilter.type == VehicleType.BIKE &&
                    bikeType.isBlank() -> {
                _uiState.value = VehicleUiState.Error("Please select bike type")
                false
            }
            else -> true
        }
    }
}