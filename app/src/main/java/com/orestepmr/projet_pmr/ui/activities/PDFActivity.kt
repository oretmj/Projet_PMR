package com.orestepmr.projet_pmr.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.orestepmr.projet_pmr.R
import com.orestepmr.projet_pmr.databinding.ActivityMainBinding
import com.orestepmr.projet_pmr.databinding.ActivityPdfBinding

private lateinit var binding: ActivityPdfBinding
class PDFActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = this.getIntent().getExtras()
        val difficulty : String = bundle?.getString("difficulty").toString() //On récupère la difficulté

        findViewById<Button>(R.id.btn_new_game).setOnClickListener { //Demarrage de la partie
            val intent = Intent(this, ScanGoogleActivity::class.java)
            startActivity(intent)
        }

    }
}