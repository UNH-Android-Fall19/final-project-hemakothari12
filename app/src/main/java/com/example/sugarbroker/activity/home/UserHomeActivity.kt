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
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.account.LoginActivity
import com.example.sugarbroker.activity.users.AddUserActivity
import com.example.sugarbroker.fragment.UserOrdersFragment
import com.example.sugarbroker.fragment.UserResaleFragment
import com.example.sugarbroker.fragment.UserTenderFragment
import com.example.sugarbroker.ui.main.SectionsPagerAdapter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_home.*

class UserHomeActivity : AppCompatActivity() {

    private val TAG = "UserHomeActivity"

    private lateinit var mDrawerLayout: DrawerLayout


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
//        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
//        val viewPager: ViewPager = findViewById(R.id.view_pager)
//        viewPager.adapter = sectionsPagerAdapter
//        val tabs: TabLayout = findViewById(R.id.tabs)
//        tabs.setupWithViewPager(viewPager)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_drawer_icon)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Handle navigation view item clicks here.
            when (menuItem.itemId) {

                R.id.nav_tender -> {
                    replaceFragment(UserTenderFragment(), "Resale")
                }
                R.id.nav_resale -> {
                    replaceFragment(UserResaleFragment(), "Resale")
                }
                R.id.nav_order -> {
                    replaceFragment(UserOrdersFragment(), "Resale")
                }
            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }


    }

    private fun replaceFragment(fragment: Fragment, screen: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, screen)
        fragmentTransaction.addToBackStack(screen)
        fragmentTransaction.commit()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//
//        firestoreDB = FirebaseFirestore.getInstance()
//
//        val inflater = menuInflater
//        inflater.inflate(R.menu.user_toolbar_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.user_drawer_menu, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//
//        when (item.itemId) {
//            R.id.search -> {
//                Toast.makeText(this, "Implement search", Toast.LENGTH_SHORT).show()
//                return true
//            }
//            R.id.logout -> {
//                //Implement Logout
//                Toast.makeText(this, "Implement Logout", Toast.LENGTH_SHORT).show()
//                performLogout()
//                return true
//            }
//            R.id.profile -> {
//                val uid = FirebaseAuth.getInstance().uid
//                updateProfile(uid!!)
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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