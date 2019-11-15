package com.example.sugarbroker.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.account.LoginActivity
import com.example.sugarbroker.adapter.UserTenderRecyclerViewAdapter
import com.example.sugarbroker.model.Tender
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UserTenderFragment : Fragment() {

    private val TAG = "UserTenderFragment"

    private var tenderAdapter: UserTenderRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private var logout: ImageView? = null

    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firestoreDB = FirebaseFirestore.getInstance()

        root = inflater.inflate(R.layout.fragment_user_tender, container, false)

        logout = root!!.findViewById(R.id.logout) as ImageView
        logout!!.visibility = View.VISIBLE

        loadTenderList()

        firestoreListener = firestoreDB!!.collection("tender")
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

                tenderAdapter = UserTenderRecyclerViewAdapter(tenderList, context!!, firestoreDB!!)
                val tenderListRV = root!!.findViewById<View>(R.id.user_tender_list) as RecyclerView
                tenderListRV.adapter = tenderAdapter
                tenderListRV.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
            })

        logout!!.setOnClickListener {
            performLogout()
        }

        return root
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    private fun loadTenderList() {
        firestoreDB!!.collection("tender")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tenderList = mutableListOf<Tender>()

                    for (doc in task.result!!) {
                        val tender = doc.toObject<Tender>(Tender::class.java)
                        tender.id = doc.id
                        tenderList.add(tender)
                    }

                    tenderAdapter = UserTenderRecyclerViewAdapter(tenderList, context!!, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(context!!)
                    val tenderListRV = root!!.findViewById<View>(R.id.user_tender_list) as RecyclerView
                    tenderListRV.layoutManager = mLayoutManager
                    tenderListRV.itemAnimator = DefaultItemAnimator()
                    tenderListRV.adapter = tenderAdapter
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

}