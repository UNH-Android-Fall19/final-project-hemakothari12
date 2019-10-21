package com.example.sugarbroker.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.EventListener
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.AddTenderActivity
import com.example.sugarbroker.activity.LoginActivity
import com.example.sugarbroker.adapter.TenderRecyclerViewAdapter
import com.example.sugarbroker.model.Tender
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_tender.*

/**
 * A simple [Fragment] subclass.
 */
class TenderFragment : Fragment() {

    private val TAG = "TenderFragment"

    private var mAdapter: TenderRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        firestoreDB = FirebaseFirestore.getInstance()

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

                mAdapter = TenderRecyclerViewAdapter(tenderList, context!!, firestoreDB!!)
                tender_list.adapter = mAdapter
            })

        return inflater.inflate(R.layout.fragment_tender, container, false)
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

                    mAdapter = TenderRecyclerViewAdapter(tenderList, context!!, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(context!!)
                    tender_list.layoutManager = mLayoutManager
                    tender_list.itemAnimator = DefaultItemAnimator()
                    tender_list.adapter = mAdapter
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                Log.d("Impletemebt serach", "Implement search")
                return true
            }
            R.id.logout -> {
                //Implemented Logout
                performLogout()
                return true
            }
            R.id.add -> {
                Log.d("TenderFragment", "Add Tender clicked")
                val intent = Intent(context!!, AddTenderActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(context!!, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


}
