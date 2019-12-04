package com.example.sugarbroker.model

class User {
    var uid: String? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var address: String? = null
    var phone: String? = null
    var type: String? = null

    // constructor for Add New
    constructor(uid: String, name: String, email: String, password: String, address: String, phone: String, type: String) {
        this.uid = uid
        this.name = name
        this.email = email
        this.password = password
        this.address = address
        this.phone = phone
        this.type = type
    }

    // constructor for Update
    constructor(name: String, email: String, password: String, address: String, phone: String, type: String) {
        this.name = name
        this.email = email
        this.password = password
        this.address = address
        this.phone = phone
        this.type = type
    }

}
