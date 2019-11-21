package com.example.sugarbroker.activity.resale

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sugarbroker.R
import com.example.sugarbroker.fragment.ResaleFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_buy_resale.*


class BuyResaleActivity : AppCompatActivity() {

    private val TAG = "BuyResaleActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_resale)

        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateResaleId")
            Toast.makeText(applicationContext, "ID ${id}", Toast.LENGTH_SHORT).show()

            mill_name_edittext.setText(bundle.getString("UpdateResaleMillName"))
            price_edittext.setText(bundle.getString("UpdateResalePrice"))
        }

        review_button.setOnClickListener {
            val millName = mill_name_edittext.text.toString()
            val price = price_edittext.text.toString()
            val quantity = quantity_edittext.text.toString()

            val intent = Intent(applicationContext, ReviewResaleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("UpdateResaleId", id)
            intent.putExtra("UpdateResaleMillName", millName)
            intent.putExtra("UpdateResalePrice", price)
            intent.putExtra("UpdateResaleQuantity", quantity)
            applicationContext.startActivity(intent)

            finish()

        }

        cancel_button.setOnClickListener {
//            val intent = Intent(applicationContext, supportFragmentManager.popBackStack())
//            applicationContext.startActivity(intent)


        }
    }

}
