package com.example.myapplication

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapaZonasScreen(zonas: List<ZonaWifi>) {
    val context = LocalContext.current

    // Log para depuración
    Log.d("MapaZonas", "Total de zonas recibidas: ${zonas.size}")

    // Convertir coordenadas DMS a decimal y filtrar las válidas
    val zonasConCoordenadas = zonas.mapNotNull { zona ->
        val latDecimal = CoordenadasUtils.coordenadaADecimal(zona.latitud, esLatitud = true)
        val lonDecimal = CoordenadasUtils.coordenadaADecimal(zona.longitud, esLatitud = false)

        if (latDecimal != null && lonDecimal != null) {
            Log.d("MapaZonas", "✓ ${zona.nombre_zona_wifi}")
            Log.d("MapaZonas", "  DMS: lat=${zona.latitud}, lon=${zona.longitud}")
            Log.d("MapaZonas", "  Decimal: lat=$latDecimal, lon=$lonDecimal")
            Triple(zona, latDecimal, lonDecimal)
        } else {
            Log.w("MapaZonas", "✗ No se pudo convertir: ${zona.nombre_zona_wifi}")
            Log.w("MapaZonas", "  lat=${zona.latitud}, lon=${zona.longitud}")
            null
        }
    }

    Log.d("MapaZonas", "Zonas con coordenadas válidas: ${zonasConCoordenadas.size}")

    // Configurar OSMDroid una sola vez
    DisposableEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = context.packageName
        onDispose { }
    }

    if (zonasConCoordenadas.isEmpty()) {
        // Mostrar mensaje si no hay zonas con coordenadas
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No hay zonas WiFi con coordenadas para mostrar en el mapa.\nTotal de zonas: ${zonas.size}",
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    // Centrar en Bucaramanga
                    controller.setZoom(13.0)
                    controller.setCenter(GeoPoint(7.12539, -73.1198))

                    // Agregar marcadores
                    zonasConCoordenadas.forEach { (zona, lat, lon) ->
                        Log.d("MapaZonas", "Agregando marcador: ${zona.nombre_zona_wifi} en ($lat, $lon)")

                        val marker = Marker(this).apply {
                            position = GeoPoint(lat, lon)
                            title = zona.nombre_zona_wifi ?: "Zona WiFi"
                            snippet = buildString {
                                append("📍 ${zona.direccion ?: "N/A"}\n")
                                append("🏘️ Barrio: ${zona.barrio ?: "N/A"}\n")
                                append("🏛️ Comuna: ${zona.comuna ?: "N/A"}")
                            }
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        overlays.add(marker)
                    }

                    invalidate()
                    Log.d("MapaZonas", "Mapa creado con ${overlays.size} marcadores")
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { map ->
                // Limpiar y actualizar marcadores cuando cambien las zonas
                map.overlays.clear()

                zonasConCoordenadas.forEach { (zona, lat, lon) ->
                    val marker = Marker(map).apply {
                        position = GeoPoint(lat, lon)
                        title = zona.nombre_zona_wifi ?: "Zona WiFi"
                        snippet = buildString {
                            append("📍 ${zona.direccion ?: "N/A"}\n")
                            append("🏘️ Barrio: ${zona.barrio ?: "N/A"}\n")
                            append("🏛️ Comuna: ${zona.comuna ?: "N/A"}")
                        }
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    map.overlays.add(marker)
                }

                map.invalidate()
                Log.d("MapaZonas", "Mapa actualizado con ${map.overlays.size} marcadores")
            }
        )
    }
}
