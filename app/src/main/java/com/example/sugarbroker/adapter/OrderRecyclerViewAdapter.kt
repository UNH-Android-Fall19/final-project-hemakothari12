package com.example.sugarbroker.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.AddOrderActivity
import com.example.sugarbroker.model.Orders
import com.google.firebase.firestore.FirebaseFirestore

class OrderRecyclerViewAdapter(private val orderList: MutableList<Orders>, private val context: Context,
                               private val firestoreDB: FirebaseFirestore): RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order= orderList[position]

        holder.userName.text = order.name
        holder.quantity.text = order.quantity
        holder.millName.text = order.millName

        holder.edit.setOnClickListener { updateOrder(order) }
        holder.delete.setOnClickListener { deleteOrder(order.id!!, position) }
    }

    override fun getItemCount(): Int {
        Log.d("Size", "Size is ${orderList.size}")
        return orderList.size
    }


    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var userName: TextView
        internal var quantity: TextView
        internal var millName: TextView
        internal var edit: ImageView
        internal var delete: ImageView

        init {
            userName = view.findViewById(R.id.user_name_textview)
            quantity = view.findViewById(R.id.quantity_textview)
            millName = view.findViewById(R.id.mill_name_textview)

            edit = view.findViewById(R.id.ivEdit)
            delete = view.findViewById(R.id.ivDelete)
        }
    }

    private fun updateOrder(order: Orders) {
        val intent = Intent(context, AddOrderActivity::class.java)
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

    private fun deleteOrder(id: String, position: Int) {
        firestoreDB.collection("orders")
            .document(id)
            .delete()
            .addOnCompleteListener {
                orderList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, orderList.size)
                Toast.makeText(context, "Order has been deleted!", Toast.LENGTH_SHORT).show()
            }
    }

}

