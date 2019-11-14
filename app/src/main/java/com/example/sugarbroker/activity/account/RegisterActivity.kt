package com.example.sugarbroker.activity.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.home.UserHomeActivity
import com.example.sugarbroker.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setUpToolbar()

        val createAccount = "Already have an account? " + "<font><b>" + "Sign in" + "<b></font>"
        already_account_textview.setText(Html.fromHtml(createAccount))

        register_button.setOnClickListener {
            performRegister()
        }

        already_account_textview.setOnClickListener {
            Log.d("Mainactivity", "Try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun performRegister() {
        var email = email_edittext.text.toString()
        var password = password_edittext.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Email", "Email is: " + email)
        Log.d("Password", "Password is:  + $password")

        //Firebase Authentication to create user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                saveUserDetailsToFirebase()
                Log.d("Main", "Successfully created user with uid: ${it?.result?.user?.uid} ")

                val intent = Intent(this, UserHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserDetailsToFirebase() {

        //Firestore database

        val uid = FirebaseAuth.getInstance().uid ?: ""

        val db = FirebaseFirestore.getInstance()

        val user = User(
            uid,
            name_edittext.text.toString(),
            email_edittext.text.toString(),
            password_edittext.text.toString(),
            address_edittext.text.toString(),
            phone_edittext.text.toString(),
            "User"
        )

        //Add document with specific ID
        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("Regi", "DocumentSnapshot added with ID")
            }
            .addOnFailureListener { e ->
                Log.w("Regi", "Error adding document", e)
            }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.register)
        actionBar.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
    }
}
