package com.orestepmr.projet_pmr.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONObject
import com.orestepmr.projet_pmr.R


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
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch(exc: Exception) {
                Log.e("test", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun startScanning() {
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Scan a barcode")
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                setAnswerButtons(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setAnswerButtons(enigmaQr: String) {
        val file = this.assets.open("enigmas.json")
        val json = JSONObject(file.reader().readText()).getJSONArray("level1")
        enigma = JSONObject()
        for (i in 0 until json.length()) {
            enigma = json.getJSONObject(i)
            if (enigma.getString("type") == enigmaQr) {
                break
            }
        }

        cleanButtons()

        if (enigma.getBoolean("needsAnswer")) {
            val answers = enigma.getString("answer").toString().split(",")
            answersNeeded = answers.size
            for (i in answers.indices) {
                val button = Button(this)
                button.text = answers[i]
                button.setOnClickListener {
                    answersNeeded--
                    if (answersNeeded == 0) {
                        Toast.makeText(this, "Enigme résolue", Toast.LENGTH_LONG).show()
                        clues.add(enigma.getString("clue"))
                        checkLastClue()
                        cleanButtons()
                    }
                    it.isClickable = false
                }
                buttons.add(button)

                // add this to automatic click of buttons and complete the enigma
                //button.callOnClick()
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
                startScanning()
            } else {
                AlertDialog.Builder(this)
                    .setTitle(enigma.getString("title"))
                    .setMessage(enigma.getString("description") + "\n\nTrouvez ${enigma.getString("clueKey")} pour déverrouiller l'énigme")
                    .setPositiveButton("Retour") { dialog, _ ->
                        cleanButtons()
                        startScanning()
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        } else {
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
            clues.add(enigma.getString("clue"))
            checkLastClue()
        }
    }

    private fun cleanButtons() {
        val layout = findViewById<FrameLayout>(R.id.layout_view)
        buttons.forEach {
            it.isClickable = false
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

