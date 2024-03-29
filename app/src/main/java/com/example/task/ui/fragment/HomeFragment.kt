package com.example.task.ui.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.task.R
import com.example.task.databinding.FragmentHomeBinding
import com.example.task.ui.adapter.WalletAdapter
import com.example.task.ui.models.resmodel.WalletDetModel
import com.example.task.ui.models.resmodel.WalletModel
import com.example.task.ui.models.viewmodel.AuthViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val authVM: AuthViewModel by viewModels()
    private var spinnerData: ArrayList<String> = ArrayList()
    private lateinit var dbRef: DatabaseReference
    private var spinnerText = ""
    private var userList: ArrayList<WalletDetModel> = ArrayList()
    private var userId = ""
    private lateinit var dbWal: DatabaseReference

    companion object {
        var updatedWalAmt = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        spinnerData.add("Choose Category")
        spinnerData.add("Money Cash")
        spinnerData.add("Debit Card")
        spinnerData.add("Bank Account")
        spinnerData.add("Credit Card")

        userId = authVM.sharHelp.getFromUser("userId")
        // db instance
        dbRef = FirebaseDatabase.getInstance().getReference("Wallet_Details").child(userId)
            .child("card_detail")
        dbWal = FirebaseDatabase.getInstance().getReference("Wallet_Details").child(userId)
            .child("wallet_detail")

        if (dbRef != null && dbWal != null) {
            retrieveData()
        }

        onClick()
        date()
    }

    private fun date() {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        val current = formatter.format(date)
        binding.date.text = current
    }

    private fun retrieveData() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        val userData = userSnap.getValue(WalletDetModel::class.java)
                        userList.add(userData!!)
                    }
                    // TODO Wallet List Adapter
                    val walletAdapter = WalletAdapter(userList)
                    binding.walDetRV.adapter = walletAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        dbWal.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    updatedWalAmt = 0
                    val walBlc = snapshot.getValue(WalletModel::class.java)
                    updatedWalAmt = walBlc!!.wal_blc
                    binding.blc.text = walBlc.wal_blc.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun onClick() {
        //Add Card Btn
        binding.walletBtn.setOnClickListener {
            showDialog(requireContext(), spinnerData)
        }
        // QR Code Show
        binding.settingBtn.setOnClickListener {
            val data = authVM.sharHelp.getFromUser("userId")
            if (data.isEmpty()) {
                showToast("enter some data")
            } else {
                val writer = QRCodeWriter()
                try {
                    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 400, 400)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bmp = Bitmap.createBitmap(
                        width,
                        height,
                        Bitmap.Config.RGB_565
                    )
                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    showQRCode(requireContext(), bmp)
//                    qrIV.setImageBitmap(bmp)
                } catch (e: Exception) {
                    println("exceptionnn" + e.message)
                }
            }
        }
    }

    private fun showQRCode(context: Context, bmp: Bitmap) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.otp_popup)

        val imgView = dialog.findViewById<ImageView>(R.id.header)
        imgView.setImageBitmap(bmp)
        dialog.show()
    }

    private fun showDialog(context: Context, spinnerData: ArrayList<String>) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.add_card_layout)

        val spinner = dialog.findViewById<AppCompatSpinner>(R.id.spinner)
        val desc = dialog.findViewById<AppCompatEditText>(R.id.desc)
        val amt = dialog.findViewById<AppCompatEditText>(R.id.amt)
        val addCrdBtn = dialog.findViewById<AppCompatButton>(R.id.addCardBtn)

        //spinner data
        val spinAdapter = ArrayAdapter(
            context,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, spinnerData
        )
        spinner.adapter = spinAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinnerText = spinnerData[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        // add card details into db
        addCrdBtn.setOnClickListener {
            val walAmt = amt.text.toString()
            val walDesc = desc.text.toString()

            val validate = authVM.validate
            if (spinnerText == "Choose Category") {
                showToast("Choose Category")
            } else if (validate.isEmptyString(walDesc)) {
                showToast("Description required")
            } else if (validate.isEmptyString(walAmt)) {
                showToast("Amount required")
            } else {
//                dbRef.child("user").child(data)
                val user = dbRef.push().key!!
                val walDetails =
                    WalletDetModel(walAmt, walDesc, spinnerText) // wallet details updation
                dbRef.child(user).setValue(walDetails).addOnCompleteListener {
                    showToast("Card Added Successfully")
                    dialog.dismiss()
                }.addOnFailureListener {
                    showToast(it.message.toString())
                    dialog.dismiss()
                }

                // Wallet total add into firebase database
                updatedWalAmt += walAmt.toInt()
                val walModel = WalletModel(updatedWalAmt)
                dbWal.setValue(walModel).addOnCompleteListener {
                }.addOnFailureListener {
                    showToast(it.message.toString())
                }
            }
        }
        dialog.show()
    }

    private fun showToast(msg: String) {
        authVM.navHelper.toast(requireContext(), msg)
    }
}