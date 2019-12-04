package com.example.sugarbroker.activity.tender

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.calculatePrice
import com.example.sugarbroker.activity.home.AdminHomeActivity
import com.example.sugarbroker.activity.home.SellerHomeActivity
import com.example.sugarbroker.activity.home.UserHomeActivity
import com.example.sugarbroker.activity.userType
import com.example.sugarbroker.model.Orders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_review_tender.*

class ReviewTenderActivity : AppCompatActivity() {

    private val TAG = "ReviewTenderActivity"

    private var firestoreDB: FirebaseFirestore? = null

    internal var id: String? = ""
    internal var quantity: Int? = 0
    internal var price: Float? = 0f
    internal var quantityPrice: Float? = 0f
    internal var GST: Float? = 0f
    internal var totalPrice: Float? = 0f
    var name: Any? = null
    var address: Any? = null
    var email: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_tender)

        firestoreDB = FirebaseFirestore.getInstance()

        setUpToolbar()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateTenderId")
            Log.d(TAG, "id is ${id}")

            mill_name_textview.setText(bundle.getString("UpdateTenderMillName"))
            price_textview.setText(bundle.getString("UpdateTenderPrice"))
            quantity_textview.setText(bundle.getString("UpdateTenderQuantity"))

            quantity = bundle.getString("UpdateTenderQuantity")!!.toInt()
            price = bundle.getString("UpdateTenderPrice")!!.toFloat()

            val (quantityPrice, GST, totalPrice) = calculatePrice(quantity!!, price!!)

            quantityprice_textview.setText(quantityPrice!!.toString())
            GST_textview.setText(GST.toString())
            totalprice_textview.setText(totalPrice.toString())
        }


        confirm_button.setOnClickListener {
            val millName = mill_name_textview.text.toString()
            val currentUser = FirebaseAuth.getInstance().currentUser
            val uid = FirebaseAuth.getInstance().uid


            Log.d(TAG, "currentUser ${currentUser}")
            Log.d(TAG, "uid ${uid}")

            firestoreDB!!.collection("users").whereEqualTo("uid", uid).get()
                .addOnSuccessListener { documents ->
                    if (documents != null) {
                        for (document in documents) {
                            name = document.get("name")
                            address = document.get("address")
                            email = document.get("email")
                            addOrder(millName, price.toString(), quantity.toString(), quantityPrice.toString(), GST.toString(), totalPrice.toString(), email.toString(), name.toString(), address.toString())

                        }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
            Log.d(TAG, "Name: ${name}")
            Log.d(TAG, "Address: ${address}")


            finish()
        }

        cancel_button.setOnClickListener {
            if(userType == "Seller") {
                val intent = Intent(applicationContext, SellerHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                applicationContext.startActivity(intent)
            }
            if (userType == "User") {
                val intent = Intent(applicationContext, UserHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                applicationContext.startActivity(intent)
            }
            if (userType == "Admin") {
                val intent = Intent(applicationContext, AdminHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                applicationContext.startActivity(intent)
            }
        }
    }

    private fun addOrder(millName: String, price: String, quantity: String, quantityPrice: String, GST: String,
                         totalPrice: String, email: String, name: String, address: String) {
        val order = Orders(millName, price, quantity, quantityPrice, GST, totalPrice, email, name, address, "Open")

        firestoreDB!!.collection("orders")
            .add(order)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "DocumentSnapshot written with ID: " + documentReference.id)
                Toast.makeText(applicationContext, "Order has been added!", Toast.LENGTH_SHORT).show()
                firestoreDB!!.collection("orders").document(documentReference.id).update("id", documentReference.id)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating ID", e) }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Note document", e)
                Toast.makeText(applicationContext, "Order could not be added!", Toast.LENGTH_SHORT).show()
            }
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
}
