package com.example.task.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.task.databinding.FragmentLoginBinding
import com.example.task.ui.models.viewmodel.AuthViewModel
import com.example.task.ui.activity.HomeActivity
import com.example.task.ui.models.reqmodel.EventModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val authVM: AuthViewModel by viewModels()
    private lateinit var fbAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
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
                requireActivity().supportFragmentManager
            )
        }
    }

    private fun login() {
        fbAuth.signInWithEmailAndPassword(binding.UserName.text.toString(), binding.userPwd.text.toString()).addOnCompleteListener {
            if (it.isSuccessful) {
                authVM.navHelper.setIntent(HomeActivity::class.java, 1, requireActivity())
                authVM.sharHelp.putInUser("userId", it.result.user!!.uid)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
    }

    private fun showToast(msg: String) {
        authVM.navHelper.toast(requireContext(), msg)
    }
}