package com.example.sugarbroker.model


class Resale {
    var id: String? = null
    var millName: String? = null
    var price: String? = null
    var address: String? = null
    var contact: String? = null
    var email: String? = null
    var resaleUrl: String? = null

    constructor() {}

    constructor(id: String, millName: String, price: String, address: String, contact: String, email: String, url: String? = null) {
        this.id = id
        this.millName = millName
        this.price = price
        this.address = address
        this.contact = contact
        this.email = email
        this.resaleUrl = url
    }

    constructor(millName: String, price: String, address: String, contact: String, email: String, url: String? = null) {
        this.millName = millName
        this.price = price
        this.address = address
        this.contact = contact
        this.email = email
        this.resaleUrl = url
    }

}