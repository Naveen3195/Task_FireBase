package com.example.task.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.example.task.databinding.ActivitySplashBinding
import com.example.task.ui.adapter.LoginActivity
import com.example.task.ui.models.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val authVM: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            val userId = authVM.sharHelp.getFromUser("userId")
            println("userId:: $userId")
            if (userId.isNullOrBlank()){
                authVM.navHelper.setIntent(LoginActivity::class.java, 1, this)
            } else {
                authVM.navHelper.setIntent(HomeActivity::class.java, 1, this)
            }
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
    }
}