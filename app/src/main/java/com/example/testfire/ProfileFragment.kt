package com.example.testfire

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text


class ProfileFragment : Fragment() {
    companion object {
        private var imageUri: Uri? = null
        private val fireStorage = FirebaseStorage.getInstance().reference
        private val fireDatabase = FirebaseDatabase.getInstance().reference
        private val user = Firebase.auth.currentUser //회원정보
        private val uid = user?.uid.toString()

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    private lateinit var profileImageView: ImageView
    private lateinit var dogClassEditText: EditText
    private lateinit var dogAgeEditText: EditText
    private lateinit var dogWeightEditText: EditText
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            imageUri = result.data?.data //이미지 경로 원본
            profileImageView.setImageURI(imageUri) //이미지 뷰를 바꿈

            //기존 사진을 삭제 후 새로운 사진을 등록
            fireStorage.child("userImages/$uid/photo").delete().addOnSuccessListener {
                fireStorage.child("userImages/$uid/photo").putFile(imageUri!!).addOnSuccessListener {
                    fireStorage.child("userImages/$uid/photo").downloadUrl.addOnSuccessListener { uri ->
                        val photoUri: Uri = uri
                        println("$photoUri")
                        fireDatabase.child("users/$uid/profileImageUrl").setValue(photoUri.toString())
                        Toast.makeText(requireContext(), "프로필사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Log.d("이미지", "성공")
        } else {
            Log.d("이미지", "실패")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profileImageView = view.findViewById(R.id.profile_imageview) // 프로필 이미지뷰 찾기

        val email = view.findViewById<TextView>(R.id.profile_textview_email)
        val name = view.findViewById<TextView>(R.id.profile_textview_name)
        val button = view.findViewById<Button>(R.id.profile_button)
        dogClassEditText = view.findViewById(R.id.dogclass)
        dogAgeEditText = view.findViewById(R.id.dogage)
        dogWeightEditText = view.findViewById(R.id.dogweight)
        val profile_location=view.findViewById<TextView>(R.id.profil_location)

        //프로필 구현
        fireDatabase.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue<Friend>()
                println(userProfile)
                Glide.with(requireContext())
                    .load(userProfile?.profileImageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(profileImageView)
                email?.text = userProfile?.email
                name?.text = userProfile?.name
                dogClassEditText.setText(userProfile?.dog?.dclass) // 견종 설정
                dogAgeEditText.setText(userProfile?.dog?.dage) // 나이 설정
                dogWeightEditText.setText(userProfile?.dog?.dweight) // 몸무게 설정
                profile_location?.text=userProfile?.location   // 위치 설정
            }
        })

        //프로필사진 바꾸기
        profileImageView.setOnClickListener {
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }

        button?.setOnClickListener { // 동 바꾸기
            if (profile_location?.text!!.isNotEmpty()) {
                fireDatabase.child("users/$uid/location").setValue(profile_location.text.toString())
                profile_location.clearFocus()
                Toast.makeText(requireContext(), "위치가 변경되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

}