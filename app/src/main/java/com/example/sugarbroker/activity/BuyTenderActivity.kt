package com.example.sugarbroker.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sugarbroker.R
import com.example.sugarbroker.model.Tender
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_buy_tender.*
import com.mikelau.croperino.CroperinoConfig
import com.mikelau.croperino.Croperino
import com.mikelau.croperino.CroperinoFileUtil
import kotlinx.android.synthetic.main.activity_buy_tender.sugar_image
import kotlinx.android.synthetic.main.activity_detail_tender.*
import android.content.Context


class BuyTenderActivity : AppCompatActivity() {

    private val TAG = "AddTenderActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_tender)

        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateTenderId")
            Toast.makeText(applicationContext, "ID ${id}", Toast.LENGTH_SHORT).show()

            mill_name_edittext.setText(bundle.getString("UpdateTenderMillName"))
            price_edittext.setText(bundle.getString("UpdateTenderPrice"))
//            sugar_image.setText(bundle.getString("UpdateTenderUrl"))
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
    }

}
