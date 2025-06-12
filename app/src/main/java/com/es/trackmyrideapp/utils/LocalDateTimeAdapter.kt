package com.es.trackmyrideapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Adaptador personalizado para Gson que permite serializar y deserializar objetos LocalDateTime.
 * Usa el formato ISO_LOCAL_DATE_TIME para convertir entre String y LocalDateTime.
 * Requiere Android API nivel 26 (Oreo) o superior para usar java.time.
 */
class LocalDateTimeAdapter : JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    /**
     * Deserializa un JSON String en un objeto LocalDateTime.
     *
     * @param json Elemento JSON que contiene la fecha en formato String.
     * @param typeOfT Tipo esperado (LocalDateTime).
     * @param context Contexto de deserialización.
     * @return Objeto LocalDateTime parseado del String JSON.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
        return LocalDateTime.parse(json.asString, formatter)
    }


    /**
     * Serializa un objeto LocalDateTime a su representación JSON como String.
     *
     * @param src Objeto LocalDateTime a serializar.
     * @param typeOfSrc Tipo del objeto fuente.
     * @param context Contexto de serialización.
     * @return JsonPrimitive que contiene la fecha formateada como String.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun serialize(src: LocalDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.format(formatter))
    }
}