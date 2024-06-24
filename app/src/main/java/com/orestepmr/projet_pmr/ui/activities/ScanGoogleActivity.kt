package com.orestepmr.projet_pmr.ui.activities

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.orestepmr.projet_pmr.R
import org.json.JSONObject
import java.util.jar.Manifest


class ScanGoogleActivity : AppCompatActivity() {

    private lateinit var cameraPreview: PreviewView

    private var answersNeeded: Int = 0
    private var buttons = mutableListOf<Button>()
    private var enigma = JSONObject()

    private var clues = mutableListOf<String>()

    companion object {
        private const val REQUEST_CAMERA = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_google)
        cameraPreview = findViewById(R.id.camera_preview)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startScanning()

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                ScanGoogleActivity.REQUEST_CAMERA
            )
        }


    }

    fun startScanning() {
        val scanner = GmsBarcodeScanning.getClient(this)
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val rawValue: String? = barcode.rawValue
                // Affichez le résultat avec un Toast
                Toast.makeText(this, "Scanned: $rawValue", Toast.LENGTH_LONG).show()
                // La caméra continue de fonctionner en arrière-plan
                setAnswerButtons(rawValue!!)
                startCamera()
            }
            .addOnCanceledListener {
                // Tâche annulée
            }
            .addOnFailureListener { e ->
                // Tâche échouée avec une exception
                Toast.makeText(this, "Scan Failed: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("Scan", "Scan Failed: ${e.message}")
            }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraPreview.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)
            } catch(exc: Exception) {
                Log.e("test", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun setAnswerButtons(enigmaQr: String) {
        val file = this.assets.open("enigmas.json")
        // read all enigmas and save them in a list
        val json = JSONObject(file.reader().readText()).getJSONArray("enigmas")

        // get a random enigma
        //val enigma = json.getJSONObject((0 until json.length()).random())

        // get enigma with type enigmaQr
        enigma = JSONObject()
        for (i in 0 until json.length()) {
            enigma = json.getJSONObject(i)
            if (enigma.getString("type") == enigmaQr) {
                break
            }
        }

        //val layout = findViewById<FrameLayout>(R.id.layout_view)
        cleanButtons()

        Log.d("Enigma", enigma.toString())
        if (enigma.getBoolean("needsAnswer")) {
            val answers = enigma.getString("answer").toString().split(",")
            answersNeeded = answers.size
            for (i in 0 until answers.size) {
                val button = Button(this)
                button.text = answers[i]
                button.setOnClickListener {
                    Log.d("Answer", button.text.toString())
                    answersNeeded--
                    if (answersNeeded == 0) {
                        //setAnswerButtons()
                        Toast.makeText(this, "Enigme résolue", Toast.LENGTH_LONG).show()
                        clues.add(enigma.getString("clue"))
                        checkLastClue()
                        cleanButtons()
                    }
                    it.isClickable = false
                }
                buttons.add(button)

                // auto click du button pour tester
                //button.callOnClick()

                Log.i("Answer", "button created with answer: ${answers[i]}")
            }
            AlertDialog.Builder(this)
                .setTitle(enigma.getString("title"))
                .setMessage(enigma.getString("description"))
                .setPositiveButton("Retour") { dialog, _ ->
                    cleanButtons()
                    startScanning()
                    dialog.dismiss()
                }
                .create()
                .show()
        } else if (enigma.getBoolean("needsClue")) {
            if (clues.contains(enigma.getString("clueKey"))) {
                Toast.makeText(this, "Vous avez déjà la clé", Toast.LENGTH_LONG).show()
                clues.add(enigma.getString("clue"))
                checkLastClue()
            } else {
                AlertDialog.Builder(this)
                    .setTitle(enigma.getString("title"))
                    .setMessage(enigma.getString("description") + "\n\nTrouvez la clé pour déverrouiller l'énigme")
                    .setPositiveButton("Retour") { dialog, _ ->
                        cleanButtons()
                        startScanning()
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
        // scan again
        //startQRScanner()
    }


    private fun cleanButtons() {
        val layout = findViewById<FrameLayout>(R.id.layout_view)
        buttons.forEach {
            it.isClickable = false
            //layout.removeView(it)
        }
        buttons.clear()
    }

    private fun checkLastClue() {
        if (clues.last() == "--FIN--") {
            AlertDialog.Builder(this)
                .setTitle("Félicitations")
                .setMessage("Vous avez résolu toutes les énigmes")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .create()
                .show()
        }
    }
}
