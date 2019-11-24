package com.example.sugarbroker.activity.tender

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.home.SellerHomeActivity
import com.example.sugarbroker.activity.home.UserHomeActivity
import com.example.sugarbroker.activity.userType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_buy_tender.*


class BuyTenderActivity : AppCompatActivity() {

    private val TAG = "BuyTenderActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_tender)

        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateTenderId")

            mill_name_edittext.setText(bundle.getString("UpdateTenderMillName"))
            price_edittext.setText(bundle.getString("UpdateTenderPrice"))
        }

        review_button.setOnClickListener {
            val millName = mill_name_edittext.text.toString()
            val price = price_edittext.text.toString()
            val quantity = quantity_edittext.text.toString()

            val intent = Intent(applicationContext, ReviewTenderActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("UpdateTenderId", id)
            intent.putExtra("UpdateTenderMillName", millName)
            intent.putExtra("UpdateTenderPrice", price)
            intent.putExtra("UpdateTenderQuantity", quantity)
            applicationContext.startActivity(intent)

            finish()

        }

        cancel_button.setOnClickListener {
            if(userType == "Seller") {
                val intent = Intent(applicationContext, SellerHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                applicationContext.startActivity(intent)
            } else {
                val intent = Intent(applicationContext, UserHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                applicationContext.startActivity(intent)
            }
        }
    }

}
