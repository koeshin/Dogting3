package com.example.testfire

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.testfire.Fragment.ProfileFragment
import com.example.testfire.model.Friend
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.FileInputStream
import java.io.FileOutputStream

class CalendarFragment : Fragment() {

    private val fireStorage = FirebaseStorage.getInstance().reference
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private val user = Firebase.auth.currentUser //회원정보
    private val uid = user?.uid.toString()
    private var fname: String? = null
    private var str: String? = null
    private var calendarView: CalendarView? = null
    private var cha_Btn: Button? = null
    private var del_Btn: Button? = null
    private var save_Btn: Button? = null
    private var diaryTextView: TextView? = null
    private var textView2: TextView? = null
    private var textView3: TextView? = null
    private var contextEditText: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        diaryTextView = view.findViewById(R.id.diaryTextView)
        save_Btn = view.findViewById(R.id.manbo_save_Btn)
        del_Btn = view.findViewById(R.id.del_Btn)
        cha_Btn = view.findViewById(R.id.cha_Btn)
        textView2 = view.findViewById(R.id.textmemo)
        textView3 = view.findViewById(R.id.textView3)
        contextEditText = view.findViewById(R.id.contextEditText)
        val currentSteps = arguments?.getInt("currentSteps", 0) ?: 0
        textView2?.text = currentSteps.toString()

        // 로그인 및 회원가입 엑티비티에서 이름을 받아옴
        ProfileFragment.fireDatabase.child("users").child(ProfileFragment.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue<Friend>()
                val name=userProfile?.name
                textView3?.text = "$name 님의 산책 일기장"
            }
        })


        calendarView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            diaryTextView?.visibility = View.VISIBLE
            save_Btn?.visibility = View.VISIBLE
            contextEditText?.visibility = View.VISIBLE
            textView2?.visibility = View.INVISIBLE
            cha_Btn?.visibility = View.INVISIBLE
            del_Btn?.visibility = View.INVISIBLE
            diaryTextView?.text = String.format("%d / %d / %d", year, month + 1, dayOfMonth)
            contextEditText?.setText("")
            checkDay(year, month, dayOfMonth, uid)
        }

        save_Btn?.setOnClickListener {
            saveDiary(fname)
            str = contextEditText?.text.toString()
            textView2?.text = str
            save_Btn?.visibility = View.INVISIBLE
            cha_Btn?.visibility = View.VISIBLE
            del_Btn?.visibility = View.VISIBLE
            contextEditText?.visibility = View.INVISIBLE
            textView2?.visibility = View.VISIBLE
        }

        return view
    }

    private fun checkDay(cYear: Int, cMonth: Int, cDay: Int, uid: String?) {
        fname = "$uid$cYear-${cMonth + 1}$cDay.txt" // 저장할 파일 이름 설정
        var fis: FileInputStream? = null // FileStream fis 변수
        try {
            fis = requireContext().openFileInput(fname)
            val fileData = ByteArray(fis.available())
            fis.read(fileData)
            fis.close()
            str = String(fileData)
            contextEditText?.visibility = View.INVISIBLE
            textView2?.visibility = View.VISIBLE
            textView2?.text = str
            save_Btn?.visibility = View.INVISIBLE
            cha_Btn?.visibility = View.VISIBLE
            del_Btn?.visibility = View.VISIBLE

            cha_Btn?.setOnClickListener {
                contextEditText?.visibility = View.VISIBLE
                textView2?.visibility = View.INVISIBLE
                contextEditText?.setText(str)
                save_Btn?.visibility = View.VISIBLE
                cha_Btn?.visibility = View.INVISIBLE
                del_Btn?.visibility = View.INVISIBLE
                textView2?.text = contextEditText?.text
            }

            del_Btn?.setOnClickListener {
                textView2?.visibility = View.INVISIBLE
                contextEditText?.setText("")
                contextEditText?.visibility = View.VISIBLE
                save_Btn?.visibility = View.VISIBLE
                cha_Btn?.visibility = View.INVISIBLE
                del_Btn?.visibility = View.INVISIBLE
                removeDiary(fname)
            }

            if (textView2?.text.isNullOrEmpty()) {
                textView2?.visibility = View.INVISIBLE
                diaryTextView?.visibility = View.VISIBLE
                save_Btn?.visibility = View.VISIBLE
                cha_Btn?.visibility = View.INVISIBLE
                del_Btn?.visibility = View.INVISIBLE
                contextEditText?.visibility = View.VISIBLE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeDiary(readDay: String?) {
        var fos: FileOutputStream? = null
        try {
            fos = requireContext().openFileOutput(readDay, AppCompatActivity.MODE_PRIVATE)
            val content = ""
            fos.write(content.toByteArray())
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveDiary(readDay: String?) {
        var fos: FileOutputStream? = null
        try {
            fos = requireContext().openFileOutput(readDay, AppCompatActivity.MODE_PRIVATE)
            val content = contextEditText?.text.toString()
            fos.write(content.toByteArray())
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        fun newInstance(): CalendarFragment {
            return CalendarFragment()
        }
    }

}
