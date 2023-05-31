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
import com.example.testfire.model.Dog
import com.example.testfire.model.Friend
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

    // 이미지 등록을 위한 ActivityResultLauncher
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data // 선택한 이미지의 원본 경로
            registration_iv?.setImageURI(imageUri) // 이미지 뷰에 이미지 설정
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

        // EditText와 Button, ImageView 등 필요한 뷰 요소들 초기화
        val email = findViewById<EditText>(R.id.et_registration_id).text
        val password = findViewById<EditText>(R.id.et_registration_password).text
        val name = findViewById<EditText>(R.id.et_registration_name).text
        val button = findViewById<Button>(R.id.btn_registration)
        val dclass = findViewById<EditText>(R.id.et_registration_dogclass).text
        val dsex =findViewById<EditText>(R.id.et_registration_dogsex).text
        val dage = findViewById<EditText>(R.id.et_registration_dogage).text
        val dcharacter = findViewById<EditText>(R.id.et_registration_dogcharacter).text
        val dong=findViewById<EditText>(R.id.et_registration_location).text
        val profile = findViewById<ImageView>(R.id.registration_iv)
        var profileCheck = false

        // 프로필 이미지를 선택하기 위한 클릭 리스너 설정
        profile.setOnClickListener {
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
            profileCheck = true
        }

        val intent = Intent(this, LogInActivity2::class.java)

        button.setOnClickListener {
            if (email.isEmpty() && password.isEmpty() && name.isEmpty() && dclass.isEmpty() && dsex.isEmpty() && dage.isEmpty() && dcharacter.isEmpty() && profileCheck) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                Log.d("Email", "$email, $password,$dclass")
            } else {
                if (!profileCheck) {
                    Toast.makeText(this, "프로필 사진을 등록해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    // Firebase에 사용자 계정 생성
                    auth.createUserWithEmailAndPassword(email.toString(), password.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = Firebase.auth.currentUser
                                val userId = user?.uid
                                val userIdSt = userId.toString()

                                val dog = Dog(
                                    dclass = dclass.toString(),
                                    dage = dage.toString(),
                                    dsex = dsex.toString(),
                                    dcharacter=dcharacter.toString()
                                )

                                // Friend 객체 생성 및 값 설정
                                val friend = Friend(
                                    email = email.toString(),
                                    name = name.toString(),
                                    profileImageUrl = "",  // 프로필 이미지 URL은 이후에 설정됩니다.
                                    uid = userIdSt,
                                    location=dong.toString(),
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

                                                // Friend 객체를 Firebase Realtime Database에 저장
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
        // 사용자가 로그인한 상태인지 확인하고 UI를 업데이트합니다.
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
