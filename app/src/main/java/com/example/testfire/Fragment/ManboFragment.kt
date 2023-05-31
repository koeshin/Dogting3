import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.testfire.CalendarFragment
import com.example.testfire.R
import com.example.testfire.model.walkNmemo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.MonthDay
import java.time.Year
import java.util.*

class ManboFragment : Fragment(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepCountSensor: Sensor? = null
    private var stepCountView: TextView? = null
    private var resetButton: Button? = null
    private var saveButton: Button? = null
    private var memoTextView: TextView? = null
    private var currentSteps = 0
    private var imageViewCalendar: ImageView? = null
    private var memoEditText:EditText?=null
    private var endButton: Button?=null
    private var isRecording = false
    private lateinit var currentUserUid: String




    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manbo, container, false)
        stepCountView = view.findViewById(R.id.stepCountView)
        resetButton = view.findViewById(R.id.resetButton)
        endButton = view.findViewById(R.id.endbutton)
        saveButton = view.findViewById(R.id.manbo_save_Btn)
        memoTextView = view.findViewById(R.id.textmemo)
        imageViewCalendar = view.findViewById(R.id.imageView_calendar)
        memoEditText=view.findViewById(R.id.memoEditText)
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
        sensorManager =
            requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
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
        endButton?.setOnClickListener{
            memoTextView?.visibility= View.VISIBLE
            saveButton?.visibility=View.VISIBLE
            memoEditText?.visibility=View.VISIBLE
            resetButton?.visibility=View.INVISIBLE
            endButton?.visibility=View.INVISIBLE

        }
        saveButton?.setOnClickListener {
            if (isRecording) {
                saveButton?.text = "저장"
                memoTextView?.visibility = View.INVISIBLE
                memoEditText?.visibility=View.INVISIBLE
                saveButton
                isRecording = false
            } else {
                saveButton?.text = "저장 중..."
                memoTextView?.visibility = View.VISIBLE
                memoEditText?.visibility=View.VISIBLE
                val memo = memoEditText?.text.toString()
                saveDataToFirebase(currentSteps.toString(), memo)
                memoTextView?.visibility = View.INVISIBLE
                memoEditText?.visibility=View.INVISIBLE
                saveButton?.visibility=View.INVISIBLE
                resetButton?.visibility=View.VISIBLE
                endButton?.visibility=View.VISIBLE

            }
        }

        imageViewCalendar?.setOnClickListener {
            val calendarFragment = CalendarFragment.newInstance()
            calendarFragment.arguments = Bundle().apply {

            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragments_frame, calendarFragment)
                .addToBackStack(null)
                .commit()
        }

        // 현재 사용자 UID 가져오기
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

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

    private fun saveDataToFirebase(steps: String, memo: String) {
        val data = walkNmemo(
            walk = steps,
            memo = memo,
            year = Year.now().value.toString(),
            month = Month.from(LocalDate.now()).value.toString(),
            monthDay = MonthDay.from(LocalDate.now()).dayOfMonth.toString()
        )
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child("walkData").child(currentUserUid)
            .setValue(data)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data saved to Firebase", Toast.LENGTH_SHORT)
                    .show()
                resetFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to save data to Firebase",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun resetFields() {
        currentSteps = 0
        stepCountView?.text = currentSteps.toString()
        memoTextView?.text = ""
        saveButton?.text = "저장"
        memoTextView?.visibility = View.INVISIBLE
        isRecording = false
    }

    companion object {
        fun newInstance(): ManboFragment {
            return ManboFragment()
        }
    }
}
