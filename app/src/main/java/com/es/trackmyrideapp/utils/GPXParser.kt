package com.es.trackmyrideapp.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Xml
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.google.android.gms.maps.model.LatLng
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.InputStream
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GPXParser {

    fun parseGpx(gpxContent: String): List<LatLng> {
        val points = mutableListOf<LatLng>()
        val parser: XmlPullParser = Xml.newPullParser()
        parser.setInput(StringReader(gpxContent))

        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "trkpt") {
                val lat = parser.getAttributeValue(null, "lat")?.toDoubleOrNull()
                val lon = parser.getAttributeValue(null, "lon")?.toDoubleOrNull()
                if (lat != null && lon != null) {
                    points.add(LatLng(lat, lon))
                }
            }
            eventType = parser.next()
        }
        return points
    }

    fun parseGpxFromInputStream(inputStream: InputStream): List<LatLng> {
        val points = mutableListOf<LatLng>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()

            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            var insideTrackSegment = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "trkseg" -> insideTrackSegment = true
                            "trkpt" -> {
                                if (insideTrackSegment) {
                                    val lat = parser.getAttributeValue(null, "lat")?.toDoubleOrNull()
                                    val lon = parser.getAttributeValue(null, "lon")?.toDoubleOrNull()
                                    if (lat != null && lon != null) {
                                        points.add(LatLng(lat, lon))
                                    }
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "trkseg") {
                            insideTrackSegment = false
                        }
                    }
                }
                eventType = parser.next()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream.close()
        }
        return points
    }

    fun generateGpx(points: List<LatLng>): String {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())
        return buildString {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            append("<gpx version=\"1.1\" creator=\"TrackMyRideApp\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n")
            append("<trk><name>Exported Route</name><trkseg>\n")
            for (point in points) {
                append("<trkpt lat=\"${point.latitude}\" lon=\"${point.longitude}\">\n")
                append("<time>$date</time>\n")
                append("</trkpt>\n")
            }
            append("</trkseg></trk>\n")
            append("</gpx>")
        }
    }

    fun shareRouteAsGpx(context: Context, gpxContent: String) {
        val fileName = "route_${System.currentTimeMillis()}.gpx"
        val file = File(context.getExternalFilesDir(null), fileName)

        file.writeText(gpxContent)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/gpx+xml"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share route as GPX"))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveGpxToDownloads(context: Context, fileName: String, gpxContent: String): Boolean {
        return try {
            val resolver = context.contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/gpx+xml")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: return false

            resolver.openOutputStream(uri).use { outputStream ->
                outputStream?.write(gpxContent.toByteArray())
            }
            true
        } catch (e: Exception) {
            Log.e("GPXExport", "Error al guardar GPX: ${e.message}")
            false
        }
    }
}