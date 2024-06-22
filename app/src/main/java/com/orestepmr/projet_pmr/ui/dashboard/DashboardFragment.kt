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

    //private var games : List<Game> = List

    private lateinit var gamesAdapter: GamesAdapter

    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        gamesAdapter = GamesAdapter(requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = gamesAdapter

        observeViewModel()

        return root
    }

    private fun setView(games: List<Game>) {
        gamesAdapter.replaceData(games)
    }

    private fun observeViewModel() {
        dashboardViewModel.games.observe(viewLifecycleOwner) {
            setView(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.getDataGames(requireContext())
    }
}