package com.es.trackmyrideapp.core.extensions


fun Double.round(decimals: Int = 2): Double =
    "%.${decimals}f".format(this).toDouble()