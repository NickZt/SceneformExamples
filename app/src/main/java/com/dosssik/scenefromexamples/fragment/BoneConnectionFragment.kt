package com.dosssik.scenefromexamples.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dosssik.scenefromexamples.databinding.BoneConnectionFragmentBinding
import io.github.sceneview.SceneView
import io.github.sceneview.nodes.ModelNode

private const val HAT_SCALE = 0.25F
private const val HAT_SHIFT_Y = 0.08F

class BoneConnectionFragment : Fragment() {
    private var _binding: BoneConnectionFragmentBinding? = null
    private val binding get() = _binding!!
    private var modelNode: ModelNode? = null
    private var hatNode: ModelNode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BoneConnectionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sceneView = binding.sceneView
        modelNode = ModelNode()
        sceneView.addChild(modelNode!!)
        modelNode?.loadModelGlbAsync(requireContext(), "robot.glb") {
            addHat()
            enableButtons()
        }
    }

    private fun addHat() {
        hatNode = ModelNode()
        modelNode?.addChild(hatNode!!)
        hatNode?.loadModelGlbAsync(requireContext(), "baseball-cap.glb") {
            hatNode?.scale = HAT_SCALE
            hatNode?.position = hatNode?.position?.apply { y -= HAT_SHIFT_Y } ?: return@loadModelGlbAsync
        }
    }

    private fun enableButtons() {
        binding.startAnimation.isEnabled = true
        binding.startAnimation.setOnClickListener {
            playRandomAnimation()
        }
        binding.showOrHideConnectedNode.isEnabled = true
        binding.showOrHideConnectedNode.setOnClickListener {
            hatNode?.isVisible = hatNode?.isVisible?.not() ?: true
        }
    }

    private fun playRandomAnimation() {
        modelNode?.let { node ->
            val animations = node.animations
            if (animations.isNotEmpty()) {
                val random = (animations.indices).random()
                node.playAnimation(animations[random].name)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}