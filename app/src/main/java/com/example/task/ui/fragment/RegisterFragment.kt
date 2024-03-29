package com.example.task.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.task.databinding.FragmentRegisterBinding
import com.example.task.ui.models.viewmodel.AuthViewModel
import com.example.task.ui.activity.HomeActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var fbAuth: FirebaseAuth
    private val authVM: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        fbAuth = Firebase.auth
        onClick()
    }

    private fun onClick() {
        // Click to move Signup
        binding.loginBtn.setOnClickListener {
            signUp()
        }

        //back btn
        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
    }

    private fun signUp() {
        val validate = authVM.validate
        val userName = binding.UserName.text.toString()
        val userPwd = binding.userPwd.text
        val conUserPwd = binding.confirmPwd.text

        if (validate.isEmptyString(userName)) {
            showToast("Enter valid Gmail-ID")
        } else if (validate.isEmptyString(userPwd.toString()) || userPwd!!.length < 6) {
            showToast("Password Required could not empty or less than 6")
        } else if (validate.isEmptyString(conUserPwd.toString()) || (userPwd.toString() != conUserPwd.toString())) {
            showToast("Confirm Password could not empty or not mismatch")
        } else {
            fbAuth.createUserWithEmailAndPassword(userName, conUserPwd.toString())
                .addOnCompleteListener(requireActivity()) {
                    try{
                        if (it.isSuccessful) {
                            showToast("Account Created Successfully")
                            authVM.navHelper.setIntent(HomeActivity::class.java, 1, requireActivity())
                            authVM.sharHelp.putInUser("userId", it.result.user!!.uid)
                        } else {
                            showToast(it.exception?.message.toString())
                        }
                    } catch (e: Exception) {
                        showToast(e.message.toString())
                    }
                }
        }
    }

    private fun showToast(msg: String) {
        authVM.navHelper.toast(requireContext(), msg)
    }
}