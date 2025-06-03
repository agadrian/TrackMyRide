package com.es.trackmyrideapp.utils

import android.util.Base64
import com.google.android.gms.maps.model.LatLng
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.zip.Deflater
import java.util.zip.Inflater


object RouteSimplifier {
    /**
     * Comprimir ruta:
     * - 1º DouglasPeuckerSimplifier(points, 0.00005) -> Simplified
     * - 2º compressRouteWithDelta(simplifiedPoints) -> BinaryData
     * - 3º Base64.encodeToString(binaryData, Base64.NO_WRAP) -> Base 64
     */


    private val geometryFactory = GeometryFactory()

    fun compressRoute(points: List<LatLng>, tolerance: Double): String {

        // Simplificar la ruta utilizando Douglas-Peucker
        val coords = points.map { Coordinate(it.longitude, it.latitude) }.toTypedArray()
        val lineString = geometryFactory.createLineString(coords)
        val simplified = DouglasPeuckerSimplifier.simplify(lineString, tolerance) as LineString

        // Comprimir la ruta usando delta encoding
        val binaryData = compressRouteWithDelta(simplified.coordinates.map { LatLng(it.y, it.x) })

        // Simplificarla de delta encoding a base64
        val routeInBase64 = Base64.encodeToString(binaryData, Base64.NO_WRAP)
        return routeInBase64
    }

    fun decompressRoute(routeInBase64: String): List<LatLng> {
        // Base 64 a binaryDelta
        val binaryData = Base64.decode(routeInBase64, Base64.NO_WRAP)

        // Delta a latlng
        val points = decompressRouteWithDelta(binaryData)
        return points
    }


    private fun compressRouteWithDelta(points: List<LatLng>): ByteArray {
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

    private fun decompressRouteWithDelta(compressed: ByteArray): List<LatLng> {
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

    /*
    fun generateRealisticSampleRoute(startLat: Double, startLng: Double, pointCount: Int = 34): List<LatLng> {
        val route = mutableListOf<LatLng>()

        var currentLat = startLat
        var currentLng = startLng

        val random = Random.Default

        for (i in 0 until pointCount) {
            // Movimiento principal hacia el noreste
            val latStep = random.nextDouble(0.0005, 0.0015)
            val lngStep = random.nextDouble(0.0005, 0.0015)

            // Variación aleatoria en dirección para que no sea línea recta
            val curveFactorLat = random.nextDouble(-0.0003, 0.0003)
            val curveFactorLng = random.nextDouble(-0.0003, 0.0003)

            currentLat += latStep + curveFactorLat
            currentLng += lngStep + curveFactorLng

            route.add(LatLng(currentLat, currentLng))
        }

        return route
    }

     */
}