package com.example.sugarbroker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sugarbroker.adapter.SugarRecyclerViewAdapter
import com.example.sugarbroker.model.Sugar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_sugar.*

class SugarActivity : AppCompatActivity() {

    private val TAG = "SugarActivity"

    private var mAdapter: SugarRecyclerViewAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sugar)

        firestoreDB = FirebaseFirestore.getInstance()

        loadSugarList()

        firestoreListener = firestoreDB!!.collection("resale")
            .addSnapshotListener(EventListener { documentSnapshots, e ->
                if (e != null) {
                    Log.e(TAG, "Listen failed!", e)
                    return@EventListener
                }

                val sugarList = mutableListOf<Sugar>()

                for (doc in documentSnapshots!!) {
                    val sugar = doc.toObject(Sugar::class.java)
                    sugar.id = doc.id
                    sugarList.add(sugar)
                }

                mAdapter = SugarRecyclerViewAdapter(sugarList, applicationContext, firestoreDB!!)
                sugar_list.adapter = mAdapter
            })
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    private fun loadSugarList() {
        firestoreDB!!.collection("resale")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val sugarList = mutableListOf<Sugar>()

                    for (doc in task.result!!) {
                        val sugar = doc.toObject<Sugar>(Sugar::class.java)
                        sugar.id = doc.id
                        sugarList.add(sugar)
                    }

                    mAdapter = SugarRecyclerViewAdapter(sugarList, applicationContext, firestoreDB!!)
                    val mLayoutManager = LinearLayoutManager(applicationContext)
                    sugar_list.layoutManager = mLayoutManager
                    sugar_list.itemAnimator = DefaultItemAnimator()
                    sugar_list.adapter = mAdapter
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.search -> {
                Toast.makeText(this, "Implement search", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.logout -> {
                //Implemented Logout
                performLogout()
                return true
            }
            R.id.add -> {
                val intent = Intent(this, AddSugarActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}
