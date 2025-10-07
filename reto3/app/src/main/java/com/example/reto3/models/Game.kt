package com.example.reto3.models

data class Game(
    val gameId: String = "",
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    var status: GameStatus = GameStatus.WAITING,
    val playerX: String = "", // ID del jugador X (creador)
    var playerO: String? = null, // ID del jugador O (quien se une)
    var currentTurn: String = "X", // X o O
    var board: List<String> = List(9) { " " }, // Tablero 3x3
    var winner: String? = null, // X, O, TIE o null
    var lastMove: Long = System.currentTimeMillis()
) {
    // Constructor vacío requerido por Firebase
    constructor() : this(
        gameId = "",
        createdBy = "",
        createdAt = 0L,
        status = GameStatus.WAITING,
        playerX = "",
        playerO = null,
        currentTurn = "X",
        board = List(9) { " " },
        winner = null,
        lastMove = 0L
    )

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "gameId" to gameId,
            "createdBy" to createdBy,
            "createdAt" to createdAt,
            "status" to status.name,
            "playerX" to playerX,
            "playerO" to playerO,
            "currentTurn" to currentTurn,
            "board" to board,
            "winner" to winner,
            "lastMove" to lastMove
        )
    }
}

enum class GameStatus {
    WAITING,    // Esperando segundo jugador
    PLAYING,    // Juego en progreso
    FINISHED    // Juego terminado
}
