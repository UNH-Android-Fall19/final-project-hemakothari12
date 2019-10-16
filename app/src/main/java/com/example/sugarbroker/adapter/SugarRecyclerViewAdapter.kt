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
import com.example.sugarbroker.AddSugarActivity
import com.example.sugarbroker.R
import com.example.sugarbroker.SugarActivity
import com.example.sugarbroker.model.Sugar
import com.google.firebase.firestore.FirebaseFirestore

class SugarRecyclerViewAdapter(private val sugarList: MutableList<Sugar>, private val context: Context,
                               private val firestoreDB: FirebaseFirestore): RecyclerView.Adapter<SugarRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_sugar, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sugar= sugarList[position]

        holder.millName.text = sugar.millName
        holder.price.text = sugar.price

        holder.edit.setOnClickListener { updateSugar(sugar) }
        holder.delete.setOnClickListener { deleteSugar(sugar.id!!, position) }
    }

    override fun getItemCount(): Int {
        Log.d("Size", "Size is ${sugarList.size}")
        return sugarList.size
    }


    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var millName: TextView
        internal var price: TextView
        internal var edit: ImageView
        internal var delete: ImageView

        init {
            millName = view.findViewById(R.id.mill_name_textview)
            price = view.findViewById(R.id.price_textview)

            edit = view.findViewById(R.id.ivEdit)
            delete = view.findViewById(R.id.ivDelete)
        }
    }

    private fun updateSugar(sugar: Sugar) {
        val intent = Intent(context, AddSugarActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateSugarId", sugar.id)
        intent.putExtra("UpdateSugarMillName", sugar.millName)
        intent.putExtra("UpdateSugarPrice", sugar.price)
        context.startActivity(intent)
    }

    private fun deleteSugar(id: String, position: Int) {
        firestoreDB.collection("resale")
            .document(id)
            .delete()
            .addOnCompleteListener {
                sugarList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, sugarList.size)
                Toast.makeText(context, "Note has been deleted!", Toast.LENGTH_SHORT).show()
            }
    }

}

