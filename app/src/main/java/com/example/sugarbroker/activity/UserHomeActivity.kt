package com.example.sugarbroker.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.sugarbroker.R
import com.example.sugarbroker.ui.main.SectionsPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserHomeActivity : AppCompatActivity() {

    private val TAG = "UserHomeActivity"

    private var firestoreDB: FirebaseFirestore? = null

    internal var id: String? = null
    internal var name: Any? = null
    internal var email: Any? = null
    internal var password: Any? = null
    internal var address: Any? = null
    internal var phone: Any? = null
    internal var type: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        firestoreDB = FirebaseFirestore.getInstance()

        val inflater = menuInflater
        inflater.inflate(R.menu.user_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.search -> {
                Toast.makeText(this, "Implement search", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.logout -> {
                //Implement Logout
                Toast.makeText(this, "Implement Logout", Toast.LENGTH_SHORT).show()
                performLogout()
                return true
            }
            R.id.profile -> {
                val uid = FirebaseAuth.getInstance().uid
                updateProfile(uid!!)
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

    private fun updateProfile(uid: Any ) {
        val intent = Intent(applicationContext, AddUserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        Log.d("UID in function", "uid in function ${uid}")

        firestoreDB!!.collection("users").whereEqualTo("uid", uid).get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        name = document.get("name")
                        email = document.get("email")
                        password = document.get("password")
                        address = document.get("address")
                        phone = document.get("phone")
                        type = document.get("type")

                        intent.putExtra("UpdateUserId", uid.toString())
                        intent.putExtra("UpdateUserName", name.toString())
                        intent.putExtra("UpdateUserEmail", email.toString())
                        intent.putExtra("UpdateUserPassword", password.toString())
                        intent.putExtra("UpdateUserAddress", address.toString())
                        intent.putExtra("UpdateUserPhone", phone.toString())
                        intent.putExtra("UpdateUserType", type.toString())
                        intent.putExtra("UpdateUserProfileType", "User")

                        applicationContext.startActivity(intent)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}