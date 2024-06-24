package com.orestepmr.projet_pmr.ui.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
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
import org.json.JSONObject

class ScanActivity : CaptureActivity() {

    private val VOICE_RECOGNITION_REQUEST_CODE = 1234
    private lateinit var arSceneView: ArSceneView
    private lateinit var transformationSystem: TransformationSystem


    private var answersNeeded: Int = 0
    private var buttons = mutableListOf<Button>()
    private var enigma = JSONObject()

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


        //AR
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
                //display3DChest(result.contents)
                Toast.makeText(this, "Chargement du modèle : ${result.contents}", Toast.LENGTH_LONG).show()
                setAnswerButtons(result.contents)

            }
        }
    }

    private fun display3DChest(contents: String) {
        //Gestion de la 3D pour afficher un coffre

        val anchor = arSceneView.session?.createAnchor(arSceneView.arFrame?.camera?.pose)
        if (anchor != null) {
            placeObject(arSceneView, anchor)
        }

        // Mettre un bouton invisible pour dire "ouvrir" avec la bibliothèque realwear
        //openChest(contents)
    }

    private fun placeObject(arSceneView: ArSceneView, anchor: Anchor) {
        MaterialFactory.makeOpaqueWithColor(this, com.google.ar.sceneform.rendering.Color(android.graphics.Color.WHITE))
            .thenAccept { material ->
                val modelRenderable = ShapeFactory.makeCube(Vector3(0.1f, 0.1f, 0.1f), Vector3.zero(), material)
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(arSceneView.scene)
                val node = TransformableNode(transformationSystem)
                node.renderable = modelRenderable
                node.setParent(anchorNode)
                node.select()
            }
    }

    private fun openChest(contents: String) {
        //Open the right enigma with the contents value of the QR code
        AlertDialog.Builder(this)
                    .setTitle("Scan Result")
                    .setMessage("QR Code content:\n${contents}")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
    }

    private fun showEnigmaDialog() {
        if (!buttons.isEmpty()) {
            // wait a second
            AlertDialog.Builder(this)
                .setTitle(enigma.getString("title"))
                .setMessage(enigma.getString("description"))
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }


    override fun onResume() {
        super.onResume()
        //setAnswerButtons()
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
                        cleanButtons()
                    }
                    it.isClickable = false
                }
                buttons.add(button)

                Log.i("Answer", "button created with answer: ${answers[i]}")
            }
        }
        // scan again
        //startQRScanner()

        if (!buttons.isEmpty()) {
            // wait a second
            AlertDialog.Builder(this)
                .setTitle(enigma.getString("title"))
                .setMessage(enigma.getString("description"))
                .setPositiveButton("OK") { dialog, _ ->
                    cleanButtons()
                    startQRScanner()
                    dialog.dismiss()
                }
                .create()
                .show()
        }

    }

    private fun cleanButtons() {
        val layout = findViewById<FrameLayout>(R.id.layout_view)
        buttons.forEach {
            it.isClickable = false
            layout.removeView(it)
        }
        buttons.clear()
    }


}