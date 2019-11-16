package com.example.sugarbroker.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.users.AddUserActivity
import com.example.sugarbroker.activity.users.DetailUserActivity
import com.example.sugarbroker.fragment.UserFragment
import com.example.sugarbroker.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class UserRecyclerViewAdapter(private val usersList: MutableList<User>, private val context: Context,
                              private val firestoreDB: FirebaseFirestore): RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder>() {

    private val arraylist: ArrayList<User>

    init {

        this.arraylist = ArrayList<User>()
        this.arraylist.addAll(usersList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_user, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val users= usersList[position]

        holder.name.text = users.name
        holder.phone.text = users.phone
        holder.tvIcon.text = users.name!!.get(0).toUpperCase().toString()

        holder.edit.setOnClickListener { updateUser(users) }
//        holder.delete.setOnClickListener { deleteUser(users.uid!!, position) }
        holder.itemView.setOnClickListener { detailUser(users) }
    }

    override fun getItemCount(): Int {
        Log.d("Size", "Size is ${usersList.size}")
        return usersList.size
    }


    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var name: TextView
        internal var phone: TextView
        internal var edit: ImageView
        internal var tvIcon: TextView
//        internal var delete: ImageView

        init {
            name = view.findViewById(R.id.name_textview)
            phone = view.findViewById(R.id.phone_textview)

            edit = view.findViewById(R.id.ivEdit)
            tvIcon = view.findViewById(R.id.tvIcon)
        }
    }

    private fun detailUser(user: User) {
        val intent = Intent(context, DetailUserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateUserId", user.uid)
        intent.putExtra("UpdateUserName", user.name)
        intent.putExtra("UpdateUserEmail", user.email)
        intent.putExtra("UpdateUserPassword", user.password)
        intent.putExtra("UpdateUserAddress", user.address)
        intent.putExtra("UpdateUserPhone", user.phone)
        intent.putExtra("UpdateUserType", user.type)
        context.startActivity(intent)
    }

    private fun updateUser(user: User) {
        val intent = Intent(context, AddUserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateUserId", user.uid)
        intent.putExtra("UpdateUserName", user.name)
        intent.putExtra("UpdateUserEmail", user.email)
        intent.putExtra("UpdateUserPassword", user.password)
        intent.putExtra("UpdateUserAddress", user.address)
        intent.putExtra("UpdateUserPhone", user.phone)
        intent.putExtra("UpdateUserType", user.type)
        intent.putExtra("UpdateUserProfileType", "Admin")
        context.startActivity(intent)
    }

//    private fun deleteUser(id: String, position: Int) {
//        firestoreDB.collection("users")
//            .document(id)
//            .delete()
//            .addOnCompleteListener {
//                usersList.removeAt(position)
//                notifyItemRemoved(position)
//                notifyItemRangeChanged(position, usersList.size)
//                Toast.makeText(context, "User has been deleted!", Toast.LENGTH_SHORT).show()
//            }
//    }

    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        usersList.clear()
        if (charText.length == 0) {
            usersList.addAll(arraylist)
        } else {
            for (wp in arraylist) {
                if (wp.name!!.toLowerCase(Locale.getDefault()).contains(charText)) {
                    usersList.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

}

