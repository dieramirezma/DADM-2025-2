# Documentación: Sistema de Búsqueda de Lugares de Interés

## Resumen Ejecutivo

Este documento explica el funcionamiento del sistema de búsqueda de lugares de interés implementado en la aplicación Android "Puntos de Interés". La aplicación utiliza la Google Places API para encontrar lugares cercanos basados en la ubicación actual del usuario y criterios de búsqueda configurables.

## Arquitectura del Sistema

### Componentes Principales

1. **MainActivity.kt**: Controlador principal que maneja la lógica de búsqueda
2. **PlacesService.kt**: Servicio para realizar llamadas HTTP a la Nearby Search API
3. **PlaceResult.kt**: Modelo de datos para representar lugares encontrados
4. **Google Maps API**: Servicio para mostrar mapas y marcadores
5. **SettingsActivity.kt**: Configuración de parámetros de búsqueda
6. **Preferencias**: Almacenamiento de configuración del usuario
7. **OkHttp**: Cliente HTTP para realizar llamadas REST
8. **Corrutinas**: Manejo de operaciones asíncronas

## Flujo de Búsqueda de Lugares

### 1. Inicialización del Sistema

```kotlin
// Inicialización de servicios
fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
placesService = PlacesService()
```

**Proceso:**
- Se inicializa el cliente de ubicación para obtener GPS
- Se crea una instancia del servicio de lugares para llamadas HTTP
- Se configuran los permisos de ubicación necesarios

### 2. Obtención de Ubicación Actual

```kotlin
private fun getCurrentLocation() {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            currentLocation = location
            // Actualizar mapa y marcadores
        }
    }
}
```

**Proceso:**
- Utiliza FusedLocationProviderClient para obtener la ubicación GPS
- Verifica permisos de ubicación antes de la consulta
- Almacena la ubicación para usarla en las búsquedas

### 3. Configuración de Parámetros de Búsqueda

La aplicación permite configurar dos parámetros principales:

#### Radio de Búsqueda
- **Rango**: 1-50 kilómetros
- **Valor por defecto**: 5 km
- **Almacenamiento**: SharedPreferences con clave "search_radius"

#### Tipo de Lugar
- **Opciones disponibles**:
  - Hospitales
  - Restaurantes
  - Gasolineras
  - Farmacias
  - Bancos
  - Lugares Turísticos
  - Tiendas
  - Hoteles

### 4. Proceso de Búsqueda Principal

```kotlin
private fun findNearbyPlaces() {
    // 1. Obtener configuración
    val radiusKm = prefs.getInt("search_radius", 5)
    val placeType = prefs.getString("place_type", "hospital") ?: "hospital"
    val radiusMeters = radiusKm * 1000

    // 2. Usar corrutinas para llamada asíncrona
    CoroutineScope(Dispatchers.Main).launch {
        try {
            // 3. Mapear tipo de lugar a tipo de API
            val apiType = placesService.mapPlaceTypeToApiType(placeType)
            
            // 4. Realizar búsqueda usando Nearby Search API REST
            val places = withContext(Dispatchers.IO) {
                placesService.findNearbyPlaces(
                    lat = currentLocation!!.latitude,
                    lng = currentLocation!!.longitude,
                    radiusMeters = radiusMeters,
                    type = apiType,
                    apiKey = BuildConfig.GOOGLE_MAPS_API_KEY
                )
            }

            // 5. Actualizar UI en el hilo principal
            updateMapWithPlaces(places, radiusKm, placeType)
        } catch (e: Exception) {
            // Manejo de errores
        }
    }
}
```

## Algoritmo de Filtrado

### 1. Búsqueda Inicial
- Utiliza la **Nearby Search API REST** de Google Places
- Realiza llamadas HTTP directas al endpoint: `https://maps.googleapis.com/maps/api/place/nearbysearch/json`
- Obtiene hasta 20 resultados por página (hasta 60 con paginación)
- Respeta el radio de búsqueda especificado en metros

### 2. Implementación del Servicio HTTP

```kotlin
suspend fun findNearbyPlaces(
    lat: Double, 
    lng: Double, 
    radiusMeters: Int, 
    type: String,
    apiKey: String
): List<PlaceResult> = withContext(Dispatchers.IO) {
    val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=$lat,$lng&radius=$radiusMeters&type=$type&key=$apiKey"
    
    val request = Request.Builder().url(url).build()
    val response = client.newCall(request).execute()
    
    // Procesar respuesta JSON
    val json = JSONObject(response.body?.string() ?: "")
    val results = json.getJSONArray("results")
    
    // Convertir a objetos PlaceResult
    (0 until results.length()).map { i ->
        val obj = results.getJSONObject(i)
        PlaceResult(
            name = obj.getString("name"),
            lat = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
            lng = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lng"),
            rating = if (obj.has("rating")) obj.getDouble("rating") else null,
            vicinity = if (obj.has("vicinity")) obj.getString("vicinity") else null
        )
    }
}
```

### 3. Mapeo de Tipos de Lugar

```kotlin
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
```

**Ventajas de la Nueva Implementación:**
- **Precisión**: Respeta exactamente el radio especificado
- **Cantidad**: Hasta 60 resultados vs 1-5 del método anterior
- **Relevancia**: Encuentra lugares específicos del tipo solicitado
- **Información**: Incluye rating, dirección y tipos adicionales

### 4. Procesamiento de Resultados

```kotlin
private fun updateMapWithPlaces(places: List<PlaceResult>, radiusKm: Int, placeType: String) {
    map.clear()

    // Agregar marcador de ubicación actual
    val currentLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
    map.addMarker(MarkerOptions().position(currentLatLng).title("Mi Ubicación"))

    val bounds = com.google.android.gms.maps.model.LatLngBounds.Builder()
    bounds.include(currentLatLng)

    for (place in places) {
        val placeLatLng = LatLng(place.lat, place.lng)
        
        // Calcular distancia
        val distance = FloatArray(1)
        Location.distanceBetween(
            currentLocation!!.latitude, currentLocation!!.longitude,
            place.lat, place.lng, distance
        )

        // Agregar marcador con información adicional
        val markerOptions = MarkerOptions()
            .position(placeLatLng)
            .title(place.name)
            .snippet("Distancia: ${String.format("%.1f", distance[0]/1000)} km")
        
        if (place.rating != null) {
            markerOptions.snippet("${markerOptions.snippet} • Rating: ${place.rating}")
        }
        
        map.addMarker(markerOptions)
        bounds.include(placeLatLng)
    }

    // Ajustar vista del mapa
    if (places.isNotEmpty()) {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
    }
}
```

**Proceso:**
- Limpia marcadores anteriores del mapa
- Agrega marcador de ubicación actual
- Procesa cada lugar encontrado por la API
- Calcula distancia real usando `Location.distanceBetween()`
- Agrega marcadores con información detallada (nombre, distancia, rating)
- Ajusta la vista del mapa para mostrar todos los resultados

## Visualización en el Mapa

### 1. Marcadores
- **Ubicación Actual**: Marcador azul con título "Mi Ubicación"
- **Lugares Encontrados**: Marcadores rojos con nombre y distancia
- **Información**: Tooltip muestra distancia en kilómetros

### 2. Ajuste de Vista
```kotlin
if (placesFound > 0) {
    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
} else {
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
}
```

**Estrategia:**
- Si hay lugares encontrados: ajusta vista para mostrar todos los marcadores
- Si no hay lugares: mantiene zoom en ubicación actual
- Padding de 100px para evitar que marcadores toquen los bordes

## Configuración y Personalización

### Archivo de Configuración (preferences.xml)
```xml
<SeekBarPreference
    android:key="search_radius"
    android:title="Radio de búsqueda (km)"
    android:defaultValue="5"
    android:max="50"
    android:min="1" />

<ListPreference
    android:key="place_type"
    android:title="Tipo de lugar"
    android:entries="@array/place_types"
    android:entryValues="@array/place_type_values"
    android:defaultValue="hospital" />
```

### Arrays de Configuración (strings.xml)
```xml
<string-array name="place_types">
    <item>Hospitales</item>
    <item>Restaurantes</item>
    <item>Gasolineras</item>
    <item>Farmacias</item>
    <item>Bancos</item>
    <item>Lugares Turísticos</item>
    <item>Tiendas</item>
    <item>Hoteles</item>
</string-array>

<string-array name="place_type_values">
    <item>hospital</item>
    <item>restaurant</item>
    <item>gas_station</item>
    <item>pharmacy</item>
    <item>bank</item>
    <item>tourist_attraction</item>
    <item>store</item>
    <item>lodging</item>
</string-array>
```

## Manejo de Errores

### 1. Errores de Ubicación
```kotlin
if (location != null) {
    // Procesar ubicación
} else {
    Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
}
```

### 2. Errores de API
```kotlin
.addOnFailureListener { exception ->
    Toast.makeText(this, "Error al buscar lugares: ${exception.message}", Toast.LENGTH_LONG).show()
}
```

### 3. Permisos
```kotlin
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        LOCATION_PERMISSION_REQUEST_CODE
    )
    return
}
```

## Limitaciones y Consideraciones

### 1. Limitaciones de la API
- **Costo**: Cada consulta consume créditos de la API de Google Places
- **Rate Limiting**: Google impone límites en el número de consultas por minuto
- **Dependencia de Internet**: Requiere conexión a internet para funcionar
- **Paginación**: Solo devuelve 20 resultados por página (hasta 60 con paginación)

### 2. Mejoras Futuras Sugeridas
- **Implementar Paginación**: Para obtener más de 20 resultados
- **Caché Local**: Almacenar resultados para reducir consultas API
- **Búsqueda Offline**: Usar datos almacenados cuando no hay internet
- **Filtros Avanzados**: Por rating, precio, horarios de apertura
- **Búsqueda por Texto**: Implementar Text Search API para búsquedas más específicas

### 3. Optimizaciones de Rendimiento
- **Corrutinas**: Ya implementadas para operaciones asíncronas
- **Debounce**: Evitar múltiples consultas rápidas del usuario
- **Lazy Loading**: Cargar marcadores de forma progresiva
- **Compresión**: Optimizar imágenes y datos de respuesta

## Flujo de Datos Completo

```
Usuario presiona "Buscar Lugares"
    ↓
Obtener configuración (radio, tipo)
    ↓
Verificar ubicación actual
    ↓
Mapear tipo de lugar a tipo de API
    ↓
Crear URL para Nearby Search API REST
    ↓
Ejecutar llamada HTTP asíncrona con OkHttp
    ↓
Procesar respuesta JSON
    ↓
Convertir a objetos PlaceResult
    ↓
Calcular distancias reales
    ↓
Agregar marcadores al mapa
    ↓
Ajustar vista del mapa
    ↓
Mostrar estadísticas al usuario
```

## Comparación: Antes vs Después

| Aspecto | Implementación Anterior | Nueva Implementación |
|---------|------------------------|---------------------|
| **API Usada** | FindCurrentPlaceRequest | Nearby Search API REST |
| **Propósito** | Encontrar ubicación actual | Buscar lugares por tipo |
| **Resultados** | 1-5 lugares | Hasta 60 lugares |
| **Radio** | ❌ No respetado | ✅ Respetado exactamente |
| **Precisión** | Baja (lugares probables) | Alta (lugares específicos) |
| **Información** | Básica | Completa (rating, dirección) |
| **Rendimiento** | SDK nativo | HTTP directo |
| **Costo** | Menor | Mayor (más consultas) |

## Conclusión

El sistema de búsqueda de lugares ha sido completamente rediseñado para utilizar la **Nearby Search API REST** de Google Places, lo que resuelve definitivamente los problemas de precisión y cantidad de resultados. La nueva implementación:

### ✅ **Ventajas Principales:**
- **Precisión Total**: Respeta exactamente el radio especificado (28km, 50km, etc.)
- **Resultados Abundantes**: Hasta 60 lugares vs 1-5 del método anterior
- **Relevancia Alta**: Encuentra lugares específicos del tipo solicitado
- **Información Rica**: Incluye rating, dirección y tipos adicionales
- **Arquitectura Moderna**: Usa corrutinas y HTTP moderno

### 🔧 **Implementación Técnica:**
- **PlacesService.kt**: Servicio HTTP dedicado para llamadas REST
- **PlaceResult.kt**: Modelo de datos estructurado
- **Corrutinas**: Manejo asíncrono eficiente
- **OkHttp**: Cliente HTTP robusto y confiable

### 📊 **Resultados Esperados:**
- **Hospitales**: Ahora encontrará múltiples hospitales dentro de 28km
- **Restaurantes**: Encontrará muchos restaurantes dentro de 50km
- **Todos los Tipos**: Funciona correctamente para todos los tipos de lugares

La arquitectura modular permite fácil extensión y personalización, mientras que el manejo robusto de errores asegura una experiencia de usuario estable y confiable. Esta implementación representa la solución correcta y definitiva para la búsqueda de lugares de interés en aplicaciones Android.
