package com.example.sugarbroker.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.order.AddOrderActivity
import com.example.sugarbroker.activity.order.DetailOrderActivity
import com.example.sugarbroker.model.Orders
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class OrderRecyclerViewAdapter(private val orderList: MutableList<Orders>, private val context: Context,
                               private val firestoreDB: FirebaseFirestore): RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder>() {

    private val listOrders: ArrayList<Orders>

    init {

        this.listOrders = ArrayList<Orders>()
        this.listOrders.addAll(orderList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order= orderList[position]

        holder.userName.text = order.name
        holder.quantity.text = order.quantity
        holder.millName.text = order.millName
        holder.tvIcon.text = order.name!!.get(0).toUpperCase().toString()

        holder.edit.setOnClickListener { updateOrder(order) }
        holder.itemView.setOnClickListener { detailOrder(order) }
    }

    override fun getItemCount(): Int {
        Log.d("Size", "Size is ${orderList.size}")
        return orderList.size
    }


    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var userName: TextView
        internal var quantity: TextView
        internal var millName: TextView
        internal var edit: Button
        internal var tvIcon: TextView

        init {
            userName = view.findViewById(R.id.user_name_textview)
            quantity = view.findViewById(R.id.quantity_textview)
            millName = view.findViewById(R.id.mill_name_textview)

            edit = view.findViewById(R.id.ivEdit)
            tvIcon = view.findViewById(R.id.tvIcon)
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
        intent.putExtra("UpdateOrderEmail", order.email)
        intent.putExtra("UpdateOrderUsername", order.name)
        intent.putExtra("UpdateOrderUserAddress", order.address)
        intent.putExtra("UpdateOrderStatus", order.status)
        context.startActivity(intent)
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
        intent.putExtra("UpdateOrderEmail", order.email)
        intent.putExtra("UpdateOrderUsername", order.name)
        intent.putExtra("UpdateOrderUserAddress", order.address)
        intent.putExtra("UpdateOrderStatus", order.status)
        context.startActivity(intent)
    }

    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        orderList.clear()
        if (charText.length == 0) {
            orderList.addAll(listOrders)
        } else {
            for (wp in listOrders) {
                if (wp.name!!.toLowerCase(Locale.getDefault()).contains(charText)) {
                    orderList.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

}

