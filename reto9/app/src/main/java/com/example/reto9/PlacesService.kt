package com.example.reto9

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

/**
 * Servicio para realizar búsquedas de lugares cercanos usando la Nearby Search API de Google Places
 */
class PlacesService {

    private val client = OkHttpClient()

    /**
     * Busca lugares cercanos usando la Nearby Search API REST
     * @param lat Latitud de la ubicación de búsqueda
     * @param lng Longitud de la ubicación de búsqueda
     * @param radiusMeters Radio de búsqueda en metros
     * @param type Tipo de lugar a buscar (hospital, restaurant, etc.)
     * @param apiKey Clave API de Google Places
     * @return Lista de lugares encontrados
     */
    suspend fun findNearbyPlaces(
        lat: Double,
        lng: Double,
        radiusMeters: Int,
        type: String,
        apiKey: String
    ): List<PlaceResult> = withContext(Dispatchers.IO) {
        try {
            val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                    "?location=$lat,$lng&radius=$radiusMeters&type=$type&key=$apiKey"

            Log.d("PlacesService", "Buscando lugares: $url")

            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            Log.d("PlacesService", "Respuesta API: $responseBody")

            if (!response.isSuccessful) {
                Log.e("PlacesService", "Error HTTP: ${response.code}")
                return@withContext emptyList()
            }

            val json = JSONObject(responseBody)

            // Verificar si hay error en la respuesta
            if (json.has("error_message")) {
                Log.e("PlacesService", "Error API: ${json.getString("error_message")}")
                return@withContext emptyList()
            }

            val results = json.getJSONArray("results")
            val places = mutableListOf<PlaceResult>()

            for (i in 0 until results.length()) {
                val obj = results.getJSONObject(i)

                val name = obj.getString("name")
                val geometry = obj.getJSONObject("geometry")
                val location = geometry.getJSONObject("location")
                val placeLat = location.getDouble("lat")
                val placeLng = location.getDouble("lng")

                // Información adicional opcional
                val rating = if (obj.has("rating")) obj.getDouble("rating") else null
                val vicinity = if (obj.has("vicinity")) obj.getString("vicinity") else null

                // Tipos de lugar
                val types = if (obj.has("types")) {
                    val typesArray = obj.getJSONArray("types")
                    (0 until typesArray.length()).map { typesArray.getString(it) }
                } else null

                places.add(
                    PlaceResult(
                        name = name,
                        lat = placeLat,
                        lng = placeLng,
                        rating = rating,
                        vicinity = vicinity,
                        types = types
                    )
                )
            }

            Log.d("PlacesService", "Encontrados ${places.size} lugares")
            places

        } catch (e: Exception) {
            Log.e("PlacesService", "Error al buscar lugares", e)
            emptyList()
        }
    }

    /**
     * Mapea los tipos de lugar de la configuración a los tipos de la API de Google
     */
    fun mapPlaceTypeToApiType(placeType: String): String {
        return when (placeType) {
            "hospital" -> "hospital"
            "restaurant" -> "restaurant"
            "gas_station" -> "gas_station"
            "pharmacy" -> "pharmacy"
            "bank" -> "bank"
            "tourist_attraction" -> "tourist_attraction"
            "store" -> "store"
            "lodging" -> "lodging"
            else -> "hospital"
        }
    }
}
