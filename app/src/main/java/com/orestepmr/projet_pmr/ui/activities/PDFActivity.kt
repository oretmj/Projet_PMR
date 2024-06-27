package com.orestepmr.projet_pmr.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

private lateinit var binding: ActivityPdfBinding
private val REQUEST_CODE_PERMISSION = 1001
class PDFActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
        }

        val bundle = this.getIntent().getExtras()
        val difficulty : String = bundle?.getString("difficulty").toString() //On récupère la difficulté

        val level : String
        if (difficulty == "Easy") {
            level = "level1"
        } else if (difficulty == "Medium") {
            level = "level2"
        } else {
            level = "level3"
        }

        var button_down = findViewById<Button>(R.id.btn_download)
        var button_partie = findViewById<Button>(R.id.btn_new_game)

        button_down.setOnClickListener {
            telechargePDF("$level.pdf")
        }

        button_partie.setOnClickListener { //Demarrage de la partie
            val intent = Intent(this, ScanActivity::class.java)
            intent.putExtra("level",level)
            startActivity(intent)
        }

    }

    private fun telechargePDF(nom: String) {
        val assetManager = assets
        val inputStream: InputStream
        try {
            inputStream = assetManager.open(nom)
            val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val outFile = File(downloadFolder, nom)
            val outputStream = FileOutputStream(outFile)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Toast.makeText(this,"PDF téléchargé vers : ${outFile.absolutePath}",Toast.LENGTH_SHORT).show()
//            openPDF(outFile)
        } catch (e: IOException) {
            Toast.makeText(this,"Erreur : $e",Toast.LENGTH_SHORT).show()
        }
    }

//    private fun openPDF(pdf: File) {
//        val intent = Intent(Intent.ACTION_VIEW).apply {
//            setDataAndType(Uri.parse(pdf.toString()), DocumentsContract.Document.MIME_TYPE_DIR)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        }
//        if (intent.resolveActivity(packageManager) != null) {
//            startActivity(intent)
//        } else {
//            Log.e("MainActivity", "No file explorer found")
//            Toast.makeText(this,"Erreur impossible d'ouvrir l'explorateur de fichiers",Toast.LENGTH_SHORT).show()
//        }
//    }
}