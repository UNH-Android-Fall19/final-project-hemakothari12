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
import com.example.sugarbroker.fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_admin_home.*


class AdminHomeActivity : AppCompatActivity() {

    private val TAG = "AdminHomeActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var name: Any? = null
    internal var email: Any? = null
    internal var password: Any? = null
    internal var address: Any? = null
    internal var phone: Any? = null
    internal var type: Any? = null

    private val onNavigationItemReselectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item->
        when(item.itemId) {
            R.id.navigation_resale -> {
                Log.d("AdminHomeActivity", "Resale Clicked")
                replaceFragment(ResaleFragment(), "Resale")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_tender -> {
                Log.d("AdminHomeActivity", "Tender Clicked")
                replaceFragment(TenderFragment(), "Tender")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_orders -> {
                Log.d("AdminHomeActivity", "Orders Clicked")
                replaceFragment(OrdersFragment(), "Orders")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_users -> {
                Log.d("AdminHomeActivity", "Users Clicked")
                replaceFragment(UserFragment(), "Users")
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        firestoreDB = FirebaseFirestore.getInstance()

        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener)


        if (supportFragmentManager.backStackEntryCount > 0) {
            val tag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                .name
            when(tag) {
                "Resale" -> {
                    replaceFragment(ResaleFragment(), "Resale")
                }
                "Tender" -> {
                    replaceFragment(TenderFragment(), "Tender")
                }
                "Orders" -> {
                    replaceFragment(OrdersFragment(), "Orders")
                }
                "Users" -> {
                    replaceFragment(UserFragment(), "Users")
                }
            }
        }
        else {
            replaceFragment(ResaleFragment(), "Resale")
        }

    }

    private fun replaceFragment(fragment: Fragment, screen: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, screen)
        fragmentTransaction.addToBackStack(screen)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)

        return true
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
            R.id.profile -> {
                val uid = FirebaseAuth.getInstance().uid
                updateProfile(uid!!)
                return true
            }
            R.id.add -> {
                val tag =
                    supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                        .name

                Log.d("Tag vale", "${tag}")

                when(tag) {
                    "Resale" -> {
                        val intent = Intent(this, AddResaleActivity::class.java)
                        startActivity(intent)
                    }
                    "Tender" -> {
                        val intent = Intent(this, AddTenderActivity::class.java)
                        startActivity(intent)
                    }
                    "Orders" -> {
                        val intent = Intent(this, AddOrderActivity::class.java)
                        startActivity(intent)
                    }
                    "Users" -> {
                        Log.d("AdminHomeActivity", "Cannot Add Users but can only update")
                        Toast.makeText(this, "Cannot Add User but only Update", Toast.LENGTH_SHORT).show()
                    }
                }


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
                        intent.putExtra("UpdateUserProfileType", "Admin")

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

