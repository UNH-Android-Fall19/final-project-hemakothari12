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
import com.example.sugarbroker.adapter.UserResaleRecyclerViewAdapter
import com.example.sugarbroker.model.Resale
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UserResaleFragment : Fragment(), SearchView.OnQueryTextListener {

    private val TAG = "UserResaleFragment"

    private var resaleAdapter: UserResaleRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    lateinit var editSearchUser: SearchView

    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firestoreDB = FirebaseFirestore.getInstance()

        root = inflater.inflate(R.layout.fragment_user_resale, container, false)

        MobileAds.initialize(context,getString(R.string.app_unit_id))
        val mAdView = root!!.findViewById<View>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        editSearchUser = root!!.findViewById(R.id.searchResaleUser_sv) as SearchView

        loadResaleList()

        firestoreListener = firestoreDB!!.collection("resale")
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

                resaleAdapter = UserResaleRecyclerViewAdapter(resaleList, context!!, firestoreDB!!)
                val resaleListRV = root!!.findViewById<View>(R.id.user_resale_list) as RecyclerView
                resaleListRV.adapter = resaleAdapter
                resaleListRV.addItemDecoration(
                    DividerItemDecoration(context,
                        DividerItemDecoration.VERTICAL)
                )
            })


        editSearchUser!!.setOnQueryTextListener(this)

        return root
    }

    override fun onQueryTextChange(newText: String): Boolean {
        resaleAdapter!!.filter(newText)
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    private fun loadResaleList() {
        firestoreDB!!.collection("resale")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val resaleList = mutableListOf<Resale>()

                    for (doc in task.result!!) {
                        val resale = doc.toObject<Resale>(Resale::class.java)
                        resale.id = doc.id
                        resaleList.add(resale)
                    }

                    resaleAdapter = UserResaleRecyclerViewAdapter(resaleList, context!!, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(context!!)
                    val resaleListRV = root!!.findViewById<View>(R.id.user_resale_list) as RecyclerView
                    resaleListRV.layoutManager = mLayoutManager
                    resaleListRV.itemAnimator = DefaultItemAnimator()
                    resaleListRV.adapter = resaleAdapter
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }

}