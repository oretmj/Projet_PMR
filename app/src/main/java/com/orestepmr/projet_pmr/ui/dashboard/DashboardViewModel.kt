package com.orestepmr.projet_pmr.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orestepmr.projet_pmr.models.Game

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text


    fun dummyGetGamesList(): List<Game> {
        // Call to the API to get the list of games

        // for now we will return a dummy list
        var counter = 0
        return List(10) {
            counter++
            Game(
                counter,
                counter,
                counter,
                counter,
                "Game Master $counter",
                "Game $counter",
                "2021-10-10",
                1000
            )
        }
    }
}