package com.es.trackmyrideapp.navigation

import kotlinx.serialization.Serializable

/*
Si no se manda parametros, podemos ujsar un objeto. Para mandar parametros, se crea DataClass
 */

/*
- Si solo quiero ir atras: pasar la lambda tambien, y en el wrapper usar navigate.popbackstack()
- Si te navega a otra aplicacion, al darle hacia atras, si tienes navigateup, te lleva a la antigua app, sin embargo si usanmos popbackstack, te navega hacia atras en la nueva aplicacion abierta
- Si qujiero saltar a la primera pantalla estando en la quinta por ejemplo, se debe hacer:
    lo mismo con la lambda, y anvegamos usando navcontroller.navigate(ScreenName){
        popUpTo<ScreenName>{inclusive = true}  --> Esto navega hasta esta vista, eliminando las pantallas del stack que estan en medio.
        Inclusive false -> Sin borrar la ScreenName
        Inclusive true -> Borrando la pantalla primera a la que navegamos (Usar este normalmente)
    }
 */

/*

 */

@Serializable
object Login

@Serializable
object ForgotPassword

@Serializable
object Register

@Serializable
object Home

@Serializable
object RoutesHistory

@Serializable
data class RouteDetails(val routeId: Long)

@Serializable
object Profile

@Serializable
object Premium

@Serializable
object AboutUs

@Serializable
object Vehicles

@Serializable
object AdminScreen


