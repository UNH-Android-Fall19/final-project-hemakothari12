package com.example.sugarbroker.fragment


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.EventListener
import com.example.sugarbroker.R
import com.example.sugarbroker.adapter.TenderRecyclerViewAdapter
import com.example.sugarbroker.model.Tender
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * [Tender Fragment] subclass.
 */
class TenderFragment : Fragment() {

    private val TAG = "TenderFragment"

    private var tenderAdapter: TenderRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null

    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        firestoreDB = FirebaseFirestore.getInstance()

        root = inflater.inflate(R.layout.fragment_tender, container, false)

        setHasOptionsMenu(true)

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

                tenderAdapter = TenderRecyclerViewAdapter(tenderList, context!!, firestoreDB!!)
                val tenderListRV = root!!.findViewById<View>(R.id.tender_list) as RecyclerView
                tenderListRV.adapter = tenderAdapter
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

                    tenderAdapter = TenderRecyclerViewAdapter(tenderList, context!!, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(context!!)
                    val tenderListRV = root!!.findViewById<View>(R.id.tender_list) as RecyclerView
                    tenderListRV.layoutManager = mLayoutManager
                    tenderListRV.itemAnimator = DefaultItemAnimator()
                    tenderListRV.adapter = tenderAdapter
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }
}
