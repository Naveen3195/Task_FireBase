package com.example.task.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.task.R
import com.example.task.databinding.CardsLayoutBinding
import com.example.task.ui.models.resmodel.WalletDetModel

class WalletAdapter(private val userList: ArrayList<WalletDetModel>) : RecyclerView.Adapter<WalletAdapter.WalletVH>() {

    class WalletVH(val binding: CardsLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletVH {
        val binding = CardsLayoutBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return WalletVH(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: WalletVH, position: Int) {
        with(holder){
            with(userList[position]) {
                binding.cvHeader.text = this.card_name
                binding.desc.text = this.card_desc
                binding.amt.text = this.card_amt

                when(this.card_name) {
                    "Money Cash" -> {
                        Glide.with(holder.itemView.context).load(R.drawable.wallet1).into(binding.img)
                    }
                    "Debit Card" -> {
                        Glide.with(holder.itemView.context).load(R.drawable.debit_card).into(binding.img)
                    }
                    "Bank Account" -> {
                        Glide.with(holder.itemView.context).load(R.drawable.bank).into(binding.img)
                    }
                    "Credit Card" -> {
                        Glide.with(holder.itemView.context).load(R.drawable.debit_card).into(binding.img)
                    }

                    else -> {}
                }
            }
        }
    }
}