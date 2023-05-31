package com.example.testfire

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 스플래시 화면을 위해 레이아웃 설정
        setContentView(R.layout.activity_splash)

        // Handler를 사용하여 지연 실행
        Handler().postDelayed({
            // LogInActivity2로 이동하기 위한 인텐트 생성
            val intent = Intent(this, LogInActivity2 ::class.java)

            // 전환 중에 애니메이션 효과 방지를 위한 플래그 추가
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

            // LogInActivity2 시작
            startActivity(intent)

            // 스플래시 화면 액티비티를 종료하여 다시 돌아가지 않도록 함
            finish()
        }, 2000) // 2초(2000 밀리초) 지연
    }
}
