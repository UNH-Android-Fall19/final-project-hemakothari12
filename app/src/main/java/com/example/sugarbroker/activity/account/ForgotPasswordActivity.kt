package com.example.sugarbroker.activity.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.sugarbroker.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setUpToolbar()

        auth = FirebaseAuth.getInstance()

        submit_button.setOnClickListener {

            var email = email_edittext.text.toString()

            if (email_edittext.text.isNullOrBlank() || TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter valid Email", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    if (it.isSuccessful) {
                        Toast.makeText(this, "Email sent successfully", Toast.LENGTH_SHORT).show()

                        intent = Intent(applicationContext, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to send Email: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.forgot_password)
        actionBar.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
    }
}
