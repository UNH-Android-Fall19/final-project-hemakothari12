package com.example.sugarbroker.activity

import com.example.sugarbroker.model.SendNotificationModel
import com.google.gson.annotations.SerializedName

class RequestNotificaton {

    @SerializedName("token")
    var token: String? = null

    @SerializedName("notification")
    var sendNotificationModel: SendNotificationModel? = null
}