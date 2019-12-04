package com.example.sugarbroker.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.account.LoginActivity
import com.example.sugarbroker.activity.home.AdminHomeActivity
import com.example.sugarbroker.activity.home.SellerHomeActivity
import com.example.sugarbroker.activity.home.UserHomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        adminName.visibility = View.GONE

        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            adminName.visibility = View.VISIBLE
        },4000)

        Handler().postDelayed({
            callHomePage()
        },5000)
    }

    private fun callHomePage() {
        if (auth.currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            val uid = FirebaseAuth.getInstance().uid ?: ""
            val documentRef = db.collection("users").document(uid)
            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val value = document.getString("type")

                        if (value == "Admin") {
                            Log.d(TAG, "User Logged in is Admin")
                            intent = Intent(applicationContext, AdminHomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else if (value == "Seller") {
                            Log.d(TAG, "User Logged in is Seller")
                            userType = "Seller"
                            intent = Intent(applicationContext, SellerHomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("LoggedInUserEmail",document.getString("email"))
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            Log.d(TAG, "User Logged in is User")
                            userType = "User"
                            intent = Intent(applicationContext, UserHomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("LoggedInUserEmail",document.getString("email"))
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }

                    } else {
                        Log.d(TAG, "No document found")
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Failed to fetch document: ", exception)

                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }

        } else {
            intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
