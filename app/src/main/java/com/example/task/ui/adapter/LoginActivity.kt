package com.example.task.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.task.R
import com.example.task.databinding.ActivityLoginBinding
import com.example.task.ui.activity.HomeActivity
import com.example.task.ui.fragment.RegisterFragment
import com.example.task.ui.models.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authVM: AuthViewModel by viewModels()
    private lateinit var fbAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fbAuth = FirebaseAuth.getInstance()
        onClick()
    }

    private fun onClick() {
        //Login Button
        binding.loginBtn.setOnClickListener {
            login()
        }

        //SignupBtn Click Listener
        binding.signUp.setOnClickListener {
            authVM.navHelper.moveFrag(
                android.R.id.content, RegisterFragment(), true,
                supportFragmentManager
            )
        }

        // visible hide
        binding.gone.setOnClickListener {
            binding.userPwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.gone.visibility = View.GONE
            binding.visible.visibility = View.VISIBLE
        }

        binding.visible.setOnClickListener {
            binding.userPwd.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.gone.visibility = View.VISIBLE
            binding.visible.visibility = View.GONE
        }
    }

    private fun login() {
        fbAuth.signInWithEmailAndPassword(binding.UserName.text.toString(), binding.userPwd.text.toString()).addOnCompleteListener {
            if (it.isSuccessful) {
                authVM.navHelper.setIntent(HomeActivity::class.java, 1, this)
                authVM.sharHelp.putInUser("userId", it.result.user!!.uid)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
    }

    private fun showToast(msg: String) {
        authVM.navHelper.toast(this, msg)
    }
}