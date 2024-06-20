package com.orestepmr.projet_pmr.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "player_id") val playerId: Int,
    @ColumnInfo(name = "player_name") val playerName: String,
    @ColumnInfo(name = "game_master_id") val gameMasterId: Int,
    @ColumnInfo(name = "game_master_name") val gameMasterName: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "time") val time: Int
)