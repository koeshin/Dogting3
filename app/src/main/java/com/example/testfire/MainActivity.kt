package com.example.testfire

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testfire.databinding.ActivityMainBinding


import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.bottomNav.setOnNavigationItemSelectedListener(BottomNavItemSelectedListener)

        val homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_frame, homeFragment)
            .commitNow()
    }

    private val BottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.menu_home -> {
                val homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, homeFragment)
                    .commitNow()
            }
            R.id.menu_chat -> {
                val chatFragment = ChatFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, chatFragment)
                    .commitNow()
            }
            R.id.menu_profile -> {
                val profileFragment = ProfileFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, profileFragment)
                    .commitNow()
            }
        }
        true
    }
}
