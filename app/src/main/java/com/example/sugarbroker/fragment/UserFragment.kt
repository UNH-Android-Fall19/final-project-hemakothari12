package com.example.sugarbroker.fragment


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
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
    private var mainToolbar: Toolbar? = null
    private var searchIcon: ImageView? = null
    private var backIcon: ImageView? = null
    private var heading: TextView? = null
    private var logout: ImageView? = null

    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestoreDB = FirebaseFirestore.getInstance()

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_users, container, false)

//        setHasOptionsMenu(true)
        setUpToolbar()

        mainToolbar = root!!.findViewById<View>(R.id.toolbar) as Toolbar
        mainToolbar!!.visibility = View.VISIBLE
        heading = root!!.findViewById<View>(R.id.heading_textview) as TextView
        heading!!.visibility = View.VISIBLE
        editsearch = root!!.findViewById(R.id.searchuser_sv) as SearchView
        editsearch.visibility = View.GONE
        backIcon = root!!.findViewById(R.id.back_button) as ImageView
        backIcon!!.visibility = View.GONE
        backIcon!!.setOnClickListener {
            editsearch.setQuery("",false)
            editsearch.clearFocus()
            backIcon!!.visibility = View.GONE
            editsearch!!.visibility = View.GONE
            heading!!.visibility = View.VISIBLE
            searchIcon!!.visibility = View.VISIBLE
            logout!!.visibility = View.VISIBLE
        }
        logout = root!!.findViewById(R.id.logout) as ImageView
        logout!!.visibility = View.VISIBLE

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


        editsearch!!.setOnQueryTextListener(this)

        searchIcon = root!!.findViewById<View>(R.id.search_icon) as ImageView
        searchIcon!!.setOnClickListener {
            backIcon!!.visibility = View.VISIBLE
            editsearch!!.visibility = View.VISIBLE
            heading!!.visibility = View.GONE
            searchIcon!!.visibility = View.GONE
            logout!!.visibility = View.GONE
            editsearch!!.requestFocus()
        }

        return root
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.toolbar_menu_user, menu)
//
//
//        super.onCreateOptionsMenu(menu, inflater)
//    }

    private fun setUpToolbar() {
        setHasOptionsMenu(true)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        }
        (activity as AppCompatActivity).supportActionBar?.title = "Users"
        (activity as AppCompatActivity).supportActionBar?.elevation = 4.0F
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
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
