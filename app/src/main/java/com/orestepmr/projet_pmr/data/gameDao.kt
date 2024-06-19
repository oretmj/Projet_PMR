package com.orestepmr.projet_pmr.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.orestepmr.projet_pmr.models.GameEntity
import com.orestepmr.projet_pmr.models.PlayersEntity

@Dao
interface gameDao {
    @Query("SELECT * FROM games")
    fun getAllGames():  List<GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUser(game: PlayersEntity)

}