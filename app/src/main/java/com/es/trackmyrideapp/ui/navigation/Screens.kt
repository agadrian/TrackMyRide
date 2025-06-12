package com.es.trackmyrideapp.ui.navigation

import kotlinx.serialization.Serializable


/*
SOBRE LA NAVEGACIÓN EN COMPOSE:

- Si no se deben pasar parámetros a una pantalla, se puede declarar como `object`.
- Si una pantalla necesita parámetros (por ejemplo, un ID), se define como `data class` y debe ser `@Serializable`.

NOTAS SOBRE EL USO DE LA NAVEGACIÓN:

- Para volver a la pantalla anterior:
    -> Usar una lambda que invoque `navController.popBackStack()`

- Si se ha navegado a otra app y se desea evitar que "atrás" regrese a la app anterior:
    -> Usar `popBackStack()` en lugar de `navigateUp()` para permanecer dentro de la app actual.

- Para ir directamente a una pantalla específica (saltando otras del stack):
    navController.navigate(ScreenName) {
        popUpTo<ScreenName> {
            inclusive = true // Borra la pantalla objetivo también
        }
    }
    Esto elimina todas las pantallas en el back stack hasta `ScreenName`, inclusive si se indica.
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

// Pantalla que requiere de parámetros
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


