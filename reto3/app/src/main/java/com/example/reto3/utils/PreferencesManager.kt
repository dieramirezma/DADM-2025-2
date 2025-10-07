package com.example.reto3.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de preferencias compartidas
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    /**
     * Guardar ID del jugador
     */
    fun savePlayerId(playerId: String) {
        prefs.edit().putString(Constants.PREF_PLAYER_ID, playerId).apply()
    }

    /**
     * Obtener ID del jugador
     */
    fun getPlayerId(): String? {
        return prefs.getString(Constants.PREF_PLAYER_ID, null)
    }

    /**
     * Guardar nombre del jugador
     */
    fun savePlayerName(name: String) {
        prefs.edit().putString(Constants.PREF_PLAYER_NAME, name).apply()
    }

    /**
     * Obtener nombre del jugador
     */
    fun getPlayerName(): String? {
        return prefs.getString(Constants.PREF_PLAYER_NAME, null)
    }

    /**
     * Guardar estadísticas
     */
    fun saveTotalGames(count: Int) {
        prefs.edit().putInt(Constants.PREF_TOTAL_GAMES, count).apply()
    }

    fun getTotalGames(): Int {
        return prefs.getInt(Constants.PREF_TOTAL_GAMES, 0)
    }

    fun saveTotalWins(count: Int) {
        prefs.edit().putInt(Constants.PREF_TOTAL_WINS, count).apply()
    }

    fun getTotalWins(): Int {
        return prefs.getInt(Constants.PREF_TOTAL_WINS, 0)
    }

    fun saveTotalLosses(count: Int) {
        prefs.edit().putInt(Constants.PREF_TOTAL_LOSSES, count).apply()
    }

    fun getTotalLosses(): Int {
        return prefs.getInt(Constants.PREF_TOTAL_LOSSES, 0)
    }

    fun saveTotalTies(count: Int) {
        prefs.edit().putInt(Constants.PREF_TOTAL_TIES, count).apply()
    }

    fun getTotalTies(): Int {
        return prefs.getInt(Constants.PREF_TOTAL_TIES, 0)
    }

    /**
     * Incrementar estadísticas
     */
    fun incrementGames() {
        saveTotalGames(getTotalGames() + 1)
    }

    fun incrementWins() {
        saveTotalWins(getTotalWins() + 1)
    }

    fun incrementLosses() {
        saveTotalLosses(getTotalLosses() + 1)
    }

    fun incrementTies() {
        saveTotalTies(getTotalTies() + 1)
    }

    /**
     * Limpiar todas las preferencias
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    /**
     * Verificar si es la primera vez que se ejecuta la app
     */
    fun isFirstRun(): Boolean {
        val firstRun = prefs.getBoolean("first_run", true)
        if (firstRun) {
            prefs.edit().putBoolean("first_run", false).apply()
        }
        return firstRun
    }
}
