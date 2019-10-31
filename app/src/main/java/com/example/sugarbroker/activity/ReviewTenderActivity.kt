package com.example.sugarbroker.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sugarbroker.R
import com.example.sugarbroker.model.Orders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_review_tender.*

class ReviewTenderActivity : AppCompatActivity() {

    private val TAG = "ReviewTenderActivity"

    private var firestoreDB: FirebaseFirestore? = null

    internal var id: String? = ""
    internal var quantity: Int? = 0
    internal var price: Int? = 0
    internal var quantityPrice: Int? = 0
    internal var GST: Float? = 0f
    internal var totalPrice: Float? = 0f
//    internal var name: Any? = ""
//    internal var address: Any? = ""

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

            quantityprice_textview.text = quantityPrice.toString()
            GST_textview.text = GST.toString()
            totalprice_textview.setText(totalPrice.toString())
        }


        confirm_button.setOnClickListener {
            val millName = mill_name_textview.text.toString()
            val currentUser = FirebaseAuth.getInstance().currentUser
            val uid = FirebaseAuth.getInstance().uid
            var name: Any? = null
            var address: Any? = null

            Log.d(TAG, "currentUser ${currentUser}")
            Log.d(TAG, "uid ${uid}")

            firestoreDB!!.collection("users").whereEqualTo("uid", uid).get()
                .addOnSuccessListener { documents ->
                    if (documents != null) {
                        for (document in documents) {
//                            Log.d(TAG, "${document.id} => ${document.data}")
//                            Log.d(TAG, "DocumentSnapshot data Name: ${document.get("name")}")
//                            Log.d(TAG, "DocumentSnapshot data Name: ${document.data}")
//                            Log.d(TAG, "DocumentSnapshot data Address: ${document.get("address")}")
                            name = document.get("name")
                            address = document.get("address")
//                            Log.d(TAG, "Name: ${name}")
//                            Log.d(TAG, "Address: ${address}")
                            addOrder(millName, price.toString(), quantity.toString(), quantityPrice.toString(), GST.toString(), totalPrice.toString(), name.toString(), address.toString())

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
    }

    private fun addOrder(millName: String, price: String, quantity: String, quantityPrice: String, GST: String, totalPrice: String, name: String, address: String) {
        val order = Orders(millName, price, quantity, quantityPrice, GST, totalPrice, name, address, "Open")

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
}
