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
import com.example.sugarbroker.model.Orders
import com.example.sugarbroker.model.Tender
import com.google.firebase.firestore.FirebaseFirestore
import com.mikelau.croperino.CroperinoConfig
import com.mikelau.croperino.Croperino
import com.mikelau.croperino.CroperinoFileUtil
import kotlinx.android.synthetic.main.activity_review_tender.*
import java.util.*

class ReviewTenderActivity : AppCompatActivity() {

    private val TAG = "ReviewTenderActivity"

    private var firestoreDB: FirebaseFirestore? = null

    internal var id: String? = ""
    internal var quantity: Int? = 0
    internal var price: Int? = 0
    internal var quantityPrice: Int? = 0
    internal var GST: Float? = 0f
    internal var totalPrice: Float? = 0f
    internal var email: String? = ""
    internal var address: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_tender)

        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateTenderId")
            Log.d(TAG, "id is ${id}")
            Toast.makeText(applicationContext, "ID ${id}", Toast.LENGTH_SHORT).show()

            mill_name_textview.setText(bundle.getString("UpdateTenderMillName"))
            price_textview.setText(bundle.getString("UpdateTenderPrice"))
            quantity_textview.setText(bundle.getString("UpdateTenderQuantity"))

            quantity = bundle.getString("UpdateTenderQuantity")!!.toInt()
            price = bundle.getString("UpdateTenderPrice")!!.toInt()

            quantityPrice = quantity!! * price!!
            GST = (0.18 * quantityPrice!!).toFloat()
            totalPrice = quantityPrice!! + GST!!

//            price_textview.setText(price)
//            quantity_textview.setText(quantity)
            quantityprice_textview.text = quantityPrice.toString()
            GST_textview.text = GST.toString()
            totalprice_textview.setText(totalPrice.toString())
        }


        confirm_button.setOnClickListener {
            val millName = mill_name_textview.text.toString()

            if (title.isNotEmpty()) {
                if (id!!.isNotEmpty()) {
//                    updateUser(uid!!, name, email, password, address, phone, type)
//                } else {
                    addOrder(millName, price.toString(), quantity.toString(), quantityPrice.toString(), GST.toString(), totalPrice.toString(), email.toString(), address.toString())
                }
            }


            finish()

        }
    }

    private fun addOrder(millName: String, price: String, quantity: String, quantityPrice: String, GST: String, totalPrice: String, email: String, address: String) {
        val order = Orders(millName, price, quantity, quantityPrice, GST, totalPrice, email, address, "Open")

        firestoreDB!!.collection("orders")
            .add(order)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "DocumentSnapshot written with ID: " + documentReference.id)
                Toast.makeText(applicationContext, "Order has been added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Note document", e)
                Toast.makeText(applicationContext, "Order could not be added!", Toast.LENGTH_SHORT).show()
            }
    }
}
