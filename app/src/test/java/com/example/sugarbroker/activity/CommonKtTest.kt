package com.example.sugarbroker.activity

import org.junit.Assert.*
import org.junit.Test

class CommonKtTest {


    @Test
    fun calculateprice_success() {
        val quantity = 10
        val price = 10f
        val totalPriceExpected = 118f
        var quantityPriceExpected: Float = 100f
        var GSTExpected: Float = 18f

        val (quantityPrice, GST, totalPrice) = com.example.sugarbroker.activity.calculatePrice(
            quantity!!,
            price!!
        )
        assertEquals(totalPriceExpected, totalPrice)
        assertEquals(quantityPriceExpected, quantityPrice)
        assertEquals(GSTExpected, GST)

    }
}