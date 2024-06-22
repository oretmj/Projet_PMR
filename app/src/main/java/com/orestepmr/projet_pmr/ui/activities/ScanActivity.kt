package com.orestepmr.projet_pmr.ui.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.orestepmr.projet_pmr.R
import com.google.ar.sceneform.ux.TransformationSystem
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer

class ScanActivity : CaptureActivity() {

    private val VOICE_RECOGNITION_REQUEST_CODE = 1234
    private lateinit var arSceneView: ArSceneView
    private lateinit var transformationSystem: TransformationSystem
    private lateinit var timerTextView: TextView
    private lateinit var countDownTimer: CountDownTimer



    companion object {
        private const val REQUEST_CAMERA = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            setupScanner()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        }

        timerTextView = findViewById(R.id.timer_text_view)
        startTimer(300000) // 5 minutes en millisecondes

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupScanner()
        } else {
            // Gérer le cas où la permission n'est pas accordée
        }
    }

    private fun setupScanner() {
        setContentView(R.layout.activity_scan)


        //AR
        arSceneView = findViewById(R.id.ar_scene_view)
        val selectionVisualizer = FootprintSelectionVisualizer()
        transformationSystem = TransformationSystem(resources.displayMetrics, selectionVisualizer)

        findViewById<Button>(R.id.btn_new_scan).setOnClickListener {
            startQRScanner()
        }
        startTimer(300000) // 5 minutes en millisecondes

    }
    private fun startQRScanner() {
        // Configuration et lancement du scanner ZXing
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setCameraId(0)  // 0 pour la caméra arrière
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    // Handling the scan result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                // Handle case where no QR code was found
                AlertDialog.Builder(this)
                    .setTitle("Scanning Cancelled")
                    .setMessage("No QR Code captured, the scanner was closed.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            } else {
              /*
                // Display the result in a dialog or use the result in other ways
//                AlertDialog.Builder(this)
//                    .setTitle("Scan Result")
//                    .setMessage("QR Code content:\n${result.contents}")
//                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//                    .create()
//                    .show()
                Toast.makeText(this, "Chargement du modèle : ${result.contents}", Toast.LENGTH_LONG).show()
                val intent = Intent(this, ARActivity::class.java)
                intent.putExtra("MODEL_URI", result.contents)
                startActivity(intent)
            }
        }
        */

                // Display 3D object
                display3DChest(result.contents)

            }
        }
    }

    private fun display3DChest(contents: String) {
        //Gestion de la 3D pour afficher un coffre

        val intent = Intent(this, ARActivity::class.java)
        intent.putExtra("MODEL_URL", contents)
        startActivity(intent)

        // Mettre un bouton invisible pour dire "ouvrir" avec la bibliothèque realwear
        //openChest(contents)
    }

    private fun startTimer(timeInMillis: Long) {
        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                timerTextView.text = "Time's up!"
                // Ajoutez ici ce que vous voulez faire lorsque le temps est écoulé
            }
        }
        countDownTimer.start()
    }


}