package com.example.sugarbroker.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sugarbroker.activity.resale.AddResaleActivity
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.resale.DetailResaleActivity
import com.example.sugarbroker.model.Resale
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class ResaleRecyclerViewAdapter(private val resaleList: MutableList<Resale>, private val context: Context,
                                private val firestoreDB: FirebaseFirestore): RecyclerView.Adapter<ResaleRecyclerViewAdapter.ViewHolder>() {

    private val listResale: ArrayList<Resale>

    init {

        this.listResale = ArrayList<Resale>()
        this.listResale.addAll(resaleList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_resale, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resale= resaleList[position]

        holder.millName.text = resale.millName
        holder.price.text = resale.price

        Glide.with(context).load(resale.resaleUrl.toString())
            .placeholder(R.drawable.photoplaceholder)
            .apply(RequestOptions.circleCropTransform())
            .into(
                holder.tvIcon
            )

        holder.tvIcon.setOnClickListener {
            val mBuilder = AlertDialog.Builder(context)
            val mView = View.inflate(context,R.layout.dialog_custom_layout, null)
            val photoView = mView.findViewById<View>(R.id.imageView) as ImageView
            Glide.with(context).load(resale.resaleUrl)
                .placeholder(R.drawable.photoplaceholder)
                .apply(RequestOptions.circleCropTransform())
                .into(
                    photoView
                )
            mBuilder.setView(mView)
            val mDialog = mBuilder.create()
            mDialog.show()
        }

        holder.edit.setOnClickListener { updateResale(resale) }
        holder.itemView.setOnClickListener { detailResale(resale) }
    }

    override fun getItemCount(): Int {
        Log.d("Size", "Size is ${resaleList.size}")
        return resaleList.size
    }


    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var millName: TextView
        internal var price: TextView
        internal var edit: Button
        internal var tvIcon: CircleImageView

        init {
            millName = view.findViewById(R.id.mill_name_textview)
            price = view.findViewById(R.id.price_textview)

            edit = view.findViewById(R.id.ivEdit)
            tvIcon = view.findViewById(R.id.tvIcon)
        }
    }

    private fun detailResale(resale: Resale) {
        val intent = Intent(context, DetailResaleActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateResaleId", resale.id)
        intent.putExtra("UpdateResaleMillName", resale.millName)
        intent.putExtra("UpdateResalePrice", resale.price)
        intent.putExtra("UpdateResaleAddress", resale.address)
        intent.putExtra("UpdateResaleContact", resale.contact)
        intent.putExtra("UpdateResaleEmail", resale.email)
        intent.putExtra("UpdateResaleUrl", resale.resaleUrl)
        context.startActivity(intent)
    }

    private fun updateResale(resale: Resale) {
        val intent = Intent(context, AddResaleActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateResaleId", resale.id)
        intent.putExtra("UpdateResaleMillName", resale.millName)
        intent.putExtra("UpdateResalePrice", resale.price)
        intent.putExtra("UpdateResaleAddress", resale.address)
        intent.putExtra("UpdateResaleContact", resale.contact)
        intent.putExtra("UpdateResaleEmail", resale.email)
        intent.putExtra("UpdateResaleUrl", resale.resaleUrl)
        context.startActivity(intent)
    }

    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        resaleList.clear()
        if (charText.length == 0) {
            resaleList.addAll(listResale)
        } else {
            for (wp in listResale) {
                if (wp.millName!!.toLowerCase(Locale.getDefault()).contains(charText)) {
                    resaleList.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        resaleList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, resaleList.size)
    }

    fun restoreItem(model: Resale, position: Int) {
        resaleList.add(position, model)
        // notify item added by position
        notifyItemInserted(position)
    }

}

