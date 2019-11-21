package com.example.sugarbroker.activity.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.sugarbroker.model.Orders
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_order.*
import com.example.sugarbroker.R

class AddOrderActivity : AppCompatActivity() {

    private val TAG = "AddOrderActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""
    var toolbarTitle: String? = "Add Order Details"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_order)

        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateOrderId")
            Toast.makeText(applicationContext, "ID ${id}", Toast.LENGTH_SHORT).show()

            mill_name_edittext.setText(bundle.getString("UpdateOrderMillName"))
            price_edittext.setText(bundle.getString("UpdateOrderPrice"))
            quantity_edittext.setText(bundle.getString("UpdateOrderQuantity"))
            quantityPrice_edittext.setText(bundle.getString("UpdateOrderQuantityPrice"))
            GST_edittext.setText(bundle.getString("UpdateOrderGST"))
            totalPrice_edittext.setText(bundle.getString("UpdateOrderTotalPrice"))
            email_edittext.setText(bundle.getString("UpdateOrderEmail"))
            userName_edittext.setText(bundle.getString("UpdateOrderUsername"))
            userAddress_edittext.setText(bundle.getString("UpdateOrderUserAddress"))
            status_edittext.setText(bundle.getString("UpdateOrderStatus"))
        }

        if (title.isNotEmpty()) {
            if (id!!.isNotEmpty()) {
                add_button.text = "Update"
                toolbarTitle = "Update Order Details"
            } else {
                add_button.text = "Add"
                toolbarTitle = "Add Order Details"
            }
            setUpToolbar(toolbarTitle!!)
        }

        add_button.setOnClickListener {
            val millName = mill_name_edittext.text.toString()
            val price = price_edittext.text.toString()
            val quantity = quantity_edittext.text.toString()
            val quantityPrice = quantityPrice_edittext.text.toString()
            val GST = GST_edittext.text.toString()
            val totalPrice = totalPrice_edittext.text.toString()
            val email = email_edittext.text.toString()
            val userName = userName_edittext.text.toString()
            val userAddress = userAddress_edittext.text.toString()
            val status = status_edittext.text.toString()

            if (title.isNotEmpty()) {
                if (id!!.isNotEmpty()) {
                    updateOrder(id!!, millName, price, quantity, quantityPrice, GST, totalPrice, email, userName, userAddress, status)
                } else {
                    addOrder(millName, price, quantity, quantityPrice, GST, totalPrice, email, userName, userAddress, status)
                }
            }


            finish()

        }
    }

    private fun updateOrder(id: String, millName: String, price: String, quantity: String, quantityPrice: String, GST: String,
                            totalPrice: String, email: String, userName: String, userAddress: String, status: String) {
        val order = Orders(id, millName, price, quantity, quantityPrice, GST, totalPrice, email, userName, userAddress, status)

        firestoreDB!!.collection("orders")
            .document(id)
            .set(order)
            .addOnSuccessListener {
                Log.e(TAG, "Order document update successful!")
                Toast.makeText(applicationContext, "Order has been updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Order document", e)
                Toast.makeText(applicationContext, "Order could not be updated!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addOrder(millName: String, price: String, quantity: String, quantityPrice: String, GST: String,
                         totalPrice: String, email: String, userName: String, userAddress: String, status: String) {
        val order = Orders(millName, price, quantity, quantityPrice, GST, totalPrice, email, userName, userAddress, status)

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

    private fun setUpToolbar(toolbarTitle: String) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = toolbarTitle
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
