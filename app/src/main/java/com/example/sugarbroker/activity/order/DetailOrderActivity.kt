package com.example.sugarbroker.activity.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.userType
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

        if(userType == "User" || userType == "Seller") {
            editOrder.visibility = View.GONE
        }else {
            editOrder.visibility = View.VISIBLE
        }

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

        editOrder.setOnClickListener { updateOrder() }
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

    private fun updateOrder() {
        val intent = Intent(applicationContext, AddOrderActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateOrderId", id)
        intent.putExtra("UpdateOrderMillName", mill_name_textview.text.toString())
        intent.putExtra("UpdateOrderPrice", price_textview.text.toString())
        intent.putExtra("UpdateOrderQuantity", quantity_textview.text.toString())
        intent.putExtra("UpdateOrderQuantityPrice", quantityPrice_textview.text.toString())
        intent.putExtra("UpdateOrderGST", GST_textview.text.toString())
        intent.putExtra("UpdateOrderTotalPrice", totalPrice_textview.text.toString())
        intent.putExtra("UpdateOrderEmail", email_textview.text.toString())
        intent.putExtra("UpdateOrderUsername", userName_textview.text.toString())
        intent.putExtra("UpdateOrderUserAddress", userAddress_textview.text.toString())
        intent.putExtra("UpdateOrderStatus", status_textview.text.toString())
        applicationContext.startActivity(intent)
    }

}
