package com.example.sugarbroker.activity.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.sugarbroker.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail_order.*

class DetailOrderActivity : AppCompatActivity() {

    private val TAG = "DetailOrderActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_order)

        firestoreDB = FirebaseFirestore.getInstance()

        setUpToolbar()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateOrderId")
            mill_name_textview.setText(bundle.getString("UpdateOrderMillName"))
            price_textview.setText(bundle.getString("UpdateOrderPrice"))
            quantity_textview.setText(bundle.getString("UpdateOrderQuantity"))
            quantityPrice_textview.setText(bundle.getString("UpdateOrderQuantityPrice"))
            GST_textview.setText(bundle.getString("UpdateOrderGST"))
            totalPrice_textview.setText(bundle.getString("UpdateOrderTotalPrice"))
            email_textview.setText(bundle.getString("UpdateOrderEmail"))
            userName_textview.setText(bundle.getString("UpdateOrderUsername"))
            userAddress_textview.setText(bundle.getString("UpdateOrderUserAddress"))
            status_textview.setText(bundle.getString("UpdateOrderStatus"))
            tvIcon.setText(bundle.getString("UpdateOrderUsername")!!.get(0).toUpperCase().toString())

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
