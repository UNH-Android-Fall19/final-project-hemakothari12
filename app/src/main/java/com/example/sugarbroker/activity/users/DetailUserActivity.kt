package com.example.sugarbroker.activity.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sugarbroker.R
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

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateUserId")

            name_textview.setText(bundle.getString("UpdateUserName"))
            email_textview.setText(bundle.getString("UpdateUserEmail"))
            password_textview.setText(bundle.getString("UpdateUserPassword"))
            address_textview.setText(bundle.getString("UpdateUserAddress"))
            phone_textview.setText(bundle.getString("UpdateUserPhone"))
            type_textview.setText(bundle.getString("UpdateUserType"))
        }
    }

}
