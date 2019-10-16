package com.example.sugarbroker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sugarbroker.model.Resale
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_resale.*

class AddResaleActivity : AppCompatActivity() {

    private val TAG = "AddResaleActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_resale)

        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateSugarId")
            Toast.makeText(applicationContext, "ID ${id}", Toast.LENGTH_SHORT).show()

            mill_name_edittext.setText(bundle.getString("UpdateSugarMillName"))
            price_edittext.setText(bundle.getString("UpdateSugarPrice"))
        }

        add_button.setOnClickListener {
            val millName = mill_name_edittext.text.toString()
            val price = price_edittext.text.toString()

            if (title.isNotEmpty()) {
                if (id!!.isNotEmpty()) {
                    updateSugar(id!!, millName, price)
                } else {
                    addSugar(millName, price)
                }
            }

            finish()

        }
    }

    private fun updateSugar(id: String, millName: String, price: String) {
        val sugar = Resale(id, millName, price).toMap()

        firestoreDB!!.collection("resale")
            .document(id)
            .set(sugar)
            .addOnSuccessListener {
                Log.e(TAG, "Note document update successful!")
                Toast.makeText(applicationContext, "Note has been updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Note document", e)
                Toast.makeText(applicationContext, "Note could not be updated!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addSugar(millName: String, price: String) {
        val sugar = Resale(millName, price).toMap()

        firestoreDB!!.collection("resale")
            .add(sugar)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "DocumentSnapshot written with ID: " + documentReference.id)
                Toast.makeText(applicationContext, "Note has been added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Note document", e)
                Toast.makeText(applicationContext, "Note could not be added!", Toast.LENGTH_SHORT).show()
            }
    }
}
