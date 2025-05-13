package com.es.trackmyrideapp.utils

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

object GPXParser {

    fun parseWikilocGpx(context: Context, resourceId: Int): List<LatLng> {
        val points = mutableListOf<LatLng>()

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()

            // Abre el archivo desde recursos raw
            val inputStream = context.resources.openRawResource(resourceId)
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

            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return points
    }
}