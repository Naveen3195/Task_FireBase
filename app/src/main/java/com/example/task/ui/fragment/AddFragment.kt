package com.example.task.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.task.R
import com.example.task.databinding.FragmentAddBinding
import com.example.task.ui.activity.ScanActivity
import com.example.task.ui.adapter.CardAdapter
import com.example.task.ui.fragment.HomeFragment.Companion.updatedWalAmt
import com.example.task.ui.models.reqmodel.CreditCardModel
import com.example.task.ui.models.reqmodel.EventModel
import com.example.task.ui.models.resmodel.WalletModel
import com.example.task.ui.models.viewmodel.AuthViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private val authVM: AuthViewModel by viewModels()
    private val bus: EventBus = EventBus.getDefault()
    private var userKey = ""
    private lateinit var dbRef: DatabaseReference
    private lateinit var userDbRef: DatabaseReference
    private lateinit var cuserDbRef: DatabaseReference
    private lateinit var cdCardRef: DatabaseReference
    private var userId = ""
    private var isFirst = false
    private var isNew = false
    private val map: HashMap<String, Any> = HashMap()
    private lateinit var popWindow: PopupWindow
    private lateinit var viewInternet: View
    private lateinit var month: AppCompatEditText
    private lateinit var yr: AppCompatEditText
    private lateinit var name: AppCompatEditText
    private lateinit var number: AppCompatEditText
    private lateinit var cvv: AppCompatEditText
    private var cardList: ArrayList<CreditCardModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        //popup window
        val inflateInternet =
            requireActivity().getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewInternet = inflateInternet.inflate(R.layout.add_new_card_layout, null)
        popWindow = PopupWindow(
            viewInternet,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        init()
        return binding.root
    }

    private fun init() {
        userId = authVM.sharHelp.getFromUser("userId")
        dbRef = FirebaseDatabase.getInstance().getReference("Wallet_Details").child(userId)
            .child("wallet")
        cdCardRef = FirebaseDatabase.getInstance().getReference("Wallet_Details").child(userId)
            .child("Card_Details")
        onClick()
        if (cdCardRef != null){
          retrieveData()
        }
        println("updateWalBlc:: $updatedWalAmt")
    }

    private fun retrieveData() {
        cdCardRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    cardList.clear()
                    for (cards in snapshot.children){
                        val cardData = cards.getValue(CreditCardModel::class.java)
                        cardList.add(cardData!!)
                    }
                    //TODO Card Details Adapter
                    val cardAdapter = CardAdapter(cardList)
                    binding.cardRV.adapter = cardAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast(error.message.toString())
            }

        })
    }

    private fun onClick() {
        binding.addCardTxt.setOnClickListener {
            showDialog()
        }

        binding.barCodeBtn.setOnClickListener {
            authVM.navHelper.setIntent(ScanActivity::class.java, 0, requireActivity())
            isFirst = true
            isNew = true
        }

        binding.continueBtn.setOnClickListener {
            if (!userKey.isNullOrEmpty()) {
                if (updatedWalAmt >= binding.amt.text.toString().toInt()) {
                    userDbRef =
                        FirebaseDatabase.getInstance().getReference("Wallet_Details") // to pay user
                            .child(userKey).child("wallet_detail")

                    userDbRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                if (isFirst) {
                                    isFirst = false
                                    val dbtUser = snapshot.getValue(WalletModel::class.java)
                                    val sendAmt =
                                        dbtUser!!.wal_blc + binding.amt.text.toString().toInt()
                                    map["wal_blc"] = sendAmt
                                    userDbRef.updateChildren(map).addOnCompleteListener {
                                        showToast("Amount Transferred")
                                        binding.barCodeBtn.visibility = View.VISIBLE
                                        binding.barCodeTxt.visibility = View.GONE
                                    }.addOnFailureListener {
                                        showToast(it.message.toString())
                                    }
                                }
                            } else {
                                if (isNew) {
                                    isNew = false
                                    isFirst = false
                                    val sendAmt = binding.amt.text.toString().toInt()
                                    val walletModel = WalletModel(sendAmt)
                                    userDbRef.setValue(walletModel).addOnCompleteListener {
                                        showToast("Amount Transferred")
                                    }.addOnFailureListener {
                                        showToast(it.message.toString())
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })

                    cuserDbRef =
                        FirebaseDatabase.getInstance().getReference("Wallet_Details")
                            .child(userId) // current user
                            .child("wallet_detail")

                    Handler(Looper.getMainLooper()).postDelayed({
                        val dbtAmt = updatedWalAmt - binding.amt.text.toString().toInt()
                        val walModel = WalletModel(dbtAmt)
                        cuserDbRef.setValue(walModel).addOnCompleteListener {
                            binding.amt.setText("")
                        }
                            .addOnFailureListener {
                                showToast("Debit Failed")
                            }
                    }, 2000)

                } else {
                    showToast("In Sufficient Balance")
                }
            }
        }
    }

    private fun showDialog() {
        popWindow.isOutsideTouchable = false
        month = viewInternet.findViewById(R.id.expMon)
        yr = viewInternet.findViewById(R.id.expYr)
        name = viewInternet.findViewById(R.id.cdHolder)
        number = viewInternet.findViewById(R.id.cdNum)
        cvv = viewInternet.findViewById(R.id.cdCvvNo)
        val cntBtn = viewInternet.findViewById<AppCompatButton>(R.id.conBtn)

        cntBtn.setOnClickListener {
            val date = Date()
            val yrFormat = SimpleDateFormat("YY")
            val crYr = yrFormat.format(date)
            val monFormat = SimpleDateFormat("MM")
            val crMon = monFormat.format(date)
            val validate = authVM.validate


            val cMon = month.text.toString()
            val cYr = yr.text.toString()
            val cdNumb = number.text.toString()
            val cdName = name.text.toString()
            val cCvv = cvv.text.toString()

            // New credit / debit card validation
            if (cMon.length < 2 || cMon.toInt() > 11 || cMon.toInt() < crMon.toString().toInt()) {
                showToast("Month Required")
            } else if (cYr.length < 2 || cYr.toInt() < crYr.toString().toInt()) {
                showToast("Year Required")
            } else if (validate.isEmptyString(cdNumb) || cdNumb.length < 16) {
                showToast("Enter 16 digit card number")
            } else if (validate.isEmptyString(cdName)) {
                showToast("Name Required")
            } else if (validate.isEmptyString(cCvv) || cCvv.length < 3) {
                showToast("CVV Number Required")
            } else {
                val cdCard = cdCardRef.push().key!!
                val cdCardModel = CreditCardModel(cMon, cYr, cdNumb, cdName, cCvv)
                cdCardRef.child(cdCard).setValue(cdCardModel).addOnCompleteListener {
                    showToast("Card Details Added Successfully")
                    month.setText("")
                    yr.setText("")
                    name.setText("")
                    number.setText("")
                    cvv.setText("")

                }.addOnFailureListener {
                    showToast(it.message.toString())
                }
                popWindow.dismiss()
            }
        }

        popWindow.isFocusable = true
        popWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        popWindow.showAtLocation(viewInternet, Gravity.NO_GRAVITY, 0, 0)
    }

    override fun onStart() {
        super.onStart()
        bus.register(this)
    }

    override fun onStop() {
        super.onStop()
        bus.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    @Throws(ClassNotFoundException::class)
    fun onEvent(event: EventModel) {
        if (event != null) {
            binding.barCodeBtn.visibility = View.GONE
            binding.barCodeTxt.visibility = View.VISIBLE
            binding.barCodeTxt.text = event.id
            userKey = event.id
            bus.removeStickyEvent(event)
        }
    }

    private fun showToast(msg: String) {
        authVM.navHelper.toast(requireContext(), msg)
    }
}
