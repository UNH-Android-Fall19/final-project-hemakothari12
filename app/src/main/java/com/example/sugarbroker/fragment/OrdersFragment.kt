package com.example.sugarbroker.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.account.LoginActivity
import com.example.sugarbroker.activity.callback.SwipeToDeleteCallback
import com.example.sugarbroker.activity.interfaces.ListClick
import com.example.sugarbroker.activity.order.AddOrderActivity
import com.example.sugarbroker.adapter.OrderRecyclerViewAdapter
import com.example.sugarbroker.model.Orders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_orders.view.*

/**
 * [Orders Fragment] subclass.
 */
class OrdersFragment : Fragment(), SearchView.OnQueryTextListener, ListClick {

    private val TAG = "OrdersFragment"

    private var orderAdapter: OrderRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    lateinit var editsearch: SearchView
    private var mainToolbar: Toolbar? = null
    private var searchIcon: ImageView? = null
    private var backIcon: ImageView? = null
    private var heading: TextView? = null
    private var logout: ImageView? = null
    private var orderAdd: FloatingActionButton? = null

    private var root: View? = null

    var orderList = mutableListOf<Orders>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestoreDB = FirebaseFirestore.getInstance()

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_orders, container, false)

        mainToolbar = root!!.findViewById<View>(R.id.toolbar) as Toolbar
        mainToolbar!!.visibility = View.VISIBLE
        heading = root!!.findViewById<View>(R.id.heading_textview) as TextView
        heading!!.visibility = View.VISIBLE
        editsearch = root!!.findViewById(R.id.searchorder_sv) as SearchView
        editsearch.visibility = View.GONE
        backIcon = root!!.findViewById(R.id.back_button) as ImageView
        backIcon!!.visibility = View.GONE
        logout = root!!.findViewById(R.id.logout) as ImageView
        logout!!.visibility = View.VISIBLE
        orderAdd = root!!.findViewById<View>(R.id.ordersAdd) as FloatingActionButton

        loadOrderList()

        firestoreListener = firestoreDB!!.collection("orders")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                orderList = mutableListOf<Orders>()

                for (doc in documentSnapshots!!) {
                    val order = doc.toObject(Orders::class.java)
                    order.id = doc.id
                    orderList.add(order)
                }

                orderAdapter = OrderRecyclerViewAdapter(orderList, context!!, firestoreDB!!)
                val orderListRV = root!!.findViewById<View>(R.id.order_list) as RecyclerView
                orderListRV.adapter = orderAdapter
            })

        editsearch!!.setOnQueryTextListener(this)

        backIcon!!.setOnClickListener {
            editsearch.setQuery("",false)
            editsearch.clearFocus()
            backIcon!!.visibility = View.GONE
            editsearch!!.visibility = View.GONE
            heading!!.visibility = View.VISIBLE
            searchIcon!!.visibility = View.VISIBLE
            logout!!.visibility = View.VISIBLE
        }

        searchIcon = root!!.findViewById<View>(R.id.search_icon) as ImageView
        searchIcon!!.setOnClickListener {
            backIcon!!.visibility = View.VISIBLE
            editsearch!!.visibility = View.VISIBLE
            heading!!.visibility = View.GONE
            searchIcon!!.visibility = View.GONE
            logout!!.visibility = View.GONE
            editsearch!!.requestFocus()
        }

        logout!!.setOnClickListener {
            performLogout()
        }

        orderAdd!!.setOnClickListener {
            val intent = Intent(context, AddOrderActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onQueryTextChange(newText: String): Boolean {
        orderAdapter!!.filter(newText)
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }


    private fun loadOrderList() {
        firestoreDB!!.collection("orders")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    orderList = mutableListOf<Orders>()

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

                    val swipeHandler = object : SwipeToDeleteCallback(context!!) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                            deleteRow(viewHolder.adapterPosition)
                        }
                    }

                    val itemTouchHelper = ItemTouchHelper(swipeHandler)
                    itemTouchHelper.attachToRecyclerView(root?.order_list!!)
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(activity!!.applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun deleteRow(position: Int) {
        Log.d("Position is delete row: ", "position is ${position}")
        val users = orderList[position]
        val uid = users.id
        firestoreDB!!.collection("orders").document(uid!!).delete()
            .addOnCompleteListener {
                Toast.makeText(context, "Order has been deleted!", Toast.LENGTH_SHORT).show()
            }
    }
}
