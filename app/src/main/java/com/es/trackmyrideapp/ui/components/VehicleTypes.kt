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
    Car, MotorCycle, Bike
}

sealed class VehicleIcon {
    data class Vector(val icon: ImageVector) : VehicleIcon()
    data class PainterIcon(val painter: Int) : VehicleIcon()
}

fun VehicleType.getIcon(): VehicleIcon {
    return when (this) {
        VehicleType.Car -> VehicleIcon.Vector(Icons.Default.DirectionsCar)
        VehicleType.MotorCycle -> VehicleIcon.PainterIcon(R.drawable.motocicleta)
        VehicleType.Bike -> VehicleIcon.Vector(Icons.AutoMirrored.Filled.DirectionsBike)
    }
}
// Función de extensión para obtener el icono correspondiente al tipo de vehículo
//fun VehicleType.getIcon(): ImageVector {
//    return when (this) {
//        VehicleType.Car -> Icons.Default.DirectionsCar
//        VehicleType.MotorCycle -> painterResource(R.drawable.all_vehicles)
//        VehicleType.Bike -> Icons.AutoMirrored.Filled.DirectionsBike
//    }
//}

fun VehicleType.getLabel(): String {
    return when (this) {
        VehicleType.Car -> "Car"
        VehicleType.MotorCycle -> "Motorcycle"
        VehicleType.Bike -> "Bicycle"
    }
}