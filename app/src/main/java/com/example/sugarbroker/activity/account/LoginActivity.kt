package com.example.sugarbroker.activity.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.home.AdminHomeActivity
import com.example.sugarbroker.activity.home.SellerHomeActivity
import com.example.sugarbroker.activity.home.UserHomeActivity
import com.example.sugarbroker.activity.userType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    private var progressBar: ProgressBar? = null
    private var i = 0
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressBar = findViewById<ProgressBar>(R.id.progress_Bar) as ProgressBar
        progressBar!!.visibility = View.GONE

        val createAccount = "Don't have an account? " + "<font><b>" + "Sign up" + "<b></font>"
        newuser_textview.setText(Html.fromHtml(createAccount))

        login_button.setOnClickListener {
            progressBar!!.visibility = View.VISIBLE
            login_email_edittext.visibility = View.GONE
            login_password_edittext.visibility = View.GONE
            login_button.visibility = View.GONE
            forgotpassword_textview.visibility = View.GONE
            newuser_textview.visibility = View.GONE
            performLogin()
        }

        newuser_textview.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotpassword_textview.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        var email = login_email_edittext.text.toString()
        var password = login_password_edittext.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // Fetch User type
                val db = FirebaseFirestore.getInstance()
                val uid = FirebaseAuth.getInstance().uid ?: ""
                val documentRef = db.collection("users").document(uid)
                documentRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val value = document.getString("type")
                            Log.d("Document", "DocumentSnapshot data: ${document.data}")

                            if (value == "Admin") {
                                Log.d("User Logged", "User Logged in is Admin")
                                userType = "Admin"
                                progressBar!!.visibility = View.GONE
                                intent = Intent(applicationContext, AdminHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            } else if (value == "Seller") {
                                Log.d("User Logged", "User Logged in is Seller")
                                userType = "Seller"
                                progressBar!!.visibility = View.GONE
                                intent = Intent(applicationContext, SellerHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("LoggedInUserEmail",login_email_edittext.text.toString())
                                startActivity(intent)
                            } else {
                                Log.d("User Logged", "User Logged in is User")
                                userType = "User"
                                progressBar!!.visibility = View.GONE
                                intent = Intent(applicationContext, UserHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("LoggedInUserEmail",login_email_edittext.text.toString())
                                startActivity(intent)
                            }

                        } else {
                            Log.d("LoginActivity", "No document found")

                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("LoginActivity", "Failed to fetch document: ", exception)

                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Log.d("LoginActivity", "Failed to Login: ${it.message}")

                Toast.makeText(this, "Invalid username and password", Toast.LENGTH_SHORT).show()
            }
    }
}
