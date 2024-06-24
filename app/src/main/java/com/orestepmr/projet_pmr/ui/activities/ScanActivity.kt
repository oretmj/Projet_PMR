package com.orestepmr.projet_pmr.ui.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.orestepmr.projet_pmr.R
import com.google.ar.sceneform.ux.TransformationSystem
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import kotlin.random.Random
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable

class ScanActivity : CaptureActivity() {

    private val VOICE_RECOGNITION_REQUEST_CODE = 1234
    private lateinit var arSceneView: ArSceneView
    private lateinit var transformationSystem: TransformationSystem

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


        arSceneView = findViewById(R.id.ar_scene_view)
        val selectionVisualizer = FootprintSelectionVisualizer()
        transformationSystem = TransformationSystem(resources.displayMetrics, selectionVisualizer)



        findViewById<Button>(R.id.btn_new_scan).setOnClickListener {
            startQRScanner()
        }
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
               devinetteEnigme()

            }
        }
    }

//    private fun display3DChest(contents: String) {
//        //Gestion de la 3D pour afficher un coffre
//
//        val anchor = arSceneView.session?.createAnchor(arSceneView.arFrame?.camera?.pose)
//        if (anchor != null) {
//            placeObject(arSceneView, anchor)
//        }
//
//        // Mettre un bouton invisible pour dire "ouvrir" avec la bibliothèque realwear
//        //openChest(contents)
//    }
//
//    private fun placeObject(arSceneView: ArSceneView, anchor: Anchor) {
//        MaterialFactory.makeOpaqueWithColor(this, com.google.ar.sceneform.rendering.Color(android.graphics.Color.WHITE))
//            .thenAccept { material ->
//                val modelRenderable = ShapeFactory.makeCube(Vector3(0.1f, 0.1f, 0.1f), Vector3.zero(), material)
//                val anchorNode = AnchorNode(anchor)
//                anchorNode.setParent(arSceneView.scene)
//                val node = TransformableNode(transformationSystem)
//                node.renderable = modelRenderable
//                node.setParent(anchorNode)
//                node.select()
//            }
//    }
//
//    private fun openChest(contents: String) {
//        //Open the right enigma with the contents value of the QR code
//        AlertDialog.Builder(this)
//                    .setTitle("Scan Result")
//                    .setMessage("QR Code content:\n${contents}")
//                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//                    .create()
//                    .show()
//    }

    private fun addTextNode(text: String?) {
        ViewRenderable.builder()
            .setView(this, R.layout.view_renderable_text)
            .build()
            .thenAccept { viewRenderable ->
                viewRenderable.view.findViewById<TextView>(R.id.textView).text = text

                val textNode = Node().apply {
                    renderable = viewRenderable
                    setParent(arSceneView.scene)
                    localPosition = Vector3(0f, 1f, 0f) //Position du texte
                }
                arSceneView.scene.addChild(textNode)
            }
    }

    private fun calculEnigme(): Int {

        val operateurs = listOf("+", "-", "*")

        // Generate random operands and operator
        val nombre1 = Random.nextInt(1, 100)
        val nombre2 = Random.nextInt(1, 100)
        val operator = operateurs[Random.nextInt(operateurs.size)]

        // Create the calculation string
        val (calcul,resultat) = when (operator) {
            "+" -> "$nombre1 + $nombre2" to (nombre1 + nombre2)
            "-" -> "$nombre1 - $nombre2" to (nombre1 - nombre2)
            "*" -> "$nombre1 * $nombre2" to (nombre1 * nombre2)
            else -> "" to 0
        }
        addTextNode(calcul)
        return resultat
    }

    data class Devinette(val question: String, val reponse: String)

    private fun devinetteEnigme(): String {

        val listeDevinettes = listOf(
            Devinette("Devinette 1", "Reponse 1"),
            Devinette("Devinette 2", "Reponse 2"),
            Devinette("Devinette 3", "Reponse 3"),
            Devinette("Devinette 4", "Reponse 4"),
            Devinette("Devinette 5", "Reponse 5")
        )
        val random = Random.nextInt(listeDevinettes.size)
        val devinetteChoix = listeDevinettes[random]
        addTextNode(devinetteChoix.question)
        return devinetteChoix.reponse
    }
}
