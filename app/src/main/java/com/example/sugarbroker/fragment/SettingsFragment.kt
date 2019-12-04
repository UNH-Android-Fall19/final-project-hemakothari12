package com.example.sugarbroker.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.account.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class SettingsFragment : Fragment() {

    private val TAG = "SettingsFragment"
    private var firestoreDB: FirebaseFirestore? = null

    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_settings, container, false)

        firestoreDB = FirebaseFirestore.getInstance()
        val sharedPreferences = root!!.context.getSharedPreferences("SugarBroker", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val notificationValue = sharedPreferences.getBoolean("NOTIFICATION", false)
        val pushnotification = root!!.findViewById<View>(R.id.pushnotification) as Switch
        val delete_button = root!!.findViewById<View>(R.id.delete_button) as Button

        if (notificationValue == true) {
            pushnotification.isChecked = true
        } else {
            pushnotification.isChecked = false
        }

        pushnotification.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                subscribeToPushNotifications()
                editor.putBoolean("NOTIFICATION", true)
                editor.apply()
            }else {
                unsubscribeToPushNotifications()
                editor.putBoolean("NOTIFICATION", false)
                editor.apply()
            }
        }

        delete_button.setOnClickListener { deleteAccount() }

        return root
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = FirebaseAuth.getInstance().uid
        Log.d(TAG, "uid is for delete ${uid}")

        performLogout()

        firestoreDB!!.collection("users").document(uid!!).delete()
            .addOnCompleteListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User account deleted.")
                }
            }


    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(activity!!.applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    //this enables push notification to sugarbroker topic
    private fun subscribeToPushNotifications(){
        FirebaseMessaging.getInstance().subscribeToTopic("sugarbroker")
            .addOnCompleteListener { task ->
            }
    }

    //this disables push notification to sugarbroker topic.
    private fun unsubscribeToPushNotifications(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic("sugarbroker")
            .addOnCompleteListener { task ->
            }
    }
}