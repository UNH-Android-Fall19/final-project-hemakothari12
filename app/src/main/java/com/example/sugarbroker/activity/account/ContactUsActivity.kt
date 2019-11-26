package com.example.sugarbroker.activity.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sugarbroker.R
import kotlinx.android.synthetic.main.activity_contact_us.*


class ContactUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        setUpToolbar()

        callAdmin.setOnClickListener {
            val mobNum = "+919422003575"
            val phoneIntent = Intent(
                Intent.ACTION_DIAL, Uri.fromParts(
                    "tel", mobNum, null
                )
            )
            startActivity(phoneIntent)
        }

        emailAdmin.setOnClickListener {
//            val intent = Intent(Intent.ACTION_SENDTO)
//            intent.data = Uri.parse("mailto:" + “hema.kothari12@gmail.com”)
//            i.putExtra(Intent.EXTRA_EMAIL, new String[]{ "prasana91@gmail.com" });
//            intent.putExtra(Intent.EXTRA_SUBJECT, "")
//            if (intent.resolveActivity(packageManager) != null) {
//                startActivity(intent)
//            }

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.type = "text/html"
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, Array(1) { "jitendrarkothari@gmail.com" })
            intent.putExtra(Intent.EXTRA_SUBJECT, "Inquire about sugar price")
            intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
//            startActivity(Intent.createChooser(intent, "Send Email"))
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
