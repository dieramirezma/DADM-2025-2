package com.example.reto3.repository

import com.example.reto3.models.Game
import com.example.reto3.models.GameStatus
import com.example.reto3.models.Player
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GameRepository {

    private val database = FirebaseDatabase.getInstance()
    private val gamesRef = database.getReference("games")
    private val playersRef = database.getReference("players")
    private val presenceRef = database.getReference("presence")

    /**
     * Crea un nuevo juego
     */
    suspend fun createGame(playerId: String, playerName: String): Result<Game> {
        return try {
            val gameId = gamesRef.push().key ?: return Result.failure(Exception("Failed to generate game ID"))

            val game = Game(
                gameId = gameId,
                createdBy = playerId,
                playerX = playerId,
                status = GameStatus.WAITING
            )

            gamesRef.child(gameId).setValue(game.toMap()).await()

            // Actualizar jugador
            updatePlayerGame(playerId, gameId)

            Result.success(game)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Unirse a un juego existente
     */
    suspend fun joinGame(gameId: String, playerId: String): Result<Game> {
        return try {
            val snapshot = gamesRef.child(gameId).get().await()
            val game = snapshot.getValue(Game::class.java)
                ?: return Result.failure(Exception("Game not found"))

            // Validar que no sea el mismo jugador
            if (game.playerX == playerId) {
                return Result.failure(Exception("Cannot join your own game"))
            }

            if (game.status != GameStatus.WAITING) {
                return Result.failure(Exception("Game is not available"))
            }

            if (game.playerO != null) {
                return Result.failure(Exception("Game is full"))
            }

            // Actualizar juego
            gamesRef.child(gameId).child("playerO").setValue(playerId).await()
            gamesRef.child(gameId).child("status").setValue(GameStatus.PLAYING.name).await()

            // Actualizar jugador
            updatePlayerGame(playerId, gameId)

            val updatedGame = game.copy(playerO = playerId, status = GameStatus.PLAYING)
            Result.success(updatedGame)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener lista de juegos disponibles
     */
    fun getAvailableGames(): Flow<List<Game>> = callbackFlow {
        val query = gamesRef.orderByChild("status").equalTo(GameStatus.WAITING.name)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val games = mutableListOf<Game>()
                for (gameSnapshot in snapshot.children) {
                    gameSnapshot.getValue(Game::class.java)?.let { game ->
                        games.add(game)
                    }
                }
                trySend(games.sortedByDescending { it.createdAt })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)

        awaitClose { query.removeEventListener(listener) }
    }

    /**
     * Observar cambios en un juego específico
     */
    fun observeGame(gameId: String): Flow<Game?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val game = snapshot.getValue(Game::class.java)
                trySend(game)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        gamesRef.child(gameId).addValueEventListener(listener)

        awaitClose { gamesRef.child(gameId).removeEventListener(listener) }
    }

    /**
     * Hacer un movimiento en el juego
     */
    suspend fun makeMove(gameId: String, position: Int, player: String): Result<Unit> {
        return try {
            val snapshot = gamesRef.child(gameId).get().await()
            val game = snapshot.getValue(Game::class.java)
                ?: return Result.failure(Exception("Game not found"))

            if (game.status != GameStatus.PLAYING) {
                return Result.failure(Exception("Game is not in playing state"))
            }

            if (game.currentTurn != player) {
                return Result.failure(Exception("Not your turn"))
            }

            val updatedBoard = game.board.toMutableList()
            if (updatedBoard[position] != " ") {
                return Result.failure(Exception("Position already occupied"))
            }

            updatedBoard[position] = player

            val updates = hashMapOf<String, Any>(
                "board" to updatedBoard,
                "currentTurn" to if (player == "X") "O" else "X",
                "lastMove" to System.currentTimeMillis()
            )

            gamesRef.child(gameId).updateChildren(updates).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar el ganador del juego
     */
    suspend fun updateGameWinner(gameId: String, winner: String?) {
        try {
            val updates = hashMapOf<String, Any?>(
                "winner" to winner,
                "status" to GameStatus.FINISHED.name
            )
            gamesRef.child(gameId).updateChildren(updates).await()
        } catch (e: Exception) {
            // Log error
        }
    }

    /**
     * Salir de un juego
     */
    suspend fun leaveGame(gameId: String, playerId: String) {
        try {
            val snapshot = gamesRef.child(gameId).get().await()
            val game = snapshot.getValue(Game::class.java) ?: return

            // Si el juego está esperando, eliminarlo
            if (game.status == GameStatus.WAITING) {
                gamesRef.child(gameId).removeValue().await()
            } else {
                // Marcar como finalizado
                updateGameWinner(gameId, null)
            }

            // Limpiar referencia del jugador
            updatePlayerGame(playerId, null)
        } catch (e: Exception) {
            // Log error
        }
    }

    /**
     * Crear o actualizar jugador
     */
    suspend fun createOrUpdatePlayer(playerId: String, displayName: String): Result<Player> {
        return try {
            val player = Player(
                playerId = playerId,
                displayName = displayName,
                online = true
            )

            playersRef.child(playerId).setValue(player.toMap()).await()

            // Configurar presencia
            setupPresence(playerId)

            Result.success(player)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun setupPresence(playerId: String) {
        val presenceData = mapOf(
            "online" to true,
            "lastSeen" to ServerValue.TIMESTAMP
        )

        val disconnectData = mapOf(
            "online" to false,
            "lastSeen" to ServerValue.TIMESTAMP
        )

        presenceRef.child(playerId).setValue(presenceData)
        presenceRef.child(playerId).onDisconnect().setValue(disconnectData)
    }

    private suspend fun updatePlayerGame(playerId: String, gameId: String?) {
        try {
            playersRef.child(playerId).child("currentGameId").setValue(gameId).await()
        } catch (e: Exception) {
            // Log error
        }
    }

    /**
     * Eliminar juegos antiguos (limpieza)
     */
    suspend fun cleanupOldGames() {
        try {
            val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
            val snapshot = gamesRef.orderByChild("createdAt").endAt(oneDayAgo.toDouble()).get().await()

            for (gameSnapshot in snapshot.children) {
                gameSnapshot.ref.removeValue()
            }
        } catch (e: Exception) {
            // Log error
        }
    }
}
