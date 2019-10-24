package com.example.sugarbroker.model

import java.util.HashMap

class User {
    var uid: String? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var address: String? = null
    var phone: String? = null
    var type: String? = null

    constructor() {}

    constructor(uid: String, name: String, email: String, password: String, address: String, phone: String, type: String) {
        this.uid = uid
        this.name = name
        this.email = email
        this.password = password
        this.address = address
        this.phone = phone
        this.type = type
    }

    constructor(name: String, email: String, password: String, address: String, phone: String, type: String) {
        this.name = name
        this.email = email
        this.password = password
        this.address = address
        this.phone = phone
        this.type = type
    }

    fun toMap(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result.put("name", name!!)
        result.put("email", email!!)
//        result.put("password", password!!)
        result.put("address", address!!)
        result.put("phone", phone!!)
        result.put("type", type!!)

        return result
    }

}
