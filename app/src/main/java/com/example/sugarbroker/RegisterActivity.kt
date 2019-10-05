package com.example.sugarbroker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button.setOnClickListener {
            performRegister()
        }

        already_account_textview.setOnClickListener {
            Log.d("Mainactivity", "Try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
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
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserDetailsToFirebase() {
        // Realtime database

//        database = FirebaseDatabase.getInstance().reference
//
//
//        val uid = FirebaseAuth.getInstance().uid ?: ""
//        val user = User(uid, name_edittext.text.toString(), address_edittext.text.toString(), phone_edittext.text.toString(), "User")
//        database.child("users").child(uid).setValue(user)


        //##############################################

        //Firestore database

        val uid = FirebaseAuth.getInstance().uid ?: ""

        val db = FirebaseFirestore.getInstance()

        val user = hashMapOf(
            "uid" to uid,
            "name" to name_edittext.text.toString(),
            "address" to address_edittext.text.toString(),
            "phone" to phone_edittext.text.toString(),
            "type" to "User"
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("Regi", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Regi", "Error adding document", e)
            }



    }
}
