package com.example.sugarbroker.fragment


import android.content.Intent
import android.graphics.Color
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
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.sugarbroker.activity.account.LoginActivity
import com.example.sugarbroker.activity.callback.SwipeToDeleteCallback
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_users.view.*

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
    var userList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestoreDB = FirebaseFirestore.getInstance()

        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_users, container, false)

        mainToolbar = root!!.findViewById<View>(R.id.toolbar) as Toolbar
        mainToolbar!!.visibility = View.VISIBLE
        heading = root!!.findViewById<View>(R.id.heading_textview) as TextView
        heading!!.visibility = View.VISIBLE
        editsearch = root!!.findViewById(R.id.searchuser_sv) as SearchView
        editsearch.visibility = View.GONE
        backIcon = root!!.findViewById(R.id.back_button) as ImageView
        backIcon!!.visibility = View.GONE
        logout = root!!.findViewById(R.id.logout) as ImageView
        logout!!.visibility = View.VISIBLE

        loadUserList()

        firestoreListener = firestoreDB!!.collection("users")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                userList = mutableListOf<User>()

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
                    userList = mutableListOf<User>()

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

                    val swipeHandler = object : SwipeToDeleteCallback(context!!) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                            val position = viewHolder.adapterPosition
                            val deletedModel = userList!![position]
                            val uid = deletedModel.uid
                            firestoreDB!!.collection("tender").document(uid!!).delete()
                                .addOnCompleteListener {
                                    Toast.makeText(context, "Tender has been deleted!", Toast.LENGTH_SHORT).show()
                                }
                            userAdapter!!.removeItem(position)
                            // showing snack bar with Undo option
                            val snackbar = Snackbar.make(
                                view!!,
                                " removed from Recyclerview!",
                                Snackbar.LENGTH_LONG
                            )
                            snackbar.setAction("UNDO") {
                                // undo is selected, restore the deleted item
                                firestoreDB!!.collection("tender")
                                    .document(uid)
                                    .set(deletedModel)
                                    .addOnSuccessListener {
                                        Log.e(TAG, "Tender document Added successful!")
                                        Toast.makeText(context, "Tender has been updated!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Error adding Tender document", e)
                                        Toast.makeText(context, "Tender could not be updated!", Toast.LENGTH_SHORT).show()
                                    }
                                userAdapter!!.restoreItem(deletedModel, position)

                            }
                            snackbar.setActionTextColor(Color.YELLOW)
                            snackbar.show()
                        }
                    }

                    val itemTouchHelper = ItemTouchHelper(swipeHandler)
                    itemTouchHelper.attachToRecyclerView(root?.user_list!!)

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
