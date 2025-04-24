package com.dosssik.scenefromexamples.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.fragment.app.Fragment
import com.dosssik.scenefromexamples.databinding.FlingFragmentBinding
import io.github.sceneview.math.Vector3
import io.github.sceneview.math.Quaternion
import io.github.sceneview.nodes.Node
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val SWIPE_THRESHOLD_VELOCITY: Float = 10F
private const val ROTATION_FRICTION: Float = 3F

class FlingAnimationFragment : Fragment() {
    private var _binding: FlingFragmentBinding? = null
    private val binding get() = _binding!!

    private var lastDeltaYAxisAngle: Float = 0F
    private val quaternion = Quaternion()
    private val rotateVector = Vector3.up()
    private lateinit var mDetector: GestureDetectorCompat
    private val robotNode = Node()

    private val rotationProperty = object : FloatPropertyCompat<Node>("rotation") {
        override fun setValue(node: Node, value: Float) {
            node.localRotation = getRotationQuaternion(value)
        }
        override fun getValue(node: Node): Float = node.localRotation.y
    }

    private val animation: FlingAnimation by lazy {
        FlingAnimation(robotNode, rotationProperty).apply {
            minimumVisibleChange = DynamicAnimation.MIN_VISIBLE_CHANGE_ROTATION_DEGREES
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FlingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDetector = GestureDetectorCompat(requireContext(), FlingGestureDetector())
        binding.sceneView.setOnTouchListener { _, event ->
            mDetector.onTouchEvent(event)
        }
        // binding.sceneView.addChild(robotNode) // Добавьте, если требуется
    }

    private fun getRotationQuaternion(deltaYAxisAngle: Float): Quaternion {
        lastDeltaYAxisAngle = deltaYAxisAngle
        return quaternion.apply {
            val arc = Math.toRadians(deltaYAxisAngle.toDouble())
            val axis = sin(arc / 2.0)
            x = (rotateVector.x * axis).toFloat()
            y = (rotateVector.y * axis).toFloat()
            z = (rotateVector.z * axis).toFloat()
            w = (cos(arc / 2.0)).toFloat()
            normalize()
        }
    }

    inner class FlingGestureDetector : android.view.GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val deltaX = -(distanceX / resources.displayMetrics.density) / ROTATION_FRICTION
            robotNode.localRotation = getRotationQuaternion(lastDeltaYAxisAngle + deltaX)
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                val deltaVelocity =
                    (velocityX / resources.displayMetrics.density) / ROTATION_FRICTION
                startAnimation(deltaVelocity)
            }
            return true
        }
    }

    private fun startAnimation(velocity: Float) {
        if (!animation.isRunning) {
            animation.setStartVelocity(velocity)
            animation.setStartValue(lastDeltaYAxisAngle)
            animation.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}