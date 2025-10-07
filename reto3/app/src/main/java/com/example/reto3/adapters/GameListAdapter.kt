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
