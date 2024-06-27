package com.orestepmr.projet_pmr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.orestepmr.projet_pmr.databinding.FragmentDashboardBinding
import com.orestepmr.projet_pmr.models.Game
import com.orestepmr.projet_pmr.ui.adapters.GamesAdapter
import com.orestepmr.projet_pmr.ui.viewmodels.GamesViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //private var games : List<Game> = List

    private lateinit var gamesAdapter: GamesAdapter

    private val dashboardViewModel: GamesViewModel by viewModels()

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