package com.example.sugarbroker.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.order.DetailOrderActivity
import com.example.sugarbroker.model.Orders
import com.google.firebase.firestore.FirebaseFirestore

class UserOrdersRecyclerViewAdapter(private val orderList: MutableList<Orders>, private val context: Context,
                                    private val firestoreDB: FirebaseFirestore): RecyclerView.Adapter<UserOrdersRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.user_item_order, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order= orderList[position]

        holder.millName.text = order.millName
        holder.quantity.text = order.quantity

        holder.itemView.setOnClickListener { detailOrder(order) }
    }

    override fun getItemCount(): Int {
        Log.d("Size", "Size is ${orderList.size}")
        return orderList.size
    }


    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var millName: TextView
        internal var quantity: TextView

        init {
            millName = view.findViewById(R.id.mill_name_textview)
            quantity = view.findViewById(R.id.quantity_textview)
        }
    }

    private fun detailOrder(order: Orders) {
        val intent = Intent(context, DetailOrderActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateOrderId", order.id)
        intent.putExtra("UpdateOrderMillName", order.millName)
        intent.putExtra("UpdateOrderPrice", order.price)
        intent.putExtra("UpdateOrderQuantity", order.quantity)
        intent.putExtra("UpdateOrderQuantityPrice", order.quantityPrice)
        intent.putExtra("UpdateOrderGST", order.GST)
        intent.putExtra("UpdateOrderTotalPrice", order.totalPrice)
        intent.putExtra("UpdateOrderUsername", order.name)
        intent.putExtra("UpdateOrderUserAddress", order.address)
        intent.putExtra("UpdateOrderStatus", order.status)
        context.startActivity(intent)
    }

}

