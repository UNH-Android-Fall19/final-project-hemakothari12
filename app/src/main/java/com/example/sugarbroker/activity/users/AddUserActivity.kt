package com.example.sugarbroker.activity.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.sugarbroker.R
import com.example.sugarbroker.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_tender.add_button
import kotlinx.android.synthetic.main.activity_add_user.*

class AddUserActivity : AppCompatActivity() {

    private val TAG = "AddUserActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var uid: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        firestoreDB = FirebaseFirestore.getInstance()

        setUpToolbar()

        val bundle = intent.extras
        if (bundle != null) {
            uid = bundle.getString("UpdateUserId")
            Toast.makeText(applicationContext, "ID ${uid}", Toast.LENGTH_SHORT).show()

            val userType = bundle.getString("UpdateUserProfileType")

            if (userType == "Admin") {
                type_edittext.visibility = View.VISIBLE
            } else {
                type_edittext.visibility = View.GONE
            }

            name_textview.setText(bundle.getString("UpdateUserName"))
            email_textview.setText(bundle.getString("UpdateUserEmail"))
            address_edittext.setText(bundle.getString("UpdateUserAddress"))
            phone_edittext.setText(bundle.getString("UpdateUserPhone"))
            type_edittext.setText(bundle.getString("UpdateUserType"))
        }

        add_button.setOnClickListener {
            val name = name_textview.text.toString()
            val email = email_textview.text.toString()
            val address = address_edittext.text.toString()
            val phone = phone_edittext.text.toString()
            val type = type_edittext.text.toString()
            val password = password_edittext.text.toString()

            if (title.isNotEmpty()) {
                if (uid!!.isNotEmpty()) {
                    updateUser(uid!!, name, email, password, address, phone, type)
                }
            }

            finish()

        }
    }

    private fun updateUser(uid: String, name: String, email: String, password: String, address: String, phone: String, type: String) {
        val user = User(uid, name, email, password, address, phone, type)

        firestoreDB!!.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.e(TAG, "User document update successful!")
                Toast.makeText(applicationContext, "User has been updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating User document", e)
                Toast.makeText(applicationContext, "User could not be updated!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = "Update User Details"
        actionBar!!.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        val count = supportFragmentManager.backStackEntryCount
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            if (count == 0)
                super.onBackPressed()
            else
                supportFragmentManager.popBackStack()
        })
    }
}
