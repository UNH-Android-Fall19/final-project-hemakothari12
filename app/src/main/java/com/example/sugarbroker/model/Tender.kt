package com.example.sugarbroker.model

import android.net.Uri
import java.util.HashMap

class Tender {
    var id: String? = null
    var millName: String? = null
    var price: String? = null
    var address: String? = null
    var contact: String? = null
//    var tenderUrl: Uri? = null

    constructor() {}

    constructor(id: String, millName: String, price: String, address: String, contact: String) {
        this.id = id
        this.millName = millName
        this.price = price
        this.address = address
        this.contact = contact
//        this.tenderUrl = tenderUrl
    }

    constructor(millName: String, price: String, address: String, contact: String) {
        this.millName = millName
        this.price = price
        this.price = price
        this.address = address
        this.contact = contact
//        this.tenderUrl = tenderUrl
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("millName", millName!!)
        result.put("price", price!!)

        return result
    }

}