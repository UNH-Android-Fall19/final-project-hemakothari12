package com.example.sugarbroker.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.adapter.UserOrdersRecyclerViewAdapter
import com.example.sugarbroker.model.Orders
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UserOrdersFragment : Fragment() {

    private val TAG = "UserOrdersFragment"

    private var orderAdapter: UserOrdersRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null

    internal var LoggedInUserEmail: Any? = null
    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firestoreDB = FirebaseFirestore.getInstance()

        root = inflater.inflate(R.layout.fragment_user_orders, container, false)

        val bundle = activity!!.intent.extras
        if (bundle != null) {
            LoggedInUserEmail = bundle.getString("LoggedInUserEmail")
        }

        loadOrderList()

        firestoreListener = firestoreDB!!.collection("orders").whereEqualTo("email", LoggedInUserEmail)
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                val orderList = mutableListOf<Orders>()

                for (doc in documentSnapshots!!) {
                    val order = doc.toObject(Orders::class.java)
                    order.id = doc.id
                    orderList.add(order)
                }

                orderAdapter = UserOrdersRecyclerViewAdapter(orderList, context!!, firestoreDB!!)
                val orderListRV = root!!.findViewById<View>(R.id.user_order_list) as RecyclerView
                orderListRV.adapter = orderAdapter
                orderListRV.addItemDecoration(
                    DividerItemDecoration(context,
                        DividerItemDecoration.VERTICAL)
                )
            })

        return root
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    private fun loadOrderList() {
        firestoreDB!!.collection("orders").whereEqualTo("email",LoggedInUserEmail)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val orderList = mutableListOf<Orders>()

                    for (doc in task.result!!) {
                        val order = doc.toObject<Orders>(Orders::class.java)
                        order.id = doc.id
                        orderList.add(order)
                    }

                    orderAdapter = UserOrdersRecyclerViewAdapter(orderList, context!!, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(context!!)
                    val orderListRV = root!!.findViewById<View>(R.id.user_order_list) as RecyclerView
                    orderListRV.layoutManager = mLayoutManager
                    orderListRV.itemAnimator = DefaultItemAnimator()
                    orderListRV.adapter = orderAdapter
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }

}