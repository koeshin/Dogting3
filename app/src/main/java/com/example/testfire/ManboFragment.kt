package com.example.testfire

import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorEvent
import android.widget.Toast

/**
 * A simple [Fragment] subclass.
 * Use the [ManboFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManboFragment : Fragment(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepCountSensor: Sensor? = null
    private var stepCountView: TextView? = null
    private var resetButton: Button? = null
    private var currentSteps = 0



    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manbo, container, false)
        stepCountView = view.findViewById(R.id.stepCountView)
        resetButton = view.findViewById(R.id.resetButton)

        // 활동 퍼미션 체크
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                0
            )
        }

        // 걸음 센서 연결
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        // 디바이스에 걸음 센서의 존재 여부 체크
        if (stepCountSensor == null) {
            Toast.makeText(requireContext(), "No Step Sensor", Toast.LENGTH_SHORT).show()
        }

        // 리셋 버튼 추가 - 리셋 기능
        resetButton?.setOnClickListener {
            // 현재 걸음수 초기화
            currentSteps = 0
            stepCountView?.text = currentSteps.toString()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        if (stepCountSensor != null) {
            sensorManager?.registerListener(
                this,
                stepCountSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onStop() {
        super.onStop()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        // 걸음 센서 이벤트 발생시
        if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
            // 센서 이벤트가 발생할때 마다 걸음수 증가
            currentSteps++
            stepCountView?.text = currentSteps.toString()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    companion object {
        fun newInstance(): ManboFragment {
            return ManboFragment()
        }
    }
}