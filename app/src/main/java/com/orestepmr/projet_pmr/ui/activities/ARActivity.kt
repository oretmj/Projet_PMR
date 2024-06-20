package com.orestepmr.projet_pmr.ui.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.orestepmr.projet_pmr.R

class ARActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        val modelUri = intent.getStringExtra("MODEL_URI")
        val text = intent.getStringExtra("TEXT")
        load3DModel(modelUri, text)
    }

    private fun load3DModel(modelUri: String?, text: String?) {
        if (modelUri == null) {
            Log.e("ARActivity", "Model URI is null")
            return
        }

        val uri = Uri.parse("file:///android_asset/$modelUri")
        ModelRenderable.builder()
            .setSource(this, RenderableSource.builder().setSource(
                this,
                uri,
                RenderableSource.SourceType.GLB
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

                    if (text != null) {
                        addTextNode(anchorNode, text)
                    }
                }
            }
            .exceptionally { throwable ->
                Log.e("ARActivity", "Error loading model", throwable)
                null
            }
    }

    private fun addTextNode(parent: AnchorNode, text: String) {
        ViewRenderable.builder()
            .setView(this, R.layout.view_renderable_text)
            .build()
            .thenAccept { viewRenderable ->
                val textView = viewRenderable.view as TextView
                textView.text = text

                val textNode = TransformableNode(arFragment.transformationSystem)
                textNode.setParent(parent)
                textNode.renderable = viewRenderable
                textNode.localPosition = Vector3(0.0f, 1.0f, 0.0f)
            }
    }
}