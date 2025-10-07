# 🎮 Guía de Implementación - Reto 7: Jugando Triqui Online

## 📋 Tabla de Contenidos
1. [Visión General](#visión-general)
2. [Arquitectura Propuesta](#arquitectura-propuesta)
3. [Configuración de Firebase](#configuración-de-firebase)
4. [Modelos de Datos](#modelos-de-datos)
5. [Cambios en el Código Existente](#cambios-en-el-código-existente)
6. [Nuevos Componentes](#nuevos-componentes)
7. [Flujo de Implementación](#flujo-de-implementación)
8. [Pruebas](#pruebas)
9. [Checklist de Implementación](#checklist-de-implementación)

---

## 🎯 Visión General

### Objetivo
Convertir el juego de Triqui de un jugador local vs IA a un juego multijugador en tiempo real donde dos jugadores pueden jugar desde dispositivos diferentes.

### Cambios Principales
- ✅ **Eliminar IA**: El juego ahora es entre dos jugadores humanos
- ✅ **Lista de Juegos**: Pantalla inicial mostrando juegos disponibles
- ✅ **Crear/Unirse a Juegos**: Jugadores pueden crear o unirse a partidas
- ✅ **Sincronización en Tiempo Real**: Usar Firebase Realtime Database
- ✅ **Sistema de Turnos**: Control de turnos entre jugadores remotos
- ✅ **Gestión de Estados**: Manejo de conexión/desconexión de jugadores

---

## 🏗️ Arquitectura Propuesta

```
┌─────────────────────────────────────────────────────────────┐
│                    Firebase Realtime Database                │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐            │
│  │   games/   │  │  players/  │  │  presence/ │            │
│  └────────────┘  └────────────┘  └────────────┘            │
└─────────────────────────────────────────────────────────────┘
           ▲                           ▲
           │                           │
           │    Sincronización en      │
           │      Tiempo Real          │
           │                           │
    ┌──────┴──────┐           ┌───────┴────────┐
    │ Dispositivo 1│           │ Dispositivo 2  │
    │  (Jugador X) │           │  (Jugador O)   │
    └──────────────┘           └────────────────┘
```

### Estructura de Datos en Firebase

```
firebase-root/
├── games/
│   ├── {gameId}/
│   │   ├── gameId: String
│   │   ├── createdBy: String (playerId)
│   │   ├── createdAt: Long (timestamp)
│   │   ├── status: String (waiting/playing/finished)
│   │   ├── playerX: String (playerId)
│   │   ├── playerO: String? (playerId o null)
│   │   ├── currentTurn: String (X/O)
│   │   ├── board: Array<String> (9 posiciones)
│   │   ├── winner: String? (X/O/TIE/null)
│   │   └── lastMove: Long (timestamp)
│   
├── players/
│   ├── {playerId}/
│   │   ├── playerId: String
│   │   ├── displayName: String
│   │   ├── currentGameId: String?
│   │   └── online: Boolean
│
└── presence/
    └── {playerId}/
        ├── online: Boolean
        └── lastSeen: Long
```

---

## 🔥 Configuración de Firebase

### Paso 1: Crear Proyecto en Firebase

1. **Ir a [Firebase Console](https://console.firebase.google.com/)**
2. **Crear nuevo proyecto**:
   - Nombre: `TicTacToeOnline` (o el que prefieras)
   - Habilitar Google Analytics (opcional)
3. **Agregar aplicación Android**:
   - Package name: `com.example.reto3`
   - Descargar `google-services.json`

### Paso 2: Configurar Firebase en el Proyecto

#### 2.1 Colocar google-services.json
```bash
# Ubicación del archivo
app/google-services.json
```

#### 2.2 Modificar build.gradle.kts (Project level)

**Archivo**: `build.gradle.kts` (raíz del proyecto)

```kotlin
// AGREGAR al inicio del archivo
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // AGREGAR:
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

#### 2.3 Modificar app/build.gradle.kts

**Archivo**: `app/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // AGREGAR:
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.reto3"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.reto3"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    
    // AGREGAR para ViewBinding (opcional pero recomendado):
    buildFeatures {
        viewBinding = true
    }
    
    lint {
        abortOnError = false
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // AGREGAR Firebase Dependencies:
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    
    // AGREGAR RecyclerView para lista de juegos:
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

### Paso 3: Configurar Reglas de Seguridad en Firebase

En Firebase Console > Realtime Database > Rules:

```json
{
  "rules": {
    "games": {
      ".read": true,
      ".write": true,
      "$gameId": {
        ".indexOn": ["status", "createdAt"]
      }
    },
    "players": {
      ".read": true,
      ".write": true
    },
    "presence": {
      ".read": true,
      "$playerId": {
        ".write": "$playerId === auth.uid || auth != null"
      }
    }
  }
}
```

**Nota**: Estas son reglas permisivas para desarrollo. En producción deberían ser más restrictivas.

### Paso 4: Habilitar Autenticación Anónima

En Firebase Console:
1. Authentication > Sign-in method
2. Habilitar "Anonymous"

---

## 📦 Modelos de Datos

### 1. Crear Carpeta de Modelos

Crear: `app/src/main/java/com/example/reto3/models/`

### 2. Modelo de Juego (Game.kt)

**Archivo**: `app/src/main/java/com/example/reto3/models/Game.kt`

```kotlin
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
```

### 3. Modelo de Jugador (Player.kt)

**Archivo**: `app/src/main/java/com/example/reto3/models/Player.kt`

```kotlin
package com.example.reto3.models

data class Player(
    val playerId: String = "",
    val displayName: String = "",
    var currentGameId: String? = null,
    var online: Boolean = true
) {
    constructor() : this("", "", null, true)

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "playerId" to playerId,
            "displayName" to displayName,
            "currentGameId" to currentGameId,
            "online" to online
        )
    }
}
```

---

## 🔄 Cambios en el Código Existente

### 1. Modificar TicTacToeGame.kt

**Archivo**: `app/src/main/java/com/example/reto3/TicTacToeGame.kt`

```kotlin
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

    fun getBoardState(): CharArray {
        return board.copyOf()
    }

    fun setBoardState(boardState: CharArray) {
        if (boardState.size == BOARD_SIZE) {
            for (i in board.indices) {
                board[i] = boardState[i]
            }
        }
    }

    // MODIFICAR: Cambiar de List<String> a CharArray
    fun setBoardStateFromList(boardList: List<String>) {
        if (boardList.size == BOARD_SIZE) {
            for (i in board.indices) {
                board[i] = if (boardList[i].isNotEmpty()) boardList[i][0] else OPEN_SPOT
            }
        }
    }

    fun getBoardStateAsList(): List<String> {
        return board.map { it.toString() }
    }

    /**
     * Verifica si hay un ganador
     * Devuelve: X, O, TIE o null (juego continúa)
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

    /**
     * ELIMINAR TODOS LOS MÉTODOS RELACIONADOS CON IA:
     * - getDifficultyLevel()
     * - setDifficultyLevel()
     * - getComputerMove()
     * - getWinningMove()
     * - getBlockingMove()
     * - getRandomMove()
     */
}
```

### 2. Simplificar BoardView.kt

**Archivo**: `app/src/main/java/com/example/reto3/BoardView.kt`

```kotlin
package com.example.reto3

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val GRID_WIDTH = 6f
    }

    private lateinit var paint: Paint
    private var boardColor = Color.GRAY
    private var xImage: Bitmap? = null
    private var oImage: Bitmap? = null
    private var game: TicTacToeGame? = null
    private var cellWidth = 0f
    private var cellHeight = 0f

    init {
        initialize()
    }

    private fun initialize() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = boardColor
            strokeWidth = GRID_WIDTH
            style = Paint.Style.STROKE
        }

        try {
            val xDrawable = context.getDrawable(R.drawable.x_piece)
            val oDrawable = context.getDrawable(R.drawable.o_piece)
            xImage = drawableToBitmap(xDrawable)
            oImage = drawableToBitmap(oDrawable)
        } catch (e: Exception) {
            xImage = createColoredBitmap(100, 100, Color.GREEN)
            oImage = createColoredBitmap(100, 100, Color.RED)
        }
    }

    private fun createColoredBitmap(width: Int, height: Int, color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            this.color = color
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height / 2f, width / 3f, paint)
        return bitmap
    }

    private fun drawableToBitmap(drawable: android.graphics.drawable.Drawable?): Bitmap? {
        drawable ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun setGame(game: TicTacToeGame) {
        this.game = game
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val boardWidth = (w - paddingLeft - paddingRight).toFloat()
        val boardHeight = (h - paddingTop - paddingBottom).toFloat()
        cellWidth = boardWidth / 3f
        cellHeight = boardHeight / 3f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width - paddingLeft - paddingRight
        val height = height - paddingTop - paddingBottom

        // Dibujar líneas del tablero
        for (i in 1..2) {
            val x = paddingLeft + (width * i / 3f)
            canvas.drawLine(x, paddingTop.toFloat(), x, (paddingTop + height).toFloat(), paint)
        }
        for (i in 1..2) {
            val y = paddingTop + (height * i / 3f)
            canvas.drawLine(paddingLeft.toFloat(), y, (paddingLeft + width).toFloat(), y, paint)
        }

        // Dibujar piezas
        game?.let {
            for (i in 0 until TicTacToeGame.BOARD_SIZE) {
                val col = i % 3
                val row = i / 3

                val left = paddingLeft + col * cellWidth
                val top = paddingTop + row * cellHeight

                when (it.getBoardOccupant(i)) {
                    TicTacToeGame.PLAYER_X -> drawPiece(canvas, xImage, left, top)
                    TicTacToeGame.PLAYER_O -> drawPiece(canvas, oImage, left, top)
                }
            }
        }
    }

    private fun drawPiece(canvas: Canvas, image: Bitmap?, left: Float, top: Float) {
        image?.let {
            val scaledImage = Bitmap.createScaledBitmap(
                it,
                (cellWidth * 0.8f).toInt(),
                (cellHeight * 0.8f).toInt(),
                true
            )
            canvas.drawBitmap(
                scaledImage,
                left + cellWidth * 0.1f,
                top + cellHeight * 0.1f,
                null
            )
        }
    }

    fun getBoardCellFromCoordinates(x: Float, y: Float): Int {
        val col = ((x - paddingLeft) / cellWidth).toInt()
        val row = ((y - paddingTop) / cellHeight).toInt()

        if (row in 0..2 && col in 0..2) {
            return row * 3 + col
        }
        return -1
    }
}
```

---

## 🆕 Nuevos Componentes

### 1. GameRepository - Gestor de Firebase

**Archivo**: `app/src/main/java/com/example/reto3/repository/GameRepository.kt`

Crear carpeta: `app/src/main/java/com/example/reto3/repository/`

```kotlin
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
```

### 2. GameListActivity - Lista de Juegos

**Archivo**: `app/src/main/java/com/example/reto3/GameListActivity.kt`

```kotlin
package com.example.reto3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reto3.adapters.GameListAdapter
import com.example.reto3.models.Game
import com.example.reto3.repository.GameRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabCreateGame: FloatingActionButton
    private lateinit var adapter: GameListAdapter
    private lateinit var repository: GameRepository
    private lateinit var auth: FirebaseAuth
    
    private var playerName: String = "Player"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)

        auth = FirebaseAuth.getInstance()
        repository = GameRepository()

        // Autenticación anónima
        authenticateUser()

        initializeViews()
        setupRecyclerView()
        observeGames()
    }

    private fun authenticateUser() {
        auth.currentUser ?: run {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    askForPlayerName()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } ?: askForPlayerName()
    }

    private fun askForPlayerName() {
        val input = EditText(this)
        input.hint = "Enter your name"

        AlertDialog.Builder(this)
            .setTitle("Welcome!")
            .setMessage("Enter your player name")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                playerName = input.text.toString().ifEmpty { "Player${(1000..9999).random()}" }
                createOrUpdatePlayer()
            }
            .show()
    }

    private fun createOrUpdatePlayer() {
        lifecycleScope.launch {
            auth.currentUser?.uid?.let { uid ->
                repository.createOrUpdatePlayer(uid, playerName)
            }
        }
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewGames)
        fabCreateGame = findViewById(R.id.fabCreateGame)

        fabCreateGame.setOnClickListener {
            createNewGame()
        }
    }

    private fun setupRecyclerView() {
        adapter = GameListAdapter { game ->
            joinGame(game)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun observeGames() {
        lifecycleScope.launch {
            repository.getAvailableGames().collectLatest { games ->
                adapter.submitList(games)
            }
        }
    }

    private fun createNewGame() {
        lifecycleScope.launch {
            auth.currentUser?.uid?.let { uid ->
                val result = repository.createGame(uid, playerName)
                result.onSuccess { game ->
                    openGameActivity(game.gameId, "X")
                }.onFailure { error ->
                    Toast.makeText(
                        this@GameListActivity,
                        "Failed to create game: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun joinGame(game: Game) {
        lifecycleScope.launch {
            auth.currentUser?.uid?.let { uid ->
                val result = repository.joinGame(game.gameId, uid)
                result.onSuccess {
                    openGameActivity(game.gameId, "O")
                }.onFailure { error ->
                    Toast.makeText(
                        this@GameListActivity,
                        "Failed to join game: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openGameActivity(gameId: String, playerSymbol: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("GAME_ID", gameId)
            putExtra("PLAYER_SYMBOL", playerSymbol)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Limpiar juegos antiguos
        lifecycleScope.launch {
            repository.cleanupOldGames()
        }
    }
}
```

### 3. GameListAdapter - Adaptador RecyclerView

**Archivo**: `app/src/main/java/com/example/reto3/adapters/GameListAdapter.kt`

Crear carpeta: `app/src/main/java/com/example/reto3/adapters/`

```kotlin
package com.example.reto3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto3.R
import com.example.reto3.models.Game
import java.text.SimpleDateFormat
import java.util.*

class GameListAdapter(
    private val onGameClick: (Game) -> Unit
) : ListAdapter<Game, GameListAdapter.GameViewHolder>(GameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(getItem(position), onGameClick)
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gameIdText: TextView = itemView.findViewById(R.id.textGameId)
        private val gameInfoText: TextView = itemView.findViewById(R.id.textGameInfo)
        private val gameTimeText: TextView = itemView.findViewById(R.id.textGameTime)

        fun bind(game: Game, onClick: (Game) -> Unit) {
            gameIdText.text = "Game #${game.gameId.takeLast(6)}"
            gameInfoText.text = "Waiting for opponent..."
            
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            gameTimeText.text = "Created: ${dateFormat.format(Date(game.createdAt))}"

            itemView.setOnClickListener {
                onClick(game)
            }
        }
    }

    class GameDiffCallback : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.gameId == newItem.gameId
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem == newItem
        }
    }
}
```

### 4. MainActivity Modificado - Juego Online

**Archivo**: `app/src/main/java/com/example/reto3/MainActivity.kt` (REEMPLAZAR COMPLETAMENTE)

```kotlin
package com.example.reto3

import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.reto3.models.GameStatus
import com.example.reto3.repository.GameRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var game: TicTacToeGame
    private lateinit var boardView: BoardView
    private lateinit var statusText: TextView
    private lateinit var repository: GameRepository
    private lateinit var auth: FirebaseAuth

    private var gameId: String? = null
    private var mySymbol: String = "X" // X o O
    private var gameOver = false

    private var moveSoundPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        repository = GameRepository()

        // Obtener datos del intent
        gameId = intent.getStringExtra("GAME_ID")
        mySymbol = intent.getStringExtra("PLAYER_SYMBOL") ?: "X"

        if (gameId == null) {
            Toast.makeText(this, "Invalid game", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        game = TicTacToeGame()
        initializeViews()
        loadSound()
        observeGameChanges()
    }

    private fun initializeViews() {
        boardView = findViewById(R.id.board_view)
        statusText = findViewById(R.id.status_text)

        boardView.setGame(game)

        // Configurar listener para toques en el tablero
        boardView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && !gameOver) {
                onBoardTouch(event.x, event.y)
            }
            true
        }

        updateStatus("Waiting for game to start...")
    }

    private fun loadSound() {
        try {
            moveSoundPlayer = MediaPlayer.create(this, R.raw.human_sound)
        } catch (e: Exception) {
            // Sonido no disponible
        }
    }

    private fun observeGameChanges() {
        lifecycleScope.launch {
            gameId?.let { id ->
                repository.observeGame(id).collectLatest { gameData ->
                    gameData?.let { updateGameState(it) }
                }
            }
        }
    }

    private fun updateGameState(gameData: com.example.reto3.models.Game) {
        // Actualizar tablero local
        game.setBoardStateFromList(gameData.board)
        boardView.invalidate()

        // Actualizar estado del juego
        when (gameData.status) {
            GameStatus.WAITING -> {
                updateStatus("Waiting for opponent...")
                gameOver = true
            }
            GameStatus.PLAYING -> {
                gameOver = false
                if (gameData.currentTurn == mySymbol) {
                    updateStatus("Your turn ($mySymbol)")
                } else {
                    updateStatus("Opponent's turn")
                }
            }
            GameStatus.FINISHED -> {
                gameOver = true
                handleGameEnd(gameData.winner)
            }
        }

        // Verificar ganador localmente también
        checkWinner()
    }

    private fun onBoardTouch(x: Float, y: Float) {
        if (gameOver) {
            Toast.makeText(this, "Game is over", Toast.LENGTH_SHORT).show()
            return
        }

        val cell = boardView.getBoardCellFromCoordinates(x, y)
        if (cell == -1) return

        // Verificar si es el turno del jugador
        lifecycleScope.launch {
            gameId?.let { id ->
                val result = repository.makeMove(id, cell, mySymbol)
                result.onSuccess {
                    playSound()
                }.onFailure { error ->
                    Toast.makeText(
                        this@MainActivity,
                        error.message ?: "Invalid move",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkWinner() {
        val winner = game.checkForWinner()
        winner?.let {
            gameOver = true
            
            lifecycleScope.launch {
                gameId?.let { id ->
                    val winnerStr = when (winner) {
                        'T' -> "TIE"
                        else -> winner.toString()
                    }
                    repository.updateGameWinner(id, winnerStr)
                }
            }
        }
    }

    private fun handleGameEnd(winner: String?) {
        val message = when (winner) {
            mySymbol -> "You win! 🎉"
            "TIE" -> "It's a tie! 🤝"
            null -> "Game ended"
            else -> "You lose 😢"
        }

        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage(message)
            .setPositiveButton("New Game") { _, _ ->
                finish()
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun updateStatus(message: String) {
        statusText.text = message
    }

    private fun playSound() {
        try {
            moveSoundPlayer?.start()
        } catch (e: Exception) {
            // Sonido no disponible
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_leave -> {
                leaveGame()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun leaveGame() {
        AlertDialog.Builder(this)
            .setTitle("Leave Game")
            .setMessage("Are you sure you want to leave?")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    gameId?.let { id ->
                        auth.currentUser?.uid?.let { uid ->
                            repository.leaveGame(id, uid)
                        }
                    }
                    finish()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        moveSoundPlayer?.release()
        moveSoundPlayer = null
    }

    override fun onBackPressed() {
        leaveGame()
    }
}
```

---

## 🎨 Layouts XML

### 1. activity_game_list.xml

**Archivo**: `app/src/main/res/layout/activity_game_list.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Available Games"
            android:textSize="24sp"
            android:textStyle="bold"
            android:padding="16dp"
            android:gravity="center"
            android:background="@color/purple_500"
            android:textColor="@android:color/white"/>

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recyclerViewGames"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:padding="8dp"
            android:clipToPadding="false"/>

    </LinearLayout>

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabCreateGame"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp"
        android:src="@android:drawable/ic_input_add"
        android:contentDescription="Create new game"
        app:tint="@android:color/white"/>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### 2. item_game.xml

**Archivo**: `app/src/main/res/layout/item_game.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:id="@+id/textGameId"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Game #123456"
            android:textSize="18sp"
            android:textStyle="bold"
            android:textColor="@color/purple_700"/>

        <TextView
            android:id="@+id/textGameInfo"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Waiting for opponent..."
            android:textSize="14sp"
            android:layout_marginTop="4dp"/>

        <TextView
            android:id="@+id/textGameTime"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Created: 12:30"
            android:textSize="12sp"
            android:textColor="@android:color/darker_gray"
            android:layout_marginTop="4dp"/>

    </LinearLayout>

</androidx.cardview.widget.CardView>
```

### 3. Modificar activity_main.xml

**Archivo**: `app/src/main/res/layout/activity_main.xml` (SIMPLIFICAR)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    android:gravity="center">

    <TextView
        android:id="@+id/status_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Game Status"
        android:textSize="20sp"
        android:textStyle="bold"
        android:layout_marginBottom="24dp"/>

    <com.example.reto3.BoardView
        android:id="@+id/board_view"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:padding="8dp"/>

</LinearLayout>
```

### 4. menu/game_menu.xml

**Archivo**: `app/src/main/res/menu/game_menu.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    
    <item
        android:id="@+id/menu_leave"
        android:title="Leave Game"
        app:showAsAction="never"/>
    
</menu>
```

---

## 📝 AndroidManifest.xml

**Archivo**: `app/src/main/AndroidManifest.xml` (MODIFICAR)

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- AGREGAR permisos de Internet -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Reto3"
        tools:targetApi="31">

        <!-- CAMBIAR: GameListActivity es la actividad de inicio -->
        <activity
            android:name=".GameListActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- MainActivity ahora es la pantalla de juego -->
        <activity
            android:name=".MainActivity"
            android:exported="false"
            android:screenOrientation="portrait"/>

    </application>

</manifest>
```

---

## 🔄 Flujo de Implementación

### Fase 1: Configuración (Día 1)
1. ✅ Crear proyecto Firebase
2. ✅ Descargar y agregar `google-services.json`
3. ✅ Modificar archivos `build.gradle.kts`
4. ✅ Sincronizar Gradle
5. ✅ Configurar reglas de seguridad en Firebase
6. ✅ Habilitar autenticación anónima

### Fase 2: Modelos y Repository (Día 2)
1. ✅ Crear carpeta `models/`
2. ✅ Implementar `Game.kt`
3. ✅ Implementar `Player.kt`
4. ✅ Crear carpeta `repository/`
5. ✅ Implementar `GameRepository.kt`
6. ✅ Probar conexión con Firebase

### Fase 3: Modificar Código Existente (Día 3)
1. ✅ Modificar `TicTacToeGame.kt` (eliminar IA)
2. ✅ Simplificar `BoardView.kt`
3. ✅ Crear layouts XML nuevos
4. ✅ Modificar `AndroidManifest.xml`

### Fase 4: Lista de Juegos (Día 4)
1. ✅ Crear `GameListActivity.kt`
2. ✅ Crear carpeta `adapters/`
3. ✅ Implementar `GameListAdapter.kt`
4. ✅ Crear `activity_game_list.xml`
5. ✅ Crear `item_game.xml`
6. ✅ Probar creación y visualización de juegos

### Fase 5: Juego Online (Día 5)
1. ✅ Reescribir `MainActivity.kt` completamente
2. ✅ Implementar sincronización en tiempo real
3. ✅ Implementar sistema de turnos
4. ✅ Probar con dos dispositivos/emuladores

### Fase 6: Pruebas y Refinamiento (Día 6-7)
1. ✅ Manejar desconexiones
2. ✅ Mejorar UX/UI
3. ✅ Agregar animaciones
4. ✅ Probar casos extremos
5. ✅ Optimizar rendimiento

---

## 🧪 Pruebas

### Pruebas Esenciales

#### 1. Crear Juego
- [ ] Un jugador puede crear un juego
- [ ] El juego aparece en la lista de juegos disponibles
- [ ] El creador ve "Esperando oponente"

#### 2. Unirse a Juego
- [ ] Otro jugador puede ver el juego en la lista
- [ ] Puede unirse al juego
- [ ] Ambos jugadores ven el tablero actualizado

#### 3. Hacer Movimientos
- [ ] Solo el jugador del turno actual puede mover
- [ ] Los movimientos se sincronizan en tiempo real
- [ ] El turno cambia correctamente

#### 4. Detectar Ganador
- [ ] Se detecta correctamente cuando X gana
- [ ] Se detecta correctamente cuando O gana
- [ ] Se detecta correctamente un empate
- [ ] Ambos jugadores ven el resultado

#### 5. Salir del Juego
- [ ] Un jugador puede salir durante la espera
- [ ] El juego se elimina si está en espera
- [ ] El juego se marca como finalizado si está en progreso

#### 6. Desconexión
- [ ] Si un jugador se desconecta, el otro lo detecta
- [ ] El sistema maneja reconexiones

### Comandos de Prueba

```bash
# Ejecutar en dos emuladores diferentes
# Emulador 1:
adb -s emulator-5554 install app-debug.apk

# Emulador 2:
adb -s emulator-5556 install app-debug.apk
```

---

## ✅ Checklist de Implementación

### Configuración Firebase
- [ ] Proyecto Firebase creado
- [ ] `google-services.json` descargado y colocado
- [ ] `build.gradle.kts` (project) modificado
- [ ] `app/build.gradle.kts` modificado
- [ ] Dependencias sincronizadas
- [ ] Autenticación anónima habilitada
- [ ] Reglas de seguridad configuradas

### Modelos de Datos
- [ ] Carpeta `models/` creada
- [ ] `Game.kt` implementado
- [ ] `Player.kt` implementado
- [ ] Enums `GameStatus` definido

### Repository
- [ ] Carpeta `repository/` creada
- [ ] `GameRepository.kt` implementado
- [ ] Métodos de creación de juego
- [ ] Métodos de unirse a juego
- [ ] Observadores en tiempo real
- [ ] Gestión de movimientos
- [ ] Gestión de presencia

### Código Existente Modificado
- [ ] `TicTacToeGame.kt` - IA eliminada
- [ ] `TicTacToeGame.kt` - Métodos de conversión agregados
- [ ] `BoardView.kt` - Simplificado
- [ ] Métodos de IA eliminados

### Nuevos Componentes
- [ ] `GameListActivity.kt` creado
- [ ] Carpeta `adapters/` creada
- [ ] `GameListAdapter.kt` implementado
- [ ] `MainActivity.kt` reescrito completamente

### Layouts
- [ ] `activity_game_list.xml` creado
- [ ] `item_game.xml` creado
- [ ] `activity_main.xml` simplificado
- [ ] `menu/game_menu.xml` creado

### Configuración
- [ ] `AndroidManifest.xml` - Permisos agregados
- [ ] `AndroidManifest.xml` - Actividad de inicio cambiada
- [ ] Orientación de pantalla configurada

### Pruebas
- [ ] Crear juego funciona
- [ ] Unirse a juego funciona
- [ ] Sincronización en tiempo real funciona
- [ ] Sistema de turnos funciona
- [ ] Detección de ganador funciona
- [ ] Salir del juego funciona
- [ ] Probado con 2 dispositivos

---

## 📚 Recursos Adicionales

### Documentación
- [Firebase Realtime Database - Android](https://firebase.google.com/docs/database/android/start)
- [Firebase Authentication - Android](https://firebase.google.com/docs/auth/android/anonymous-auth)
- [RecyclerView Guide](https://developer.android.com/guide/topics/ui/layout/recyclerview)
- [Kotlin Coroutines](https://developer.android.com/kotlin/coroutines)

### Solución de Problemas Comunes

#### Error: "google-services.json not found"
**Solución**: Asegúrate de que el archivo esté en `app/google-services.json`

#### Error: "Firebase not initialized"
**Solución**: Verifica que el plugin de Google Services esté aplicado

#### Error: "Permission denied" en Firebase
**Solución**: Revisa las reglas de seguridad en Firebase Console

#### Los juegos no se sincronizan
**Solución**: Verifica la conexión a Internet y las reglas de Firebase

---

## 🎓 Notas para la Demostración con el Profesor

### Preparación
1. Ten dos dispositivos/emuladores listos
2. Asegúrate de tener conexión a Internet estable
3. Ten la consola de Firebase abierta para mostrar datos

### Flujo de Demostración
1. **Dispositivo 1**: Abrir app, ingresar nombre, crear juego
2. **Mostrar**: Lista de juegos actualizada
3. **Dispositivo 2**: Abrir app, ingresar nombre, unirse a juego
4. **Mostrar**: Sincronización en ambos dispositivos
5. **Jugar**: Alternar turnos entre dispositivos
6. **Mostrar**: Detección de ganador en ambos dispositivos

### Puntos Clave a Destacar
- ✅ Sincronización en tiempo real
- ✅ Sistema de turnos funcional
- ✅ Lista de juegos disponibles
- ✅ Creación y unión a juegos
- ✅ Detección de ganador
- ✅ Manejo de estados (esperando/jugando/finalizado)

---

## 🚀 Próximas Mejoras (Opcionales)

### Funcionalidades Adicionales
- [ ] Chat entre jugadores
- [ ] Historial de partidas
- [ ] Sistema de ranking
- [ ] Notificaciones push
- [ ] Modo espectador
- [ ] Invitaciones por enlace
- [ ] Perfiles de usuario con avatar
- [ ] Estadísticas avanzadas
- [ ] Modo torneo
- [ ] Temas personalizables

---

**¡Buena suerte con la implementación! 🎮🔥**
