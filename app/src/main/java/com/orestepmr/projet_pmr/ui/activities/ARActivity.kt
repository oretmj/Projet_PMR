package com.orestepmr.projet_pmr.ui.activities

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.orestepmr.projet_pmr.R
import kotlin.random.Random

class ARActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        val modelUri = intent.getStringExtra("MODEL_URI") //On récupère depuis le QR code le nom de l'enigme
        if (modelUri == "chest.obj" || modelUri == "cadena.obj") { // Si c'est un modèle 3d
            load3DModel(modelUri)
        } else if (modelUri == "calcul"){ //Si c'est un calcul
            val resultat = calculEnigme() //On affiche et stock le resultat
        } else if (modelUri =="devinette"){ //Si c'est une devinette
            val reponse = devinetteEnigme() //On affiche et stock la réponse
        }

    }

    private fun load3DModel(modelUri: String?) {
        if (modelUri == null) {
            Log.e("ARActivity", "Model URI is null")
            return
        }

        val uri = Uri.parse("file:///android_asset/$modelUri") //Cherche le modèle dans les asset de l'appli
        ModelRenderable.builder().setSource(this, RenderableSource.builder().setSource(this, uri, RenderableSource.SourceType.GLB
            ).build())
            .build()
            .thenAccept { renderable ->
                arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
                    val anchor = hitResult.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFragment.arSceneView.scene)

                    val modelNode = TransformableNode(arFragment.transformationSystem)
                    modelNode.setParent(anchorNode)
                    modelNode.renderable = renderable
                    modelNode.select()
                }
            }
    }

    private fun addTextNode(text: String?) {
        ViewRenderable.builder()
            .setView(this, R.layout.view_renderable_text)
            .build()
            .thenAccept { viewRenderable ->
                viewRenderable.view.findViewById<TextView>(R.id.textView).text = text

                val textNode = Node().apply {
                    renderable = viewRenderable
                    setParent(arFragment.arSceneView.scene)
                    localPosition = Vector3(0f, 1f, 0f) //Position du texte
                }
                arFragment.arSceneView.scene.addChild(textNode)
            }
    }

    private fun addColorSquareNode(color: String, position: Vector3) {
        ViewRenderable.builder()
            .setView(this, R.layout.view_renderable_color_square)
            .build()
            .thenAccept { viewRenderable ->
                viewRenderable.view.setBackgroundColor(Color.parseColor(color))

                val colorSquareNode = Node().apply {
                    renderable = viewRenderable
                    setParent(arFragment.arSceneView.scene)
                    localPosition = position
                }

                arFragment.arSceneView.scene.addChild(colorSquareNode)
            }
            .exceptionally {
                Toast.makeText(this, "Unable to load color square view", Toast.LENGTH_SHORT).show()
                null
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