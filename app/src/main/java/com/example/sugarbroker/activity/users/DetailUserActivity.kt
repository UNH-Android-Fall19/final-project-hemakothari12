package com.example.sugarbroker.activity.users

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.userType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail_user.*

class DetailUserActivity : AppCompatActivity() {

    private val TAG = "DetailUserActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)

        firestoreDB = FirebaseFirestore.getInstance()

        setUpToolbar()

        if(userType == "User" || userType == "Seller") {
            editUser.visibility = View.GONE
        }else {
            editUser.visibility = View.VISIBLE
        }

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateUserId")
            name_textview.setText(bundle.getString("UpdateUserName"))
            email_textview.setText(bundle.getString("UpdateUserEmail"))
            password_textview.setText(bundle.getString("UpdateUserPassword"))
            address_textview.setText(bundle.getString("UpdateUserAddress"))
            phone_textview.setText(bundle.getString("UpdateUserPhone"))
            type_textview.setText(bundle.getString("UpdateUserType"))
            tvIcon.setText(bundle.getString("UpdateUserName")!!.get(0).toUpperCase().toString())
        }

        callUser.setOnClickListener {
            val mobNum = bundle!!.getString("UpdateUserPhone")
            val phoneIntent = Intent(
                Intent.ACTION_DIAL, Uri.fromParts(
                    "tel", mobNum, null
                )
            )
            startActivity(phoneIntent)
        }

        editUser.setOnClickListener { updateUser() }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
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

    private fun updateUser() {
        val intent = Intent(applicationContext, AddUserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateUserId", id)
        intent.putExtra("UpdateUserName", name_textview.text.toString())
        intent.putExtra("UpdateUserEmail", email_textview.text.toString())
        intent.putExtra("UpdateUserPassword", password_textview.text.toString())
        intent.putExtra("UpdateUserAddress", address_textview.text.toString())
        intent.putExtra("UpdateUserPhone", phone_textview.text.toString())
        intent.putExtra("UpdateUserType", type_textview.text.toString())
        intent.putExtra("UpdateUserProfileType", "Admin")
        applicationContext.startActivity(intent)
    }

}
