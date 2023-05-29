package com.example.testfire

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage



lateinit var auth: FirebaseAuth
lateinit var database: DatabaseReference

@Suppress("DEPRECATION")
class RegistrationActivity: AppCompatActivity() {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private  var registration_iv : ImageView? =null
    private var imageUri : Uri? = null
   
    //이미지 등록
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data // 이미지 경로 원본
            registration_iv?.setImageURI(imageUri) // 이미지 뷰를 바꿈
            Log.d("이미지", "성공")
        } else {
            Log.d("이미지", "실패")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        database = Firebase.database.reference

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
        val email = findViewById<EditText>(R.id.et_registration_id).text
        val password = findViewById<EditText>(R.id.et_registration_password).text

        val name = findViewById<EditText>(R.id.et_registration_name).text
        val button = findViewById<Button>(R.id.btn_registration)
        val dclass = findViewById<EditText>(R.id.et_registration_dogclass).text
        val dage = findViewById<EditText>(R.id.et_registration_dogage).text
        val dweight = findViewById<EditText>(R.id.et_registration_dogweight).text
        val profile = findViewById<ImageView>(R.id.registration_iv)
        var profileCheck = false

        profile.setOnClickListener {
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
            profileCheck = true
        }

        val intent = Intent(this, LogInActivity2::class.java)

        button.setOnClickListener {
            // ... 이전 코드 생략 ...

            if (email.isEmpty() && password.isEmpty() && name.isEmpty() && dclass.isEmpty() && dweight.isEmpty() && dage.isEmpty() && profileCheck) {
                Toast.makeText(this, "아이디와 비밀번호, 프로필 사진을 제대로 입력해주세요.", Toast.LENGTH_SHORT).show()
                Log.d("Email", "$email, $password,$dclass,$dweight,$dweight")
            } else {
                if (!profileCheck) {
                    Toast.makeText(this, "프로필사진을 등록해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(email.toString(), password.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = Firebase.auth.currentUser
                                val userId = user?.uid
                                val userIdSt = userId.toString()

                                val dog = Dog(
                                    dclass = dclass.toString(),
                                    dage = dage.toString(),
                                    dweight = dweight.toString()
                                )

                                // Friend 객체 생성 및 값을 설정합니다.
                                val friend = Friend(
                                    email = email.toString(),
                                    name = name.toString(),
                                    profileImageUrl = "",  // 프로필 이미지 URL은 이후에 설정됩니다.
                                    uid = userIdSt,
                                    dog = dog
                                )

                                FirebaseStorage.getInstance()
                                    .reference.child("userImages").child("$userIdSt/photo")
                                    .putFile(imageUri!!)
                                    .addOnSuccessListener { taskSnapshot ->
                                        taskSnapshot.storage.downloadUrl
                                            .addOnSuccessListener { uri ->
                                                friend.profileImageUrl =
                                                    uri.toString()  // 프로필 이미지 URL 설정
                                                Log.d("이미지 URL", "${friend.profileImageUrl}")

                                                // Friend 객체를 Firebase Realtime Database에 저장합니다.
                                                database.child("users").child(userId.toString())
                                                    .setValue(friend)
                                                    .addOnSuccessListener {
                                                        Log.d(
                                                            "Firebase",
                                                            "Friend 정보가 성공적으로 저장되었습니다."
                                                        )
                                                    }
                                                    .addOnFailureListener {
                                                        Log.d("Firebase", "Friend 정보 저장 실패: $it")
                                                    }
                                            }
                                    }

                                Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
            public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun reload() {
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}