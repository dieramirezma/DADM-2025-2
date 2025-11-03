package com.example.myapplication

import retrofit2.http.GET
import retrofit2.http.Query

interface ZonaWifiApi {

    // Obtener todas las zonas wifi (sin paginación)
    @GET("9n9m-2dqq.json")
    suspend fun obtenerZonas(): List<ZonaWifi>

    // 📄 Obtener zonas con paginación ($limit y $offset)
    @GET("9n9m-2dqq.json")
    suspend fun obtenerZonasPaginadas(
        @Query("\$limit") limit: Int,
        @Query("\$offset") offset: Int
    ): List<ZonaWifi>

    // 🔍 Búsqueda general con SoQL ($where)
    @GET("9n9m-2dqq.json")
    suspend fun buscarPorCampo(
        @Query("\$where") where: String
    ): List<ZonaWifi>

    // 🔹 Filtro por barrio (búsqueda parcial con SoQL)
    @GET("9n9m-2dqq.json")
    suspend fun buscarPorBarrio(
        @Query("\$where") where: String
    ): List<ZonaWifi>

    // 🔹 Filtros directos (exactos)
    @GET("9n9m-2dqq.json")
    suspend fun getPorBarrio(
        @Query("barrio") barrio: String
    ): List<ZonaWifi>

    @GET("9n9m-2dqq.json")
    suspend fun getPorComuna(
        @Query("comuna") comuna: String
    ): List<ZonaWifi>

    @GET("9n9m-2dqq.json")
    suspend fun getPorDireccion(
        @Query("direccion") direccion: String
    ): List<ZonaWifi>

    @GET("9n9m-2dqq.json")
    suspend fun getPorTipoZona(
        @Query("zona_urbana_rural") tipo: String
    ): List<ZonaWifi>
}
