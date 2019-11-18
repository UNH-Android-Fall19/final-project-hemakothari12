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
import com.example.sugarbroker.activity.resale.AddResaleActivity
import com.example.sugarbroker.adapter.ResaleRecyclerViewAdapter
import com.example.sugarbroker.adapter.TenderRecyclerViewAdapter
import com.example.sugarbroker.model.Resale
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_resale.*
import kotlinx.android.synthetic.main.fragment_resale.view.*
import kotlinx.android.synthetic.main.fragment_resale.view.resaleAdd

/**
 * [Resale Fragment] subclass.
 */
class ResaleFragment : Fragment(), SearchView.OnQueryTextListener, ListClick {

    private val TAG = "ResaleFragment"

    private var resaleAdapter: ResaleRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    lateinit var editsearch: SearchView
    private var mainToolbar: Toolbar? = null
    private var searchIcon: ImageView? = null
    private var backIcon: ImageView? = null
    private var heading: TextView? = null
    private var logout: ImageView? = null
    private var resaleAdd: FloatingActionButton? = null

    private var root: View? = null

    var resaleList = mutableListOf<Resale>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestoreDB = FirebaseFirestore.getInstance()

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_resale, container, false)

        mainToolbar = root!!.findViewById<View>(R.id.toolbar) as Toolbar
        mainToolbar!!.visibility = View.VISIBLE
        heading = root!!.findViewById<View>(R.id.heading_textview) as TextView
        heading!!.visibility = View.VISIBLE
        editsearch = root!!.findViewById(R.id.searchresale_sv) as SearchView
        editsearch.visibility = View.GONE
        backIcon = root!!.findViewById(R.id.back_button) as ImageView
        backIcon!!.visibility = View.GONE
        logout = root!!.findViewById(R.id.logout) as ImageView
        logout!!.visibility = View.VISIBLE
        resaleAdd = root!!.findViewById<View>(R.id.resaleAdd) as FloatingActionButton

        loadResaleList()

        firestoreListener = firestoreDB!!.collection("resale")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                resaleList = mutableListOf<Resale>()

                for (doc in documentSnapshots!!) {
                    val resale = doc.toObject(Resale::class.java)
                    resale.id = doc.id
                    resaleList.add(resale)
                }

                resaleAdapter = ResaleRecyclerViewAdapter(resaleList, context!!, firestoreDB!!)
                val resaleListRV = root!!.findViewById<View>(R.id.resale_list) as RecyclerView
                resaleListRV.adapter = resaleAdapter
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

        resaleAdd!!.setOnClickListener {
            val intent = Intent(context, AddResaleActivity::class.java)
            startActivity(intent)
        }


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
                    resaleList = mutableListOf<Resale>()

                    for (doc in task.result!!) {
                        val resale = doc.toObject<Resale>(Resale::class.java)
                        resale.id = doc.id
                        resaleList.add(resale)
                    }

                    resaleAdapter = ResaleRecyclerViewAdapter(resaleList, context!!, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(context!!)
                    val resaleListRV = root!!.findViewById<View>(R.id.resale_list) as RecyclerView
                    resaleListRV.layoutManager = mLayoutManager
                    resaleListRV.itemAnimator = DefaultItemAnimator()
                    resaleListRV.adapter = resaleAdapter

                    val swipeHandler = object : SwipeToDeleteCallback(context!!) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                            deleteRow(viewHolder.adapterPosition)
                        }
                    }

                    val itemTouchHelper = ItemTouchHelper(swipeHandler)
                    itemTouchHelper.attachToRecyclerView(root?.resale_list!!)

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
        val resale = resaleList[position]
        val uid = resale.id
        firestoreDB!!.collection("resale").document(uid!!).delete()
            .addOnCompleteListener {
                Toast.makeText(context, "Resale has been deleted!", Toast.LENGTH_SHORT).show()
            }
    }


}
