package com.example.sugarbroker.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.userEmail
import com.example.sugarbroker.adapter.ResaleRecyclerViewAdapter
import com.example.sugarbroker.adapter.TenderRecyclerViewAdapter
import com.example.sugarbroker.model.Resale
import com.example.sugarbroker.model.Tender
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class SellerUpdatePriceFragment : Fragment(), SearchView.OnQueryTextListener {

    private val TAG = "SellerUpdatePriceFragment"
    private var tenderAdapter: TenderRecyclerViewAdapter? = null
    private var resaleAdapter: ResaleRecyclerViewAdapter? = null
    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private var firestoreListenerResale: ListenerRegistration? = null
    private var LoggedInUserEmail: String? = null
    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firestoreDB = FirebaseFirestore.getInstance()

        root = inflater.inflate(R.layout.fragment_seller_update_price, container, false)

//        MobileAds.initialize(context,getString(R.string.app_unit_id))
//        val mAdView = root!!.findViewById<View>(R.id.adView) as AdView
//        val adRequest = AdRequest.Builder().build()
//        mAdView.loadAd(adRequest)

        loadTenderList()
        loadResaleList()
        LoggedInUserEmail = userEmail
        firestoreListener = firestoreDB!!.collection("tender").whereEqualTo("email", LoggedInUserEmail)
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                val tenderList = mutableListOf<Tender>()

                for (doc in documentSnapshots!!) {
                    val tender = doc.toObject(Tender::class.java)
                    tender.id = doc.id
                    tenderList.add(tender)
                }

                tenderAdapter = TenderRecyclerViewAdapter(tenderList, context!!, firestoreDB!!)
                val tenderListRV = root!!.findViewById<View>(R.id.seller_tender_list) as RecyclerView
                tenderListRV.adapter = tenderAdapter
                tenderListRV.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
            })

        firestoreListenerResale = firestoreDB!!.collection("resale").whereEqualTo("email", LoggedInUserEmail)
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                val resaleList = mutableListOf<Resale>()

                for (doc in documentSnapshots!!) {
                    val resale = doc.toObject(Resale::class.java)
                    resale.id = doc.id
                    resaleList.add(resale)
                }

                resaleAdapter = ResaleRecyclerViewAdapter(resaleList, context!!, firestoreDB!!)
                val resaleListRV = root!!.findViewById<View>(R.id.seller_resale_list) as RecyclerView
                resaleListRV.adapter = resaleAdapter
                resaleListRV.addItemDecoration(
                    DividerItemDecoration(context,
                        DividerItemDecoration.VERTICAL)
                )
            })

        return root
    }

    override fun onQueryTextChange(newText: String): Boolean {
        tenderAdapter!!.filter(newText)
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    private fun loadTenderList() {
        LoggedInUserEmail = userEmail
        firestoreDB!!.collection("tender").whereEqualTo("email", LoggedInUserEmail)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tenderList = mutableListOf<Tender>()

                    for (doc in task.result!!) {
                        val tender = doc.toObject<Tender>(Tender::class.java)
                        tender.id = doc.id
                        tenderList.add(tender)
                    }

                    tenderAdapter = TenderRecyclerViewAdapter(tenderList, context!!, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(context!!)
                    val tenderListRV = root!!.findViewById<View>(R.id.seller_tender_list) as RecyclerView
                    tenderListRV.layoutManager = mLayoutManager
                    tenderListRV.itemAnimator = DefaultItemAnimator()
                    tenderListRV.adapter = tenderAdapter
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }

    private fun loadResaleList() {
        LoggedInUserEmail = userEmail
        firestoreDB!!.collection("resale").whereEqualTo("email", LoggedInUserEmail)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val resaleList = mutableListOf<Resale>()

                    for (doc in task.result!!) {
                        val resale = doc.toObject<Resale>(Resale::class.java)
                        resale.id = doc.id
                        resaleList.add(resale)
                    }

                    resaleAdapter = ResaleRecyclerViewAdapter(resaleList, context!!, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(context!!)
                    val resaleListRV = root!!.findViewById<View>(R.id.seller_resale_list) as RecyclerView
                    resaleListRV.layoutManager = mLayoutManager
                    resaleListRV.itemAnimator = DefaultItemAnimator()
                    resaleListRV.adapter = resaleAdapter
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }

}