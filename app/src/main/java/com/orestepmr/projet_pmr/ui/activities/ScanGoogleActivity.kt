package com.orestepmr.projet_pmr.ui.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import java.util.jar.Manifest


class ScanGoogleActivity : AppCompatActivity() {

    private lateinit var cameraPreview: PreviewView

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
                startCamera()
            }
            .addOnCanceledListener {
                // Tâche annulée
            }
            .addOnFailureListener { e ->
                // Tâche échouée avec une exception
                Toast.makeText(this, "Scan Failed: ${e.message}", Toast.LENGTH_LONG).show()
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
}
