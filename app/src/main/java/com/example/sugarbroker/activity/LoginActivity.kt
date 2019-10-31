package com.example.sugarbroker.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sugarbroker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            performLogin()
        }

        newuser_textview.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        forgotpassword_textview.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
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
                                intent = Intent(applicationContext, AdminHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            } else if (value == "Seller") {
                                Log.d("User Logged", "User Logged in is Seller")
                                intent = Intent(applicationContext, SellerHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)

                            } else {
                                Log.d("User Logged", "User Logged in is User")

                                intent = Intent(applicationContext, UserHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
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
