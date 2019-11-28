package com.example.sugarbroker.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.adapter.UserOrdersRecyclerViewAdapter
import com.example.sugarbroker.model.Orders
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class UserOrdersFragment : Fragment(), SearchView.OnQueryTextListener {

    private val TAG = "UserOrdersFragment"

    private var orderAdapter: UserOrdersRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null

    internal var LoggedInUserEmail: Any? = null
    lateinit var editsearch: SearchView
    private var toggle: RadioGroup? = null
    private var open: RadioButton? = null
    private var transit: RadioButton? = null
    private var closed: RadioButton? = null

    private var root: View? = null

    var orderList = mutableListOf<Orders>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firestoreDB = FirebaseFirestore.getInstance()

        root = inflater.inflate(R.layout.fragment_user_orders, container, false)

        MobileAds.initialize(context,getString(R.string.app_unit_id))
        val mAdView = root!!.findViewById<View>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        editsearch = root!!.findViewById(R.id.searchOrderUser_sv) as SearchView

        toggle = root!!.findViewById<View>(R.id.toggle) as RadioGroup
        open = root!!.findViewById<View>(R.id.open) as RadioButton
        transit = root!!.findViewById<View>(R.id.transit) as RadioButton
        closed = root!!.findViewById<View>(R.id.closed) as RadioButton
        val bundle = activity!!.intent.extras
        if (bundle != null) {
            LoggedInUserEmail = bundle.getString("LoggedInUserEmail")
        }
        val userRef = firestoreDB!!.collection("orders").whereEqualTo("email", LoggedInUserEmail)

        checkRadionButton()

        toggle!!.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = root!!.findViewById(checkedId)
            if (radio.text.equals("Open")) {
                radio.setTextColor(resources.getColor(R.color.transparent))
                transit!!.setTextColor(resources.getColor((R.color.colorPrimary)))
                closed!!.setTextColor(resources.getColor((R.color.colorPrimary)))
                loadOrderList("Open")
            } else if (radio.text.equals("Transit")) {
                radio.setTextColor(resources.getColor(R.color.transparent))
                open!!.setTextColor(resources.getColor(R.color.colorPrimary))
                closed!!.setTextColor(resources.getColor(R.color.colorPrimary))
                loadOrderList("Transit")
            } else if (radio.text.equals("Closed")) {
                radio.setTextColor(resources.getColor(R.color.transparent))
                open!!.setTextColor(resources.getColor(R.color.colorPrimary))
                transit!!.setTextColor(resources.getColor(R.color.colorPrimary))
                loadOrderList("Closed")
            }
        }

        firestoreListener = userRef
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

                orderAdapter = UserOrdersRecyclerViewAdapter(orderList, context!!, firestoreDB!!)
                val orderListRV = root!!.findViewById<View>(R.id.user_order_list) as RecyclerView
                orderListRV.adapter = orderAdapter
                orderListRV.addItemDecoration(
                    DividerItemDecoration(context,
                        DividerItemDecoration.VERTICAL)
                )

                checkRadionButton()

            })

        editsearch!!.setOnQueryTextListener(this)

        return root
    }

    fun checkRadionButton() {
        if (toggle!!.checkedRadioButtonId != -1) {
            val selectedId = toggle!!.getCheckedRadioButtonId()
            val radio: RadioButton = root!!.findViewById(selectedId)
            if (radio.text.equals("Open")) {
                loadOrderList("Open")
            }
            if (radio.text.equals("Transit")) {
                loadOrderList("Transit")
            }
            if (radio.text.equals("Closed")) {
                loadOrderList("Closed")
            }

        }
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

    private fun loadOrderList(typeOrder: String? = null) {
        val userRef: Query

        userRef = firestoreDB!!.collection("orders").whereEqualTo("status",typeOrder)
            .whereEqualTo("email",LoggedInUserEmail)

//        firestoreDB!!.collection("orders").whereEqualTo("email",LoggedInUserEmail)
            userRef.get()
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