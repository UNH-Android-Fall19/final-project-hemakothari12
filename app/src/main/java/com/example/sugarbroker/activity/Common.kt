package com.example.sugarbroker.activity


var userType: String? = null
var userName: String? = null
var userEmail: String? = null

fun calculatePrice(quantity: Int, price: Float): Calculate {
    val quantityPrice = quantity!! * price!!
    val GST = (0.18 * quantityPrice!!).toFloat()
    val totalPrice = quantityPrice!! + GST!!

    return Calculate(quantityPrice.toFloat(), GST, totalPrice)
}

data class Calculate(val quantityPrice: Float, val GST: Float, val totalPrice: Float)