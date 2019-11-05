package com.example.sugarbroker.activity.tender

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sugarbroker.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail_tender.*

class DetailTenderActivity : AppCompatActivity() {

    private val TAG = "AddTenderActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tender)

        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateTenderId")
            Toast.makeText(applicationContext, "ID ${id}", Toast.LENGTH_SHORT).show()

            mill_name_textview.setText(bundle.getString("UpdateTenderMillName"))
            price_textview.setText(bundle.getString("UpdateTenderPrice"))
            milladdress_textview.setText(bundle.getString("UpdateTenderAddress"))
            millcontact_textview.setText(bundle.getString("UpdateTenderContact"))
            Glide.with(this@DetailTenderActivity).load(bundle.getString("UpdateTenderUrl"))
                .placeholder(R.drawable.photoplaceholder)
                .apply(RequestOptions.circleCropTransform())
                .into(
                    detail_sugar_image
                )
        }

        buy_button.setOnClickListener {
            val millName = mill_name_textview.text.toString()
            val price = price_textview.text.toString()

            val intent = Intent(applicationContext, BuyTenderActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("UpdateTenderId", id)
            intent.putExtra("UpdateTenderMillName", millName)
            intent.putExtra("UpdateTenderPrice", price)
            applicationContext.startActivity(intent)

            finish()

        }
    }

}
