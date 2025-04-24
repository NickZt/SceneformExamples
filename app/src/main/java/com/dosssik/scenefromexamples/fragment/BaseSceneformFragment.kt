package com.dosssik.scenefromexamples.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.github.sceneview.SceneView
import io.github.sceneview.nodes.ModelNode
import io.github.sceneview.math.Quaternion
import io.github.sceneview.math.Vector3
import java.io.File

private const val MODEL_SCALE = 8F
private const val CAMERA_FOV = 60F
private const val MODEL_SHIFT_Y = 0.5f
private const val PATH_TO_MODEL = "model.glb"

abstract class BaseSceneformFragment(@LayoutRes layoutResId: Int) : Fragment(layoutResId) {

    open val needToRotate = true
    val modelNode = ModelNode()

    abstract fun onRenderableReady()
    abstract fun getSceneView(): SceneView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadModel()
    }

    override fun onResume() {
        super.onResume()
        getSceneView().resume()
    }

    override fun onPause() {
        getSceneView().pause()
        super.onPause()
    }

    override fun onDestroyView() {
        getSceneView().destroy()
        super.onDestroyView()
    }

    private fun loadModel() {
        modelNode.loadModelGlbAsync(requireContext(), PATH_TO_MODEL) {
            showModel()
            onRenderableReady()
        }
    }

    private fun showModel() {
        val camera = getSceneView().scene.camera
        camera.verticalFovDegrees = CAMERA_FOV
        val position = camera.worldPosition + camera.forward * 1.0f
        position.y -= MODEL_SHIFT_Y

        modelNode.setParent(getSceneView().scene)
        modelNode.worldPosition = position
        modelNode.scale = MODEL_SCALE

        if (needToRotate) {
            val vectorY = Vector3(0f, 1f, 0f)
            val rotationY = Quaternion.axisAngle(vectorY, 180f)
            modelNode.localRotation = rotationY
        }
    }
}