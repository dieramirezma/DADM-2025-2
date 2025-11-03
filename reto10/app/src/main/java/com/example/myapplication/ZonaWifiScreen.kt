package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZonaWifiScreen(viewModel: ZonaWifiViewModel = viewModel()) {
    val zonas by viewModel.zonas.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(false)
    val hayMasDatos by viewModel.hayMasDatos.observeAsState(true)

    var mostrarMapa by remember { mutableStateOf(false) }
    var filtroNombre by remember { mutableStateOf("") }
    var filtroBarrio by remember { mutableStateOf("") }
    var filtroDireccion by remember { mutableStateOf("") }
    var filtroComuna by remember { mutableStateOf("") }
    var filtroTipo by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val tiposZona = listOf("URBANA", "RURAL")

    LaunchedEffect(Unit) {
        viewModel.cargarZonas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zonas WiFi - Bucaramanga") },
                actions = {
                    // Botón para alternar entre lista y mapa
                    IconButton(onClick = { mostrarMapa = !mostrarMapa }) {
                        Text(if (mostrarMapa) "📋" else "🗺️", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // 🔍 Barra de búsqueda por nombre
            OutlinedTextField(
                value = filtroNombre,
                onValueChange = {
                    filtroNombre = it
                    if (it.length >= 3) {
                        viewModel.buscarPorNombre(it)
                    } else if (it.isEmpty()) {
                        viewModel.cargarZonas()
                    }
                },
                label = { Text("Buscar por nombre zona WiFi...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // 🔍 Barra de búsqueda por dirección
            OutlinedTextField(
                value = filtroDireccion,
                onValueChange = {
                    filtroDireccion = it
                    if (it.length >= 3) {
                        viewModel.buscarPorDireccion(it)
                    } else if (it.isEmpty()) {
                        viewModel.cargarZonas()
                    }
                },
                label = { Text("Buscar por dirección...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // 🔍 Barra de búsqueda por barrio
            OutlinedTextField(
                value = filtroBarrio,
                onValueChange = {
                    filtroBarrio = it
                    if (it.length >= 3) {
                        viewModel.buscarPorBarrio(it)
                    } else if (it.isEmpty()) {
                        viewModel.cargarZonas()
                    }
                },
                label = { Text("Buscar por barrio...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // 🔍 Barra de búsqueda por comuna
            OutlinedTextField(
                value = filtroComuna,
                onValueChange = {
                    filtroComuna = it
                    if (it.length >= 2) {
                        viewModel.buscarPorComuna(it)
                    } else if (it.isEmpty()) {
                        viewModel.cargarZonas()
                    }
                },
                label = { Text("Buscar por comuna...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // 🔍 Dropdown para zona urbana/rural
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (filtroTipo.isEmpty()) "Seleccionar tipo de zona"
                        else "Tipo: $filtroTipo"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todas") },
                        onClick = {
                            filtroTipo = ""
                            expanded = false
                            viewModel.cargarZonas()
                        }
                    )
                    tiposZona.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo) },
                            onClick = {
                                filtroTipo = tipo
                                expanded = false
                                viewModel.filtrarPorTipoZona(tipo)
                            }
                        )
                    }
                }
            }

            // Botón para limpiar todos los filtros
            OutlinedButton(
                onClick = {
                    filtroNombre = ""
                    filtroBarrio = ""
                    filtroDireccion = ""
                    filtroComuna = ""
                    filtroTipo = ""
                    viewModel.cargarZonas()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Limpiar filtros")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            if (mostrarMapa) {
                // 🗺️ Vista de mapa
                MapaZonasScreen(zonas = zonas)
            } else {
                // 📋 Vista de lista
                if (loading && zonas.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Mostrar cantidad de resultados
                    Text(
                        text = "${zonas.size} zona(s) encontrada(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    LazyColumn {
                        items(zonas) { zona ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        zona.nombre_zona_wifi ?: "Sin nombre",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("📍 Dirección: ${zona.direccion ?: "N/A"}")
                                    Text("🏘️ Barrio: ${zona.barrio ?: "N/A"}")
                                    Text("🏛️ Comuna: ${zona.comuna ?: "N/A"}")
                                    Text("🌆 Zona: ${zona.zona_urbana_rural ?: "N/A"}")
                                    if (zona.latitud != null && zona.longitud != null) {
                                        Text("🗺️ Coordenadas: ${zona.latitud}, ${zona.longitud}")
                                    }
                                }
                            }
                        }

                        // Botón "Cargar más" al final de la lista
                        if (hayMasDatos) {
                            item {
                                Button(
                                    onClick = { viewModel.cargarMasZonas() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    enabled = !loading
                                ) {
                                    if (loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Text(if (loading) "Cargando..." else "Cargar más zonas")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
