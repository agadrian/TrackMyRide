package com.es.trackmyrideapp.utils

object TimeFormatter{

    fun formatSecondsToHhMmSs(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return buildString {
            if (h > 0) append("${h}h ")
            if (m > 0) append("${m}m ")
            append("${s}s")
        }.trim()
    }
}
