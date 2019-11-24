package com.example.sugarbroker.activity.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.sugarbroker.R
import com.example.sugarbroker.fragment.OrdersFragment
import com.example.sugarbroker.fragment.ResaleFragment
import com.example.sugarbroker.fragment.TenderFragment
import com.example.sugarbroker.fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
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
}

