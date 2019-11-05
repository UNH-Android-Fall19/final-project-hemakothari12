package com.example.sugarbroker.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.adapter.UserRecyclerViewAdapter
import com.example.sugarbroker.model.User
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * [Orders Fragment] subclass.
 */
class UserFragment : Fragment(), SearchView.OnQueryTextListener {

    private val TAG = "UserFragment"

    private var userAdapter: UserRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    lateinit var editsearch: SearchView

    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestoreDB = FirebaseFirestore.getInstance()

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_users, container, false)

        loadUserList()

        firestoreListener = firestoreDB!!.collection("users")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                val userList = mutableListOf<User>()

                for (doc in documentSnapshots!!) {
                    val user = doc.toObject(User::class.java)
                    user.uid = doc.id
                    userList.add(user)
                }

                userAdapter = UserRecyclerViewAdapter(userList, context!!, firestoreDB!!)
                val userListRV = root!!.findViewById<View>(R.id.user_list) as RecyclerView
                userListRV.adapter = userAdapter
            })

        editsearch = root!!.findViewById(R.id.searchuser_sv) as SearchView
        editsearch!!.setOnQueryTextListener(this)

        return root
    }

    override fun onQueryTextChange(newText: String): Boolean {
        userAdapter!!.filter(newText)
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    private fun loadUserList() {
        firestoreDB!!.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userList = mutableListOf<User>()

                    for (doc in task.result!!) {
                        val user = doc.toObject<User>(User::class.java)
                        user.uid = doc.id
                        userList.add(user)
                    }

                    userAdapter = UserRecyclerViewAdapter(userList, context!!, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(context!!)
                    val userListRV = root!!.findViewById<View>(R.id.user_list) as RecyclerView
                    userListRV.layoutManager = mLayoutManager
                    userListRV.itemAnimator = DefaultItemAnimator()
                    userListRV.adapter = userAdapter
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }


}
