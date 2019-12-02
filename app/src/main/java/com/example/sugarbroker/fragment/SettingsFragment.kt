package com.example.sugarbroker.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.adapter.UserOrdersRecyclerViewAdapter
import com.example.sugarbroker.model.Orders
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    private val TAG = "SettingsFragment"

    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_settings, container, false)

        val sharedPreferences = root!!.context.getSharedPreferences("SugarBroker", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        val notificationValue = sharedPreferences.getBoolean("NOTIFICATION", false)
        val pushnotification = root!!.findViewById<View>(R.id.pushnotification) as Switch

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


        return root
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