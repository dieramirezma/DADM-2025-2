package com.example.reto3.utils

/**
 * Constantes globales de la aplicación
 */
object Constants {

    // Configuración de juego
    const val BOARD_SIZE = 9
    const val PLAYERS_PER_GAME = 2

    // Símbolos de jugadores
    const val PLAYER_X_SYMBOL = "X"
    const val PLAYER_O_SYMBOL = "O"
    const val EMPTY_CELL = " "

    // Estados de juego
    const val GAME_STATUS_WAITING = "WAITING"
    const val GAME_STATUS_PLAYING = "PLAYING"
    const val GAME_STATUS_FINISHED = "FINISHED"

    // Resultados de juego
    const val RESULT_PLAYER_X = "X"
    const val RESULT_PLAYER_O = "O"
    const val RESULT_TIE = "TIE"

    // Extras de Intent
    const val EXTRA_GAME_ID = "GAME_ID"
    const val EXTRA_PLAYER_SYMBOL = "PLAYER_SYMBOL"
    const val EXTRA_PLAYER_NAME = "PLAYER_NAME"

    // SharedPreferences
    const val PREFS_NAME = "TicTacToeOnlinePrefs"
    const val PREF_PLAYER_ID = "player_id"
    const val PREF_PLAYER_NAME = "player_name"
    const val PREF_TOTAL_GAMES = "total_games"
    const val PREF_TOTAL_WINS = "total_wins"
    const val PREF_TOTAL_LOSSES = "total_losses"
    const val PREF_TOTAL_TIES = "total_ties"

    // Timeouts y delays
    const val NETWORK_TIMEOUT = 10_000L // 10 segundos
    const val GAME_CLEANUP_DAYS = 1 // 1 día
    const val RECONNECT_DELAY = 3_000L // 3 segundos

    // Límites
    const val MAX_PLAYER_NAME_LENGTH = 20
    const val MIN_PLAYER_NAME_LENGTH = 2
    const val MAX_ACTIVE_GAMES = 100

    // Firebase paths
    const val FB_GAMES = "games"
    const val FB_PLAYERS = "players"
    const val FB_PRESENCE = "presence"

    // Animaciones
    const val ANIMATION_DURATION = 300L
    const val FADE_DURATION = 200L
}
