package com.example.sugarbroker.activity.home

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
import com.example.sugarbroker.activity.account.LoginActivity
import com.example.sugarbroker.activity.tender.AddTenderActivity
import com.example.sugarbroker.activity.users.AddUserActivity
import com.example.sugarbroker.ui.main.SectionsPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SellerHomeActivity : AppCompatActivity() {

    private val TAG = "SellerHomeActivity"

    private var firestoreDB: FirebaseFirestore? = null

    internal var id: Any? = null
    internal var name: Any? = null
    internal var email: Any? = null
    internal var password: Any? = null
    internal var address: Any? = null
    internal var phone: Any? = null
    internal var type: Any? = null
    internal var LoggedInUserEmail: Any? = null
    internal var millName: Any? = null
    internal var price: Any? = null
    internal var millAddress: Any? = null
    internal var contact: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)

        val bundle = intent.extras
        if (bundle != null) {
            LoggedInUserEmail = bundle.getString("LoggedInUserEmail")
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
//        val viewPager: ViewPager = findViewById(R.id.view_pager)
//        viewPager.adapter = sectionsPagerAdapter
//        val tabs: TabLayout = findViewById(R.id.tabs)
//        tabs.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        firestoreDB = FirebaseFirestore.getInstance()

        val inflater = menuInflater
        inflater.inflate(R.menu.seller_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.search -> {
                Toast.makeText(this, "Implement search", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.profile -> {
                val uid = FirebaseAuth.getInstance().uid
                updateProfile(uid!!)
                return true
            }
            R.id.updateprice -> {
                Toast.makeText(this, "Implement Update Price", Toast.LENGTH_SHORT).show()
                updatePrice(LoggedInUserEmail!!)
                return true
            }
            R.id.logout -> {
                Toast.makeText(this, "Implement Logout", Toast.LENGTH_SHORT).show()
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

    private fun updatePrice(LoggedInUserEmail: Any) {
        Log.d("LoggedInUserEmail", "LoggedInUserEmail ${LoggedInUserEmail}")

        val intent = Intent(applicationContext, AddTenderActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        firestoreDB!!.collection("tender").whereEqualTo("contact", LoggedInUserEmail).get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        id = document.get("id")
                        millName = document.get("millName")
                        price = document.get("price")
                        millAddress = document.get("address")
                        contact = document.get("contact")

                        intent.putExtra("UpdateTenderId", id.toString())
                        intent.putExtra("UpdateTenderMillName", millName.toString())
                        intent.putExtra("UpdateTenderPrice", price.toString())
                        intent.putExtra("UpdateTenderAddress", millAddress.toString())
                        intent.putExtra("UpdateTenderContact", contact.toString())

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