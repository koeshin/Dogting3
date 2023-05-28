package com.example.testfire

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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



class ProfileFragment : Fragment() {
    companion object {
        private var imageUri: Uri? = null
        private val fireStorage = FirebaseStorage.getInstance().reference
        private val fireDatabase = FirebaseDatabase.getInstance().reference
        private val user = Firebase.auth.currentUser
        private val uid = user?.uid.toString()

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    private lateinit var profileImageView: ImageView

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
            }
        })

        //프로필사진 바꾸기
        profileImageView.setOnClickListener {
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }

        button?.setOnClickListener {
            if (name?.text!!.isNotEmpty()) {
                fireDatabase.child("users/$uid/name").setValue(name.text.toString())
                name.clearFocus()
                Toast.makeText(requireContext(), "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

}