package com.example.sugarbroker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_admin_home.*
import kotlinx.android.synthetic.main.activity_user_row.view.*


class AdminHomeActivity : AppCompatActivity() {

    val documentRef = FirebaseFirestore.getInstance().collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        fetchUser()
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun fetchUser() {
        val adapter = GroupAdapter<GroupieViewHolder>()
        
        documentRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException!= null) {
                Log.w("AdminHomeActivity", "listen:error", firebaseFirestoreException)
                return@addSnapshotListener
            }

            for (dc in querySnapshot!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        Log.d("AdminHomeActivity", "New city: ${dc.document.data}")
                        var user = User(dc.document.getString("uid")!!, dc.document.getString("name")!!, dc.document.getString("address")!!, dc.document.getString("phone")!!, dc.document.getString("type")!!)
                        adapter.add(UserItem(user))
                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.d("AdminHomeActivity", "Modified city: ${dc.document.data}")
//                        var user = User(dc.document.getString("uid")!!, dc.document.getString("name")!!, dc.document.getString("address")!!, dc.document.getString("phone")!!, dc.document.getString("type")!!)
//                        adapter.add(UserItem(user))
                        adapter.removeGroupAtAdapterPosition(1)
                    }
                    DocumentChange.Type.REMOVED -> Log.d("AdminHomeActivity", "Removed city: ${dc.document.data}")
                }
                userlist_recyclerview.adapter = adapter
            }
        }
    }

}

class UserItem(val user: User): Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.name_textview.text = user.name
    }

    override fun getLayout(): Int {
        return R.layout.activity_user_row
    }
}

