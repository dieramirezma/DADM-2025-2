package com.example.reto9

/**
 * Modelo de datos para representar un lugar encontrado por la Nearby Search API
 */
data class PlaceResult(
    val name: String,
    val lat: Double,
    val lng: Double,
    val rating: Double? = null,
    val vicinity: String? = null,
    val types: List<String>? = null
)
