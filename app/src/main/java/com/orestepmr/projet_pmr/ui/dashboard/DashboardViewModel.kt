package com.orestepmr.projet_pmr.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room.databaseBuilder
import com.orestepmr.projet_pmr.data.AppDatabase
import com.orestepmr.projet_pmr.models.Game
import androidx.room.Room
class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text


}