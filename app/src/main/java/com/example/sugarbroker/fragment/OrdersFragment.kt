package com.example.sugarbroker.fragment


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.adapter.OrderRecyclerViewAdapter
import com.example.sugarbroker.model.Orders
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * [Orders Fragment] subclass.
 */
class OrdersFragment : Fragment() {

    private val TAG = "OrdersFragment"

    private var orderAdapter: OrderRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null

    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestoreDB = FirebaseFirestore.getInstance()

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_orders, container, false)

        setHasOptionsMenu(true)

        loadOrderList()

        firestoreListener = firestoreDB!!.collection("orders")
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

                orderAdapter = OrderRecyclerViewAdapter(orderList, context!!, firestoreDB!!)
                val orderListRV = root!!.findViewById<View>(R.id.order_list) as RecyclerView
                orderListRV.adapter = orderAdapter
            })

        return root
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun loadOrderList() {
        firestoreDB!!.collection("orders")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val orderList = mutableListOf<Orders>()

                    for (doc in task.result!!) {
                        val order = doc.toObject<Orders>(Orders::class.java)
                        order.id = doc.id
                        orderList.add(order)
                    }

                    orderAdapter = OrderRecyclerViewAdapter(orderList, context!!, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(context!!)
                    val orderListRV = root!!.findViewById<View>(R.id.order_list) as RecyclerView
                    orderListRV.layoutManager = mLayoutManager
                    orderListRV.itemAnimator = DefaultItemAnimator()
                    orderListRV.adapter = orderAdapter
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }
}
