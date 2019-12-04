package com.example.sugarbroker.activity.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.account.ContactUsActivity
import com.example.sugarbroker.activity.account.LoginActivity
import com.example.sugarbroker.activity.userEmail
import com.example.sugarbroker.activity.userName
import com.example.sugarbroker.activity.users.AddUserActivity
import com.example.sugarbroker.fragment.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_home.*

class SellerHomeActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val TAG = "SellerHomeActivity"
    private var firestoreDB: FirebaseFirestore? = null
    internal var id: Any? = null
    internal var name: Any? = null
    internal var email: Any? = null
    internal var password: Any? = null
    internal var address: Any? = null
    internal var phone: Any? = null
    internal var type: Any? = null
    private lateinit var mDrawerLayout: DrawerLayout
    lateinit var searchIcon1: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_home)

        firestoreDB = FirebaseFirestore.getInstance()

        setHeader()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

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
                    replaceFragment(UserTenderFragment(), "UserTender")
                }
                R.id.nav_resale -> {
                    replaceFragment(UserResaleFragment(), "UserResale")
                }
                R.id.nav_order -> {
                    replaceFragment(UserOrdersFragment(), "UserOrders")
                }
                R.id.updatePrice -> {
                    replaceFragment(SellerUpdatePriceFragment(), "SellerUpdatePrice")
                }
                R.id.updateProfile -> {
                    updateProfile()
                }
                R.id.settings -> {
                    replaceFragment(SettingsFragment(), "Settings")
                }
                R.id.contactUs -> {
                    contactUs()
                }
                R.id.logout -> {
                    performLogout()
                }
            }

            true
        }

        if (savedInstanceState == null) {
            replaceFragment(UserTenderFragment(), "UserTender")
        }

        searchIcon1 = findViewById<EditText>(R.id.searchIcon) as SearchView
        searchIcon1.setOnQueryTextListener(this)
    }

    override fun onQueryTextChange(newText: String): Boolean {

        if (supportFragmentManager.backStackEntryCount > 0) {
            val tag =
                supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                    .name
            when (tag) {
                "UserResale" -> {
                    val searchResaleUser = findViewById<View>(R.id.searchResaleUser_sv) as SearchView
                    searchResaleUser.setQuery(newText, false)
                }
                "UserTender" -> {
                    val searchTenderUser = findViewById<View>(R.id.searchTenderUser_sv) as SearchView
                    searchTenderUser.setQuery(newText, false)
                }
                "UserOrders" -> {
                    val searchOrderUser = findViewById<View>(R.id.searchOrderUser_sv) as SearchView
                    searchOrderUser.setQuery(newText, false)
                }
            }
        }

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    private fun replaceFragment(fragment: Fragment, screen: String) {
        val searchIconClear = findViewById<View>(R.id.searchIcon) as SearchView
        searchIconClear.setQuery("", false)
        if (screen == "SellerUpdatePrice" || screen == "Settings") {
            searchIconClear.visibility = View.GONE
        } else {
            searchIconClear.visibility = View.VISIBLE
        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, screen)
        fragmentTransaction.addToBackStack(screen)
        fragmentTransaction.commit()
    }

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

    private fun contactUs() {
        val intent = Intent(applicationContext, ContactUsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }

    private fun updateProfile() {
        val intent = Intent(applicationContext, AddUserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uid = FirebaseAuth.getInstance().uid

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

    private fun setHeader() {
        val uid = FirebaseAuth.getInstance().uid

        val hView = nav_view.getHeaderView(0)
        val textViewName = hView.findViewById(R.id.nameUser) as TextView
        val textViewEmail = hView.findViewById(R.id.emailUser) as TextView
        val textviewIcon = hView.findViewById(R.id.userIcon) as TextView

        firestoreDB!!.collection("users").whereEqualTo("uid", uid).get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        userName = document.get("name").toString()
                        userEmail = document.get("email").toString()

                        textViewName.setText(userName)
                        textViewEmail.setText(userEmail)
                        textviewIcon.setText(userName!!.get(0).toUpperCase().toString())

                    }
                } else {
                    Log.d(TAG, "Error Fetching Details")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}