package com.example.sugarbroker.fragment


import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
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
import android.view.Gravity
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat.getActionView
import kotlinx.android.synthetic.main.fragment_users.*


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

//        setHasOptionsMenu(true)
//        setUpToolbar()

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

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.toolbar_menu_user, menu)
//
//
//        super.onCreateOptionsMenu(menu, inflater)
//    }

//    private fun setUpToolbar() {
//
//        if(activity is AppCompatActivity){
//            (activity as AppCompatActivity).setSupportActionBar(toolbar)
//        }
//        (activity as AppCompatActivity).supportActionBar?.title = "Users"
//        (activity as AppCompatActivity).supportActionBar?.elevation = 4.0F
//        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
//        (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(true)
//        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
//    }

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
