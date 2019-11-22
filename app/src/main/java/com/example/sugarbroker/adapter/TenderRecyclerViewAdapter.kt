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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarbroker.R
import com.example.sugarbroker.activity.tender.AddTenderActivity
import com.example.sugarbroker.activity.tender.DetailTenderActivity
import com.example.sugarbroker.model.Tender
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class TenderRecyclerViewAdapter(private val tenderList: MutableList<Tender>, private val context: Context,
                                private val firestoreDB: FirebaseFirestore): RecyclerView.Adapter<TenderRecyclerViewAdapter.ViewHolder>() {

    private val listTender: ArrayList<Tender>

    init {

        this.listTender = ArrayList<Tender>()
        this.listTender.addAll(tenderList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tender, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tender= tenderList[position]

        holder.millName.text = tender.millName
        holder.price.text = tender.price

        Glide.with(context).load(tender.tenderUrl.toString())
            .placeholder(R.drawable.photoplaceholder)
            .apply(RequestOptions.circleCropTransform())
            .into(
                holder.tvIcon
            )

        holder.tvIcon.setOnClickListener {
            val mBuilder = AlertDialog.Builder(context)
            val mView = View.inflate(context,R.layout.dialog_custom_layout, null)
            val photoView = mView.findViewById<View>(R.id.imageView) as ImageView
            Glide.with(context).load(tender.tenderUrl)
                .placeholder(R.drawable.photoplaceholder)
                .apply(RequestOptions.circleCropTransform())
                .into(
                    photoView
                )
            mBuilder.setView(mView)
            val mDialog = mBuilder.create()
            mDialog.show()
        }

        holder.edit.setOnClickListener { updateTender(tender) }
        holder.itemView.setOnClickListener { detailTender(tender) }
    }

    override fun getItemCount(): Int {
        Log.d("Size", "Size is ${tenderList.size}")
        return tenderList.size
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

    private fun detailTender(tender: Tender) {
        val intent = Intent(context, DetailTenderActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateTenderId", tender.id)
        intent.putExtra("UpdateTenderMillName", tender.millName)
        intent.putExtra("UpdateTenderPrice", tender.price)
        intent.putExtra("UpdateTenderAddress", tender.address)
        intent.putExtra("UpdateTenderContact", tender.contact)
        intent.putExtra("UpdateTenderUrl", tender.tenderUrl)
        context.startActivity(intent)
    }

    private fun updateTender(tender: Tender) {
        val intent = Intent(context, AddTenderActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateTenderId", tender.id)
        intent.putExtra("UpdateTenderMillName", tender.millName)
        intent.putExtra("UpdateTenderPrice", tender.price)
        intent.putExtra("UpdateTenderAddress", tender.address)
        intent.putExtra("UpdateTenderContact", tender.contact)
        intent.putExtra("UpdateTenderUrl", tender.tenderUrl)
        context.startActivity(intent)
    }

    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        tenderList.clear()
        if (charText.length == 0) {
            tenderList.addAll(listTender)
        } else {
            for (wp in listTender) {
                if (wp.millName!!.toLowerCase(Locale.getDefault()).contains(charText)) {
                    tenderList.add(wp)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        tenderList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tenderList.size)
    }

    fun restoreItem(model: Tender, position: Int) {
        tenderList.add(position, model)
        // notify item added by position
        notifyItemInserted(position)
    }

}

