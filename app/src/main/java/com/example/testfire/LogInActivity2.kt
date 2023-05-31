package com.example.testfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInActivity2: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in2)
        auth = Firebase.auth

        // 이메일과 비밀번호에 대한 EditText 초기화
        val email = findViewById<EditText>(R.id.et_login_id)
        val password = findViewById<EditText>(R.id.et_login_password)

        // 로그인 버튼 클릭 시
        val btn_login = findViewById<Button>(R.id.profile_button)
        btn_login.setOnClickListener{
            if(email.text.isEmpty() && password.text.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 제대로 입력해주세요.", Toast.LENGTH_SHORT).show()
                Log.d("Email", "$email, $password")
                email.setText("")
                password.setText("")
            }
            else{
                signIn(email.text.toString(), password.text.toString())
            }
        }

        // 회원가입창으로 이동하는 버튼 클릭 시
        val btn_registration = findViewById<Button>(R.id.btn_registration)
        btn_registration.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    // 이메일과 비밀번호를 사용하여 Firebase에 로그인하는 메서드
    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        val intentMain = Intent(this, MainActivity::class.java)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("로그인", "성공")
                    val user = auth.currentUser
                    updateUI(user)
                    finish()
                    startActivity(intentMain)
                } else {
                    // 로그인 실패 시 사용자에게 메시지 표시
                    Toast.makeText(this, "정확한 아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    Log.d("로그인", "실패")
                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }

    // 사용자 인터페이스(UI)를 업데이트하는 메서드
    private fun updateUI(user: FirebaseUser?) {
        // 필요한 UI 업데이트 작업 수행
    }

    public override fun onStart() {
        super.onStart()
        // 사용자가 로그인한 상태인지 확인하고 UI를 업데이트
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    // 필요한 경우 UI를 업데이트하는 메서드
    private fun reload() {
        // 필요한 UI 업데이트 작업 수행
    }
}
