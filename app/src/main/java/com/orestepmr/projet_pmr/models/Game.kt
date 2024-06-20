package com.orestepmr.projet_pmr.models

data class Game(
    val id: Int,
    val playerId: Int,
    val playerName: String,
    val gameMasterId: Int,
    val gameMasterName: String,
    val name: String,
    val date: String,
    val time: Int
)
