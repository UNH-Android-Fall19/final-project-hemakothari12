package com.example.sugarbroker.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sugarbroker.R
import com.example.sugarbroker.model.Tender
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_tender.*

class AddTenderActivity : AppCompatActivity() {

    private val TAG = "AddTenderActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tender)

        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateTenderId")
            Toast.makeText(applicationContext, "ID ${id}", Toast.LENGTH_SHORT).show()

            mill_name_edittext.setText(bundle.getString("UpdateTenderMillName"))
            price_edittext.setText(bundle.getString("UpdateTenderPrice"))
        }

        add_button.setOnClickListener {
            val millName = mill_name_edittext.text.toString()
            val price = price_edittext.text.toString()

            if (title.isNotEmpty()) {
                if (id!!.isNotEmpty()) {
                    updateTender(id!!, millName, price)
                } else {
                    addTender(millName, price)
                }
            }

            finish()

        }
    }

    private fun updateTender(id: String, millName: String, price: String) {
        val tender = Tender(id, millName, price).toMap()

        firestoreDB!!.collection("tender")
            .document(id)
            .set(tender)
            .addOnSuccessListener {
                Log.e(TAG, "Tender document update successful!")
                Toast.makeText(applicationContext, "Tender has been updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Tender document", e)
                Toast.makeText(applicationContext, "Tender could not be updated!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addTender(millName: String, price: String) {
        val tender = Tender(millName, price).toMap()

        firestoreDB!!.collection("tender")
            .add(tender)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "DocumentSnapshot written with ID: " + documentReference.id)
                Toast.makeText(applicationContext, "Tender has been added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Note document", e)
                Toast.makeText(applicationContext, "Tender could not be added!", Toast.LENGTH_SHORT).show()
            }
    }
}
