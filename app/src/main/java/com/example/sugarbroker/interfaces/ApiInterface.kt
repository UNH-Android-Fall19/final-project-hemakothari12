package com.example.sugarbroker.interfaces

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    //AIzaSyALmJzvJKj9yK23lUj7-M4e8CmfnWENx2s this is legacy server key
    @Headers(
        "Authorization: key=AIzaSyC-BCv8NJAFr4uUEJ9t0-RzREeU2kqPBfA\n",
        "Content-Type:application/json"
    )
    @POST("fcm/send")
    fun sendChatNotification(@Body requestBody: RequestBody): Call<ResponseBody>
}