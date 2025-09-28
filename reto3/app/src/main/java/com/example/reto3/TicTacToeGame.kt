package com.example.reto3

import kotlin.random.Random

class TicTacToeGame {

    companion object {
        const val HUMAN_PLAYER = 'X'
        const val COMPUTER_PLAYER = 'O'
        const val OPEN_SPOT = ' '
        const val BOARD_SIZE = 9
    }

    /**
     * Niveles de dificultad del juego
     * EASY: Solo movimientos aleatorios
     * HARDER: Bloquea al jugador y hace movimientos aleatorios
     * EXPERT: Trata de ganar, bloquea al jugador y hace movimientos aleatorios
     */
    enum class DifficultyLevel {
        EASY, HARDER, EXPERT
    }

    private val board = CharArray(BOARD_SIZE) { OPEN_SPOT }
    private var humanFirst = true
    private var difficultyLevel = DifficultyLevel.EXPERT // Dificultad por defecto

    fun clearBoard() {
        for (i in board.indices) board[i] = OPEN_SPOT
    }

    fun setMove(player: Char, location: Int): Boolean {
        if (location in 0 until BOARD_SIZE && board[location] == OPEN_SPOT) {
            board[location] = player
            return true
        }
        return false
    }

    fun getBoardOccupant(location: Int): Char {
        return if (location in 0 until BOARD_SIZE) board[location] else OPEN_SPOT
    }

    /**
     * Setter para el nivel de dificultad
     */
    fun setDifficultyLevel(level: DifficultyLevel) {
        difficultyLevel = level
    }

    /**
     * Getter para el nivel de dificultad
     */
    fun getDifficultyLevel(): DifficultyLevel {
        return difficultyLevel
    }

    /**
     * Obtiene el mejor movimiento para la computadora basado en la dificultad seleccionada
     * EASY: Solo movimientos aleatorios
     * HARDER: Bloquea al jugador, sino movimiento aleatorio
     * EXPERT: Trata de ganar, bloquea al jugador, sino movimiento aleatorio
     */
    fun getComputerMove(): Int {
        return when (difficultyLevel) {
            DifficultyLevel.EASY -> getRandomMove()
            DifficultyLevel.HARDER -> {
                // Primero trata de bloquear al jugador
                val blockingMove = getBlockingMove()
                if (blockingMove != -1) blockingMove else getRandomMove()
            }
            DifficultyLevel.EXPERT -> {
                // Primero trata de ganar
                val winningMove = getWinningMove()
                if (winningMove != -1) return winningMove

                // Luego trata de bloquear al jugador
                val blockingMove = getBlockingMove()
                if (blockingMove != -1) blockingMove else getRandomMove()
            }
        }
    }

    /**
     * Obtiene un movimiento aleatorio de las posiciones disponibles
     */
    private fun getRandomMove(): Int {
        val availableMoves = board.indices.filter { board[it] == OPEN_SPOT }
        return if (availableMoves.isNotEmpty()) {
            availableMoves.random(Random)
        } else -1
    }

    /**
     * Busca un movimiento que permita a la computadora ganar en este turno
     * @return la posición del movimiento ganador, o -1 si no existe
     */
    private fun getWinningMove(): Int {
        for (i in board.indices) {
            if (board[i] == OPEN_SPOT) {
                // Simula el movimiento de la computadora
                board[i] = COMPUTER_PLAYER
                if (checkForWinner() == 3) { // La computadora gana
                    board[i] = OPEN_SPOT // Restaura el estado
                    return i
                }
                board[i] = OPEN_SPOT // Restaura el estado
            }
        }
        return -1
    }

    /**
     * Busca un movimiento que bloquee al jugador humano de ganar en su próximo turno
     * @return la posición del movimiento de bloqueo, o -1 si no es necesario
     */
    private fun getBlockingMove(): Int {
        for (i in board.indices) {
            if (board[i] == OPEN_SPOT) {
                // Simula el movimiento del jugador humano
                board[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) { // El humano ganaría
                    board[i] = OPEN_SPOT // Restaura el estado
                    return i // Bloquea esta posición
                }
                board[i] = OPEN_SPOT // Restaura el estado
            }
        }
        return -1
    }

    fun checkForWinner(): Int {
        val lines = arrayOf(
            intArrayOf(0,1,2), intArrayOf(3,4,5), intArrayOf(6,7,8), // filas
            intArrayOf(0,3,6), intArrayOf(1,4,7), intArrayOf(2,5,8), // columnas
            intArrayOf(0,4,8), intArrayOf(2,4,6)                      // diagonales
        )

        for (line in lines) {
            if (board[line[0]] != OPEN_SPOT &&
                board[line[0]] == board[line[1]] &&
                board[line[1]] == board[line[2]]) {
                return if (board[line[0]] == HUMAN_PLAYER) 2 else 3
            }
        }
        return if (board.all { it != OPEN_SPOT }) 1 else 0
    }

    fun isHumanFirst(): Boolean = humanFirst

    fun switchFirstPlayer() {
        humanFirst = !humanFirst
    }
}
