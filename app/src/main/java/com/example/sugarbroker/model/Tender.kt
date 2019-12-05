package com.example.sugarbroker.model

class Tender {
    var id: String? = null
    var millName: String? = null
    var price: String? = null
    var address: String? = null
    var contact: String? = null
    var email: String? = null
    var tenderUrl: String? = null

    constructor() {}

    // constructor for Add New
    constructor(id: String, millName: String, price: String, address: String, contact: String, email:String, url: String? = null) {
        this.id = id
        this.millName = millName
        this.price = price
        this.address = address
        this.contact = contact
        this.email = email
        this.tenderUrl = url
    }

    // constructor for Update
    constructor(millName: String, price: String, address: String, contact: String, email:String, url: String? = null) {
        this.millName = millName
        this.price = price
        this.address = address
        this.contact = contact
        this.email = email
        this.tenderUrl = url
    }
}