package com.example.contohtugasakhir

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormatSymbols
import java.text.NumberFormat
import java.time.Month
import java.time.format.TextStyle
import java.util.*

class TransactionAdapter(private var transactions: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    class TransactionHolder(view: View): RecyclerView.ViewHolder(view){
        val label : TextView = view.findViewById(R.id.label)
        val amount : TextView = view.findViewById(R.id.amount)
        val icon : ImageView = view.findViewById(R.id.icon_list)
        val date : TextView = view.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.amount.context

        if(transaction.amount >= 0){
            holder.amount.text = "+${rupiahFormats(transaction.amount)}"
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.money_out))
            holder.amount.text = "${rupiahFormats(transaction.amount)}"
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        holder.label.text = transaction.label

        holder.date.text = transaction.date

        holder.itemView.setOnClickListener{
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("transaction", transaction)
            context.startActivity((intent))
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun setData(transactions : List<Transaction>){
        this.transactions = transactions
        notifyDataSetChanged()
    }

    private fun rupiahFormats(number : Long) : String{
        val localeId = Locale("id", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeId)
        var rupiahFormats = numberFormat.format((number))
        var split = rupiahFormats.split(',')
        var length = split[0].length
        if(number >= 0){
            return split[0].substring(0,2)+"."+split[0].substring(2,length)
        } else {
            return split[0].substring(0,3)+"."+split[0].substring(3,length)
        }
    }
}