package com.example.task.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.task.R
import com.example.task.databinding.FragmentProfileBinding
import com.example.task.ui.adapter.LoginActivity
import com.example.task.ui.models.viewmodel.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val authVM: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        binding.logout.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val btmView = layoutInflater.inflate(R.layout.logout, null)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancelBtn = btmView.findViewById<AppCompatButton>(R.id.logCancelBtn)
        val confirmBtn = btmView.findViewById<AppCompatButton>(R.id.logOutBtn)
        val disc = btmView.findViewById<AppCompatTextView>(R.id.logTxtView2)
        disc.text = requireContext().getString(R.string.logout_content)

        confirmBtn.setOnClickListener {
            authVM.sharHelp.clearUser()
            authVM.sharHelp.clearCache()
            authVM.navHelper.setIntent(LoginActivity::class.java, 1, requireActivity())
            requireActivity().finish()
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(btmView)
        dialog.show()
    }
}