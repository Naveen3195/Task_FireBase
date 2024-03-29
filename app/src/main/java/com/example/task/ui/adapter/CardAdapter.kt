package com.example.task.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.task.R
import com.example.task.databinding.CardListLayoutBinding
import com.example.task.ui.models.reqmodel.CreditCardModel

class CardAdapter(private val cardList: ArrayList<CreditCardModel>) : RecyclerView.Adapter<CardAdapter.CardVH>() {
    private var rb : RadioButton? = null

    class CardVH(val binding : CardListLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardVH {
        val binding = CardListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardVH(binding)
    }

    override fun getItemCount(): Int {
        return  cardList.size
    }

    override fun onBindViewHolder(holder: CardVH, position: Int) {
        with(holder){
            with(cardList[position]){
                binding.expMon.setText(this.cdMonth)
                binding.expYr.setText(this.cdYr)
                binding.cdNum.setText(this.cdNum)
                binding.cdHolder.setText(this.cdName)
                binding.cdCvvNo.setText(this.cdCvv)

                binding.cardNoTxt.text = this.cdNum

                binding.visaRB.setOnClickListener {
                    val rbView = binding.visaRB
                    if (rb != null) {
                        binding.cardCV.visibility = View.GONE
                        rb!!.isChecked = false
                    }
                    rb = rbView
                    binding.cardCV.visibility = View.VISIBLE
                }
            }
        }
    }
}