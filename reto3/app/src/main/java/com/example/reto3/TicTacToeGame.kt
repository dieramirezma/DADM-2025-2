package com.example.reto3

class TicTacToeGame {

    companion object {
        const val PLAYER_X = 'X'
        const val PLAYER_O = 'O'
        const val OPEN_SPOT = ' '
        const val BOARD_SIZE = 9
    }

    private val board = CharArray(BOARD_SIZE) { OPEN_SPOT }

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
     * Obtiene el estado completo del tablero
     */
    fun getBoardState(): CharArray {
        return board.copyOf()
    }

    /**
     * Establece el estado completo del tablero
     */
    fun setBoardState(boardState: CharArray) {
        if (boardState.size == BOARD_SIZE) {
            for (i in board.indices) {
                board[i] = boardState[i]
            }
        }
    }

    /**
     * Establece el estado del tablero desde una lista de Strings (para Firebase)
     */
    fun setBoardStateFromList(boardList: List<String>) {
        if (boardList.size == BOARD_SIZE) {
            for (i in board.indices) {
                board[i] = if (boardList[i].isNotEmpty()) boardList[i][0] else OPEN_SPOT
            }
        }
    }

    /**
     * Obtiene el estado del tablero como lista de Strings (para Firebase)
     */
    fun getBoardStateAsList(): List<String> {
        return board.map { it.toString() }
    }

    /**
     * Verifica si hay un ganador
     * Devuelve: X, O, T (TIE) o null (juego continúa)
     */
    fun checkForWinner(): Char? {
        // Filas
        for (i in 0..6 step 3) {
            if (board[i] != OPEN_SPOT &&
                board[i] == board[i + 1] &&
                board[i] == board[i + 2]
            ) {
                return board[i]
            }
        }

        // Columnas
        for (i in 0..2) {
            if (board[i] != OPEN_SPOT &&
                board[i] == board[i + 3] &&
                board[i] == board[i + 6]
            ) {
                return board[i]
            }
        }

        // Diagonales
        if (board[0] != OPEN_SPOT &&
            board[0] == board[4] &&
            board[0] == board[8]
        ) {
            return board[0]
        }

        if (board[2] != OPEN_SPOT &&
            board[2] == board[4] &&
            board[2] == board[6]
        ) {
            return board[2]
        }

        // Empate - tablero lleno
        if (board.none { it == OPEN_SPOT }) {
            return 'T' // T para TIE
        }

        return null // Juego continúa
    }
}
