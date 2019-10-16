package com.example.sugarbroker.model

import java.util.HashMap

class Resale {
    var id: String? = null
    var millName: String? = null
    var price: String? = null

    constructor() {}

    constructor(id: String, millName: String, price: String) {
        this.id = id
        this.millName = millName
        this.price = price
    }

    constructor(millName: String, price: String) {
        this.millName = millName
        this.price = price
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("millName", millName!!)
        result.put("price", price!!)

        return result
    }

}