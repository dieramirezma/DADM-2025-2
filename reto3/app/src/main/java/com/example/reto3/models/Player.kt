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
