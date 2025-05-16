package com.es.trackmyrideapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.ui.graphics.vector.ImageVector
import com.es.trackmyrideapp.R

sealed class VehicleFilter {
    data object All : VehicleFilter()
    data class Type(val type: VehicleType) : VehicleFilter()
}

enum class VehicleType {
    CAR, MOTORCYCLE, BIKE
}

sealed class VehicleIcon {
    data class Vector(val icon: ImageVector) : VehicleIcon()
    data class PainterIcon(val painter: Int) : VehicleIcon()
}

fun VehicleType.getIcon(): VehicleIcon {
    return when (this) {
        VehicleType.CAR -> VehicleIcon.Vector(Icons.Default.DirectionsCar)
        VehicleType.MOTORCYCLE -> VehicleIcon.PainterIcon(R.drawable.motocicleta)
        VehicleType.BIKE -> VehicleIcon.Vector(Icons.AutoMirrored.Filled.DirectionsBike)
    }
}

fun VehicleType.getLabel(): String {
    return when (this) {
        VehicleType.CAR -> "Car"
        VehicleType.MOTORCYCLE -> "Motorcycle"
        VehicleType.BIKE -> "Bicycle"
    }
}