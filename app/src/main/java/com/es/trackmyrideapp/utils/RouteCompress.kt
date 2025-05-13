package com.es.trackmyrideapp.utils

import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.zip.Deflater
import java.util.zip.Inflater


object RouteCompress {

    fun compressRouteWithDelta(points: List<LatLng>): ByteArray {
        if (points.isEmpty()) return byteArrayOf()

        val byteBuffer = ByteBuffer.allocate(points.size * 8) // 8 bytes por punto
        var prevLat = 0.0
        var prevLng = 0.0

        points.forEachIndexed { i, point ->
            // Delta encoding: almacenamos diferencias entre puntos consecutivos
            val deltaLat = if (i == 0) point.latitude else point.latitude - prevLat
            val deltaLng = if (i == 0) point.longitude else point.longitude - prevLng

            byteBuffer.putFloat(deltaLat.toFloat()) // 4 bytes
            byteBuffer.putFloat(deltaLng.toFloat()) // 4 bytes

            prevLat = point.latitude
            prevLng = point.longitude
        }

        // Compresión Zlib (mejor ratio que GZIP para este caso)
        val deflater = Deflater(Deflater.BEST_COMPRESSION)
        deflater.setInput(byteBuffer.array())
        deflater.finish()

        val output = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        while (!deflater.finished()) {
            val count = deflater.deflate(buffer)
            output.write(buffer, 0, count)
        }
        deflater.end()

        return output.toByteArray()
    }

    fun decompressRoute(compressed: ByteArray): List<LatLng> {
        val inflater = Inflater()
        inflater.setInput(compressed)

        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }
        inflater.end()

        val bytes = outputStream.toByteArray()
        val byteBuffer = ByteBuffer.wrap(bytes)
        val points = mutableListOf<LatLng>()
        var lat = 0.0
        var lng = 0.0

        while (byteBuffer.hasRemaining()) {
            lat += byteBuffer.float.toDouble() // Sumamos los deltas
            lng += byteBuffer.float.toDouble()
            points.add(LatLng(lat, lng))
        }

        return points
    }
}