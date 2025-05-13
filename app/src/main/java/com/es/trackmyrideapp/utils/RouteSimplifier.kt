package com.es.trackmyrideapp.utils

import com.google.android.gms.maps.model.LatLng
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier
import kotlin.random.Random


object RouteSimplifier {

    private val geometryFactory = GeometryFactory()

    fun simplify(points: List<LatLng>, tolerance: Double): List<LatLng> {
        if (points.size < 3) return points

        val coords = points.map { Coordinate(it.longitude, it.latitude) }.toTypedArray()
        val lineString = geometryFactory.createLineString(coords)
        val simplified = DouglasPeuckerSimplifier.simplify(lineString, tolerance) as LineString

        return simplified.coordinates.map { LatLng(it.y, it.x) }
    }

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
}