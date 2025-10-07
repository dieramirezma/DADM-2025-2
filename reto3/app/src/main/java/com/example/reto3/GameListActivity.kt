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
