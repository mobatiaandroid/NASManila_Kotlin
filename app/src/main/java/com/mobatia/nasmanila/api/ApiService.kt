package com.mobatia.nasmanila.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("api/parent_signup")
    fun signUp(
        @Field("access_token") accessToken: String,
        @Field("email") email: String,
        @Field("deviceid") deviceID: String,
        @Field("devicetype") deviceType: String
    ): Call<ResponseBody>
    @FormUrlEncoded
    @POST("api/forgotpassword")
    fun forgotPassword(
        @Field("access_token") accessToken: String,
        @Field("email") email: String,
        @Field("deviceid") deviceID: String,
        @Field("devicetype") deviceType: String
    ): Call<ResponseBody>
    @FormUrlEncoded
    @POST("oauth/access_token")
    fun accessToken(
        @Field("grant_type") grantType: String,
        @Field("client_id") clientID: String,
        @Field("client_secret") clientSecret: String,
        @Field("username") userName: String,
        @Field("password") password: String
    ): Call<ResponseBody>
    @FormUrlEncoded
    @POST("api/login")
    fun login(
        @Field("access_token") accessToken: String,
        @Field("email")email: String,
        @Field("password")password: String,
        @Field("deviceid")deviceID: String,
        @Field("devicetype")deviceType: String
    ): Call<ResponseBody>

}
