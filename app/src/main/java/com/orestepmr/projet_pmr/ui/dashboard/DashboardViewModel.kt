package com.orestepmr.projet_pmr.ui.dashboard

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room.databaseBuilder
import com.orestepmr.projet_pmr.data.AppDatabase
import com.orestepmr.projet_pmr.models.Game
import androidx.room.Room
import com.orestepmr.projet_pmr.models.GameEntity
import com.orestepmr.projet_pmr.models.PlayersEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    val currentGame = MutableLiveData<Game>()

    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> get() = _games

    fun getDataGames(context: Context) {

        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "gameDB"
        ).build()

        //Data recovery with coroutines
        CoroutineScope(Dispatchers.Default).launch {
            val gameEntities = db.gameDao().getAllGames()
            // Convert GameEntity list to Game list
            val listOfGames = gameEntities.map { Game(
                id = it.id,
                playerId = it.playerId,
                playerName = it.playerName,
                gameMasterId = it.gameMasterId,
                gameMasterName = it.gameMasterName,
                name = it.name,
                date = it.date,
                time = it.time
            )
            }
            _games.postValue(listOfGames)

            if (listOfGames.isEmpty()) {
                insertSampleData(db, context)
            }
        }
    }

    private fun insertSampleData(db: AppDatabase, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            // Create a list of fake players
            val players = listOf(
                PlayersEntity(0, "John Doe", 25),
                PlayersEntity(0, "Jane Smith", 30),
                PlayersEntity(0, "Alice Johnson", 28)
            )

            // Insert players into database
            players.forEach {
                db.gameDao().createUser(it)
            }

            // Create a list of fake games
            val games = listOf(
                GameEntity(0, 1, "John Doe", 1, "Jane Smith", "Explorers Guild", "2022-07-15", 90),
                GameEntity(0, 2, "Jane Smith", 2, "John Doe", "Dragon Riders", "2022-07-16", 120),
                GameEntity(0, 3, "Alice Johnson", 1, "John Doe", "Mystery Mansion", "2022-07-17", 110)
            )

            // Insert games into database
            games.forEach {
                db.gameDao().insertGame(it)
            }

            // Retrieve and log all games
            val data = db.gameDao().getAllGames()

            //Log.i("room", "All the games: $data")

            getDataGames(context)

        }
    }

    fun createNewGame(context: Context, game: Game) {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "gameDB"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            val gameEntity = GameEntity(
                0,
                game.playerId,
                game.playerName,
                game.gameMasterId,
                game.gameMasterName,
                game.name,
                game.date,
                game.time
            )

            db.gameDao().insertGame(gameEntity)

            getDataGames(context)
        }

        currentGame.postValue(game)
    }

}