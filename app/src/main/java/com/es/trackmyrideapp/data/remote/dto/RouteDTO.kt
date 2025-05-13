package com.es.trackmyrideapp.data.remote.dto

// DTO para los datos de la ruta
data class RouteDTO(
    val id: Long,                   // Identificador único de la ruta
    val name: String,               // Nombre de la ruta
    val description: String?,       // Descripción de la ruta (opcional)
    val startPoint: String,         // Calle del punto de inicio
    val endPoint: String,           // Calle del punto final
    val startTime: String,          // Hora de inicio (Formato ISO 8601, ej. "2025-05-09T10:15:30")
    val endTime: String,            // Hora de finalización (Formato ISO 8601)
    val duration: Long,             // Duración de la ruta en milisegundos
    val distance: Float,            // Distancia total en kilómetros
    val averageSpeed: Float,        // Velocidad media en km/h
    val maxSpeed: Float,            // Velocidad máxima alcanzada en km/h
    val fuelConsumed: Float,        // Combustible consumido en litros
    val efficiency: Float,          // Eficiencia
    val images: List<String>?       // Lista de URLs de las imágenes asociadas a la ruta (opcional)
)