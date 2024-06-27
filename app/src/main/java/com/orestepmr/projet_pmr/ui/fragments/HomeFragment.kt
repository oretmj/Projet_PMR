package com.orestepmr.projet_pmr.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.orestepmr.projet_pmr.R
import com.orestepmr.projet_pmr.databinding.FragmentHomeBinding
import com.orestepmr.projet_pmr.ui.activities.PDFActivity
import com.orestepmr.projet_pmr.ui.viewmodels.GamesViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dashboardViewModel: GamesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
         */

        setView()

        return root
    }

    private fun setView() {
        binding.btnNewGame.setOnClickListener {
            // Create new game and navigate to game
            /*
            val name = binding.etGameName.text.toString()
            val playerName = binding.etPlayerName.text.toString()
            val gameMasterName = binding.etGameMasterName.text.toString()
            val difficulty = binding.spDifficulty.selectedItem.toString()

            var newGame = Game(0, 0, playerName, 0, gameMasterName, name, LocalDate.now().toString(), 0)

            dashboardViewModel.createNewGame(requireContext(), newGame)

             */

//            val intent = Intent(requireContext(), ScanGoogleActivity::class.java)
//            startActivity(intent)

            //Pour aller sur PDF Activity
            val difficulty = binding.spDifficulty.selectedItem.toString()
            val intent = Intent(requireContext(), PDFActivity::class.java)
            intent.putExtra("difficulty",difficulty)
            startActivity(intent)
        }

        val spinner: Spinner = binding.spDifficulty
        val difficulties = resources.getStringArray(R.array.difficulties)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, difficulties)
        spinner.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}