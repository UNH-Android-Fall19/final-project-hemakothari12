package com.example.sugarbroker.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sugarbroker.R
import com.example.sugarbroker.fragment.OrdersFragment
import com.example.sugarbroker.fragment.ResaleFragment
import com.example.sugarbroker.fragment.TenderFragment
import com.example.sugarbroker.fragment.UsersFragment
import com.example.sugarbroker.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_admin_home.*
import kotlinx.android.synthetic.main.activity_user_row.view.*


class AdminHomeActivity : AppCompatActivity() {

    val documentRef = FirebaseFirestore.getInstance().collection("users")
    private val onNavigationItemReselectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item->
        when(item.itemId) {
            R.id.navigation_resale -> {
                Log.d("AdminHomeActivity", "Resale Clicked")
                replaceFragment(ResaleFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_tender -> {
                Log.d("AdminHomeActivity", "Tender Clicked")
                replaceFragment(TenderFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_orders -> {
                Log.d("AdminHomeActivity", "Orders Clicked")
                replaceFragment(OrdersFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_users -> {
                Log.d("AdminHomeActivity", "Users Clicked")
                replaceFragment(UsersFragment())
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

//        fetchUser()

        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener)
        replaceFragment(ResaleFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.search -> {
                Toast.makeText(this, "Implement search", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.logout -> {
                //Implemented Logout
                performLogout()
                return true
            }
            R.id.add -> {
                val intent = Intent(this, AddTenderActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

//    private fun fetchUser() {
//        val adapter = GroupAdapter<GroupieViewHolder>()
//
//        documentRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//            if (firebaseFirestoreException!= null) {
//                Log.w("AdminHomeActivity", "listen:error", firebaseFirestoreException)
//                return@addSnapshotListener
//            }
//
//            for (dc in querySnapshot!!.documentChanges) {
//                when (dc.type) {
//                    DocumentChange.Type.ADDED -> {
//                        Log.d("AdminHomeActivity", "New city: ${dc.document.data}")
//                        var user = User(
//                            dc.document.getString("uid")!!,
//                            dc.document.getString("name")!!,
//                            dc.document.getString("address")!!,
//                            dc.document.getString("phone")!!,
//                            dc.document.getString("type")!!
//                        )
//                        adapter.add(UserItem(user))
//                    }
//                    DocumentChange.Type.MODIFIED -> {
//                        Log.d("AdminHomeActivity", "Modified city: ${dc.document.data}")
////                        var user = User(dc.document.getString("uid")!!, dc.document.getString("name")!!, dc.document.getString("address")!!, dc.document.getString("phone")!!, dc.document.getString("type")!!)
////                        adapter.add(UserItem(user))
//                        adapter.removeGroupAtAdapterPosition(1)
//                    }
//                    DocumentChange.Type.REMOVED -> Log.d("AdminHomeActivity", "Removed city: ${dc.document.data}")
//                }
//                userlist_recyclerview.adapter = adapter
//            }
//        }
//    }

}

//class UserItem(val user: User): Item<GroupieViewHolder>() {
//
//
//    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        viewHolder.itemView.name_textview.text = user.name
//    }
//
//    override fun getLayout(): Int {
//        return R.layout.activity_user_row
//    }
//}

