package com.orestepmr.projet_pmr.ui.activities

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.orestepmr.projet_pmr.R
import com.orestepmr.projet_pmr.databinding.ActivityArMainBinding

class MainARActivity : AppCompatActivity() {
    //lateinit for Augmented Reality Fragment
    private lateinit var arFragment: ArFragment
    //lateinit for the model uri
    private lateinit var selectedObject: Uri

    private lateinit var binding: ActivityArMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init Fragment
        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment_view) as ArFragment

        //Default model
        setModelPath("rocket.sfb")

        //Tab listener for the ArFragment
        arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->
            //If surface is not horizontal and upward facing
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                //return for the callback
                return@setOnTapArPlaneListener
            }
            //create a new anchor
            val anchor = hitResult.createAnchor()
            placeObject(arFragment, anchor, selectedObject)
        }

        //Click listener for lamp and table objects
        val smallTable = binding.smallTable
        val bigLamp = binding.bigLamp
        smallTable.setOnClickListener {
            setModelPath("model.sfb")
        }
        bigLamp.setOnClickListener {
            setModelPath("LampPost.sfb")
        }

    }

    /***
     * function to handle the renderable object and place object in scene
     */
    private fun placeObject(fragment: ArFragment, anchor: Anchor, modelUri: Uri) {
        val modelRenderable = ModelRenderable.builder()
            .setSource((fragment.requireContext()), modelUri)
            .build()
        //when the model render is build add node to scene
        modelRenderable.thenAccept { renderableObject -> addNodeToScene(fragment, anchor, renderableObject) }
        //handle error
        modelRenderable.exceptionally {
            val toast = Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
            toast.show()
            null
        }
    }

    /***
     * Function to a child anchor to a new scene.
     */
    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderableObject: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = renderableObject
        transformableNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }

    /***
     * function to get the model resource on assets directory for each figure.
     */
    private fun setModelPath(modelFileName: String) {
        selectedObject = Uri.parse(modelFileName)
        val toast = Toast.makeText(applicationContext, modelFileName, Toast.LENGTH_SHORT)
        toast.show()
    }
}
