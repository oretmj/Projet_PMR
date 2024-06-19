package com.orestepmr.projet_pmr.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.orestepmr.projet_pmr.data.AppDatabase
import com.orestepmr.projet_pmr.databinding.FragmentDashboardBinding
import com.orestepmr.projet_pmr.models.Game
import com.orestepmr.projet_pmr.models.GameEntity
import com.orestepmr.projet_pmr.models.PlayersEntity
import com.orestepmr.projet_pmr.ui.adapters.GamesAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        val db = Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java, "gameDB"
        ).build()

        //Create fake games and players for tests
        //insertSampleData(db)

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
            //Update UI
            withContext(Dispatchers.Main) {
                setView(listOfGames)
            }

        }


        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*
        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        },
         */
        return root
    }

    private fun setView(game: List<Game>) {
        val adapter: GamesAdapter = GamesAdapter(game)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun insertSampleData(db: AppDatabase) {
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

            Log.i("room", "All the games: $data")

        }
    }
}