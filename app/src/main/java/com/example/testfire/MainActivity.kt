package com.example.testfire

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testfire.Fragment.ChatFragment
import com.example.testfire.Fragment.HomeFragment
import com.example.testfire.Fragment.ProfileFragment
import com.example.testfire.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BottomNavigationView에 아이템 선택 리스너 등록
        binding.bottomNav.setOnNavigationItemSelectedListener(BottomNavItemSelectedListener)

        // 초기에 표시할 홈 프래그먼트 생성 및 표시
        val homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_frame, homeFragment)
            .commitNow()
    }

    // BottomNavigationView의 아이템 선택 리스너
    private val BottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.menu_home -> {
                // 홈 아이템 선택 시 홈 프래그먼트 생성 및 표시
                val homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, homeFragment)
                    .commitNow()
            }
            R.id.menu_chat -> {
                // 채팅 아이템 선택 시 채팅 프래그먼트 생성 및 표시
                val chatFragment = ChatFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, chatFragment)
                    .commitNow()
            }
            R.id.menu_profile -> {
                // 프로필 아이템 선택 시 프로필 프래그먼트 생성 및 표시
                val profileFragment = ProfileFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, profileFragment)
                    .commitNow()
            }
            R.id.menu_manbo -> {
                // 만보기 아이템 선택 시 만보기 프래그먼트 생성 및 표시
                val manboFragment = ManboFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, manboFragment)
                    .commitNow()
            }
        }
        true
    }
}
