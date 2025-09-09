package com.example.reto3

import kotlin.random.Random

class TicTacToeGame {

    companion object {
        const val HUMAN_PLAYER = 'X'
        const val COMPUTER_PLAYER = 'O'
        const val OPEN_SPOT = ' '
        const val BOARD_SIZE = 9
    }

    private val board = CharArray(BOARD_SIZE) { OPEN_SPOT }
    private var humanFirst = true

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

    fun getComputerMove(): Int {
        // Primero trata de ganar
        for (i in board.indices) {
            if (board[i] == OPEN_SPOT) {
                board[i] = COMPUTER_PLAYER
                if (checkForWinner() == 3) {
                    board[i] = OPEN_SPOT
                    return i
                }
                board[i] = OPEN_SPOT
            }
        }

        for (i in board.indices) {
            if (board[i] == OPEN_SPOT) {
                board[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    board[i] = OPEN_SPOT
                    return i
                }
                board[i] = OPEN_SPOT
            }
        }

        val availableMoves = board.indices.filter { board[it] == OPEN_SPOT }
        return if (availableMoves.isNotEmpty()) {
            availableMoves.random(Random)
        } else 0
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
