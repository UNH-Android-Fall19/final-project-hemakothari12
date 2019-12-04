package com.example.sugarbroker.activity.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.home.AdminHomeActivity
import com.example.sugarbroker.activity.home.SellerHomeActivity
import com.example.sugarbroker.activity.home.UserHomeActivity
import com.example.sugarbroker.activity.userEmail
import com.example.sugarbroker.activity.userType
import com.example.sugarbroker.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private var progressBar: ProgressBar? = null
    lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        progressBar = findViewById<ProgressBar>(R.id.progress_Bar) as ProgressBar
        progressBar!!.visibility = View.GONE

        val createAccount = "Don't have an account? " + "<font><b>" + "Sign up" + "<b></font>"
        newuser_textview.setText(Html.fromHtml(createAccount))

        val signIn = findViewById<View>(R.id.googleSignIn) as SignInButton
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signIn.setOnClickListener {
            login_email_edittext.visibility = View.GONE
            login_password_edittext.visibility = View.GONE
            login_button.visibility = View.GONE
            forgotpassword_textview.visibility = View.GONE
            newuser_textview.visibility = View.GONE
            signIn.visibility = View.GONE
            progressBar!!.visibility = View.VISIBLE
            signInGoogle()
        }

        login_button.setOnClickListener {
            if(TextUtils.isEmpty(login_email_edittext.text) || TextUtils.isEmpty(login_password_edittext.text)) {
                Toast.makeText(applicationContext, "Please Enter Username and Password", Toast.LENGTH_SHORT).show()
            }else {
                login_email_edittext.visibility = View.GONE
                login_password_edittext.visibility = View.GONE
                login_button.visibility = View.GONE
                forgotpassword_textview.visibility = View.GONE
                newuser_textview.visibility = View.GONE
                signIn.visibility = View.GONE
                progressBar!!.visibility = View.VISIBLE
                performLogin()
            }
        }

        newuser_textview.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotpassword_textview.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Google sign in failed ${e}", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Google sign in failed ${e}")
                }
        }else {
            Toast.makeText(this, "Problem in execution order :(", Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val db = FirebaseFirestore.getInstance()
                val uid = FirebaseAuth.getInstance().uid ?: ""
                val documentRef = db.collection("users").document(uid)
                documentRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val value = document.getString("type")
                            Log.d(TAG, "DocumentSnapshot data: ${document.data}")

                            if (value == "Admin") {
                                Log.d(TAG, "User Logged in is Admin")
                                userType = "Admin"
                                progressBar!!.visibility = View.GONE
                                intent = Intent(applicationContext, AdminHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            } else if (value == "Seller") {
                                Log.d(TAG, "User Logged in is Seller")
                                userType = "Seller"
                                progressBar!!.visibility = View.GONE
                                intent = Intent(applicationContext, SellerHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("LoggedInUserEmail",login_email_edittext.text.toString())
                                startActivity(intent)
                            } else {
                                Log.d(TAG, "User Logged in is User")
                                saveUserDetailsToFirebase(acct!!)
                                userType = "User"
                                userEmail = acct.email
                                progressBar!!.visibility = View.GONE
                                intent = Intent(applicationContext, UserHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }

                        } else {
                            Log.d(TAG, "No document found")

                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveUserDetailsToFirebase(account: GoogleSignInAccount) {

        //Firestore database

        val uid = FirebaseAuth.getInstance().uid ?: ""

        val db = FirebaseFirestore.getInstance()

        val user = User(
            uid,
            account.displayName!!,
            account.email!!,
            "",
            "",
            "",
            "User"
        )

        //Add document with specific ID
        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun performLogin() {
        var email = login_email_edittext.text.toString()
        var password = login_password_edittext.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // Fetch User type
                val db = FirebaseFirestore.getInstance()
                val uid = FirebaseAuth.getInstance().uid ?: ""
                val documentRef = db.collection("users").document(uid)
                documentRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val value = document.getString("type")
                            Log.d(TAG, "DocumentSnapshot data: ${document.data}")

                            if (value == "Admin") {
                                Log.d(TAG, "User Logged in is Admin")
                                userType = "Admin"
                                progressBar!!.visibility = View.GONE
                                intent = Intent(applicationContext, AdminHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            } else if (value == "Seller") {
                                Log.d(TAG, "User Logged in is Seller")
                                userType = "Seller"
                                progressBar!!.visibility = View.GONE
                                intent = Intent(applicationContext, SellerHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("LoggedInUserEmail",login_email_edittext.text.toString())
                                startActivity(intent)
                            } else {
                                Log.d(TAG, "User Logged in is User")
                                userType = "User"
                                progressBar!!.visibility = View.GONE
                                intent = Intent(applicationContext, UserHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("LoggedInUserEmail",login_email_edittext.text.toString())
                                startActivity(intent)
                            }

                        } else {
                            Log.d(TAG, "No document found")

                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Failed to fetch document: ", exception)

                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to Login: ${it.message}")

                Toast.makeText(this, "Invalid username and password", Toast.LENGTH_SHORT).show()
            }
    }
}
