package com.example.sugarbroker.activity.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
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
import kotlinx.android.synthetic.main.fragment_user_tender.*

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
                    replaceFragment(UserOrdersFragment(), "USerOrders")
                }
            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }

        if (savedInstanceState == null) {
            replaceFragment(UserTenderFragment(), "UserTender")
        }

        searchIcon.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                Toast.makeText(applicationContext, "${s}", Toast.LENGTH_SHORT).show()
                searchIcon.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_clear_black_24dp,0)

                if (supportFragmentManager.backStackEntryCount > 0) {
                    val tag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                        .name
                    when(tag) {
                        "UserResale" -> {

                        }
                        "UserTender" -> {
                            searchTenderUser_sv.setQuery(s,false)
                        }
                        "UserOrders" -> {

                        }
                    }
                }

            }
        })

        searchIcon.setOnTouchListener { _, event ->
            searchIcon.setText("")
            searchIcon.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            return@setOnTouchListener true
        }


    }

    private fun replaceFragment(fragment: Fragment, screen: String) {
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