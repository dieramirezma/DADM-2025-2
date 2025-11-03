package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import kotlinx.coroutines.launch

class ZonaWifiViewModel : ViewModel() {

    private val _zonas = MutableLiveData<List<ZonaWifi>>()
    val zonas: LiveData<List<ZonaWifi>> = _zonas

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    // 📄 Variables para paginación
    private var offset = 0
    private val limit = 20
    private val _hayMasDatos = MutableLiveData(true)
    val hayMasDatos: LiveData<Boolean> = _hayMasDatos

    fun cargarZonas() {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                offset = 0 // Resetear paginación
                val data = RetrofitClient.api.obtenerZonasPaginadas(limit, offset)
                _zonas.postValue(data)
                offset += limit
                _hayMasDatos.postValue(data.size >= limit)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // 📄 Cargar más zonas (paginación)
    fun cargarMasZonas() {
        if (_loading.value == true || _hayMasDatos.value == false) return

        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val nuevas = RetrofitClient.api.obtenerZonasPaginadas(limit, offset)
                val actuales = _zonas.value ?: emptyList()
                _zonas.postValue(actuales + nuevas)
                offset += limit
                _hayMasDatos.postValue(nuevas.size >= limit)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // 🔍 Búsqueda genérica usando SoQL
    fun buscarPorCampo(where: String) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = RetrofitClient.api.buscarPorCampo(where)
                _zonas.postValue(data)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // 🔍 Búsqueda por barrio usando SoQL
    fun buscarPorBarrio(barrio: String) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val query = "upper(barrio) like upper('%$barrio%')"
                val data = RetrofitClient.api.buscarPorBarrio(query)
                _zonas.postValue(data)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // 🔍 Búsqueda por dirección
    fun buscarPorDireccion(direccion: String) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val where = "upper(direccion) like upper('%$direccion%')"
                val data = RetrofitClient.api.buscarPorCampo(where)
                _zonas.postValue(data)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // 🔍 Búsqueda por nombre de zona WiFi
    fun buscarPorNombre(nombre: String) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val where = "upper(nombre_zona_wifi) like upper('%$nombre%')"
                val data = RetrofitClient.api.buscarPorCampo(where)
                _zonas.postValue(data)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // 🔍 Filtrar por tipo de zona (urbana/rural)
    fun filtrarPorTipoZona(tipo: String) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val data = RetrofitClient.api.getPorTipoZona(tipo)
                _zonas.postValue(data)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // 🔍 Filtrar por comuna
    fun buscarPorComuna(comuna: String) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val where = "upper(comuna) like upper('%$comuna%')"
                val data = RetrofitClient.api.buscarPorCampo(where)
                _zonas.postValue(data)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // 🔍 Búsqueda combinada con múltiples filtros
    fun buscarConFiltrosCombinados(
        nombre: String = "",
        barrio: String = "",
        direccion: String = "",
        comuna: String = "",
        tipo: String = ""
    ) {
        viewModelScope.launch {
            try {
                _loading.postValue(true)
                val where = buildString {
                    if (nombre.isNotEmpty()) {
                        append("upper(nombre_zona_wifi) like upper('%$nombre%')")
                    }
                    if (barrio.isNotEmpty()) {
                        if (isNotEmpty()) append(" AND ")
                        append("upper(barrio) like upper('%$barrio%')")
                    }
                    if (direccion.isNotEmpty()) {
                        if (isNotEmpty()) append(" AND ")
                        append("upper(direccion) like upper('%$direccion%')")
                    }
                    if (comuna.isNotEmpty()) {
                        if (isNotEmpty()) append(" AND ")
                        append("upper(comuna) like upper('%$comuna%')")
                    }
                    if (tipo.isNotEmpty()) {
                        if (isNotEmpty()) append(" AND ")
                        append("zona_urbana_rural='$tipo'")
                    }
                }

                val data = if (where.isEmpty()) {
                    RetrofitClient.api.obtenerZonas()
                } else {
                    RetrofitClient.api.buscarPorCampo(where)
                }
                _zonas.postValue(data)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.postValue(false)
            }
        }
    }
}
