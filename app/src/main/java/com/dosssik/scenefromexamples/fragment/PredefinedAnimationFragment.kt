package com.dosssik.scenefromexamples.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dosssik.scenefromexamples.databinding.PredefinedAnimationFragmentLayoutBinding
import io.github.sceneview.SceneView
import io.github.sceneview.nodes.ModelNode

class PredefinedAnimationFragment : Fragment() {
    private var _binding: PredefinedAnimationFragmentLayoutBinding? = null
    private val binding get() = _binding!!
    private var modelNode: ModelNode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PredefinedAnimationFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sceneView = binding.sceneView
        modelNode = ModelNode()
        sceneView.addChild(modelNode!!)
        modelNode?.loadModelGlbAsync(requireContext(), "model.glb") {
            binding.startAnimation.isEnabled = modelNode?.animations?.isNotEmpty() == true
        }
        binding.startAnimation.setOnClickListener {
            playRandomAnimation()
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