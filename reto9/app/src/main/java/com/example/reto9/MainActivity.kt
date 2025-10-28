package com.example.reto9

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesService: PlacesService
    private lateinit var tvLocationInfo: TextView
    private lateinit var btnGetLocation: Button
    private lateinit var btnFindPlaces: Button
    private lateinit var btnSettings: Button

    private var currentLocation: Location? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar servicios
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        placesService = PlacesService()

        // Configurar UI
        setupUI()

        // Configurar mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupUI() {
        tvLocationInfo = findViewById(R.id.tvLocationInfo)
        btnGetLocation = findViewById(R.id.btnGetLocation)
        btnFindPlaces = findViewById(R.id.btnFindPlaces)
        btnSettings = findViewById(R.id.btnSettings)

        btnGetLocation.setOnClickListener {
            getCurrentLocation()
        }

        btnFindPlaces.setOnClickListener {
            if (currentLocation != null) {
                findNearbyPlaces()
            } else {
                Toast.makeText(this, "Primero obtén tu ubicación", Toast.LENGTH_SHORT).show()
            }
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Configurar mapa
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        // Solicitar permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val latLng = LatLng(location.latitude, location.longitude)

                // Mover cámara a la ubicación actual
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                // Agregar marcador de ubicación actual
                map.clear()
                map.addMarker(MarkerOptions()
                    .position(latLng)
                    .title("Mi Ubicación"))

                // Actualizar información
                tvLocationInfo.text = "Lat: ${String.format("%.4f", location.latitude)}, " +
                        "Lng: ${String.format("%.4f", location.longitude)}"

                Toast.makeText(this, "Ubicación obtenida", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findNearbyPlaces() {
        if (currentLocation == null) {
            Toast.makeText(this, "Primero obtén tu ubicación", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val radiusKm = prefs.getInt("search_radius", 5)
        val placeType = prefs.getString("place_type", "hospital") ?: "hospital"

        // Convertir radio de km a metros
        val radiusMeters = radiusKm * 1000

        // Mostrar indicador de carga
        tvLocationInfo.text = "Buscando lugares..."
        Toast.makeText(this, "Buscando lugares cercanos...", Toast.LENGTH_SHORT).show()

        // Usar corrutinas para la llamada asíncrona
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Mapear tipo de lugar a tipo de API
                val apiType = placesService.mapPlaceTypeToApiType(placeType)

                // Realizar búsqueda usando Nearby Search API
                val places = withContext(Dispatchers.IO) {
                    placesService.findNearbyPlaces(
                        lat = currentLocation!!.latitude,
                        lng = currentLocation!!.longitude,
                        radiusMeters = radiusMeters,
                        type = apiType,
                        apiKey = BuildConfig.GOOGLE_MAPS_API_KEY
                    )
                }

                // Actualizar UI en el hilo principal
                updateMapWithPlaces(places, radiusKm, placeType)

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error al buscar lugares: ${e.message}", Toast.LENGTH_LONG).show()
                tvLocationInfo.text = "Error en la búsqueda"
            }
        }
    }

    private fun updateMapWithPlaces(places: List<PlaceResult>, radiusKm: Int, placeType: String) {
        map.clear()

        // Agregar marcador de ubicación actual
        val currentLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        map.addMarker(MarkerOptions()
            .position(currentLatLng)
            .title("Mi Ubicación"))

        val bounds = com.google.android.gms.maps.model.LatLngBounds.Builder()
        bounds.include(currentLatLng)

        var placesFound = 0

        for (place in places) {
            val placeLatLng = LatLng(place.lat, place.lng)

            // Calcular distancia
            val distance = FloatArray(1)
            Location.distanceBetween(
                currentLocation!!.latitude, currentLocation!!.longitude,
                place.lat, place.lng,
                distance
            )

            // Agregar marcador
            val markerOptions = MarkerOptions()
                .position(placeLatLng)
                .title(place.name)
                .snippet("Distancia: ${String.format("%.1f", distance[0]/1000)} km")

            // Agregar información adicional si está disponible
            if (place.rating != null) {
                markerOptions.snippet("${markerOptions.snippet} • Rating: ${place.rating}")
            }

            map.addMarker(markerOptions)
            bounds.include(placeLatLng)
            placesFound++
        }

        // Ajustar cámara para mostrar todos los marcadores
        try {
            if (placesFound > 0) {
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
            } else {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        } catch (e: Exception) {
            // Si no hay suficientes puntos, mantener zoom actual
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        }

        val placeTypeName = when (placeType) {
            "hospital" -> "hospitales"
            "restaurant" -> "restaurantes"
            "gas_station" -> "gasolineras"
            "pharmacy" -> "farmacias"
            "bank" -> "bancos"
            "tourist_attraction" -> "lugares turísticos"
            "store" -> "tiendas"
            "lodging" -> "hoteles"
            else -> placeType
        }

        tvLocationInfo.text = "Encontrados $placesFound $placeTypeName en un radio de ${radiusKm}km"
        Toast.makeText(this, "Encontrados $placesFound $placeTypeName", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (::map.isInitialized) {
                    map.isMyLocationEnabled = true
                }
            } else {
                Toast.makeText(this, "Permisos de ubicación requeridos", Toast.LENGTH_LONG).show()
            }
        }
    }
}