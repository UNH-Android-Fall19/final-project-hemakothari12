package com.example.sugarbroker.model

class Orders {
    var id: String? = null
    var millName: String? = null
    var price: String? = null
    var quantity: String? = null
    var quantityPrice: String? = null
    var GST: String? = null
    var totalPrice: String? = null
    var email: String? = null
    var name: String? = null
    var address: String? = null
    var status: String? = null


    // constructor for Add New
    constructor(id: String, millName: String, price: String, quantity: String, quantityPrice: String, GST: String,
                totalPrice: String, email: String, name: String, address: String, status: String) {
        this.id = id
        this.millName = millName
        this.price = price
        this.quantity = quantity
        this.quantityPrice = quantityPrice
        this.GST = GST
        this.totalPrice = totalPrice
        this.email = email
        this.name = name
        this.address = address
        this.status = status
    }

    // constructor for Update
    constructor(millName: String, price: String, quantity: String, quantityPrice: String, GST: String,
                totalPrice: String, email: String, name: String, address: String, status: String) {
        this.millName = millName
        this.price = price
        this.quantity = quantity
        this.quantityPrice = quantityPrice
        this.GST = GST
        this.totalPrice = totalPrice
        this.email = email
        this.name = name
        this.address = address
        this.status = status
    }

}