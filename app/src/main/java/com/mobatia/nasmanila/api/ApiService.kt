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
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("deviceid") deviceID: String,
        @Field("devicetype") deviceType: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/home_banner_images")
    fun getBannerImages(
        @Field("access_token") accessToken: String,
        @Field("app_version") appVersion: String,
        @Field("users_id") userID: String,
        @Field("devicetype") deviceType: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/logout")
    fun logOut(
        @Field("access_token") accessToken: String,
        @Field("users_id") userID: String,
        @Field("deviceid") deviceID: String,
        @Field("devicetype") deviceType: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/getnotifications")
    fun pushNotificationsCall(
        @Field("access_token") accessToken: String,
        @Field("deviceid") deviceID: String,
        @Field("devicetype") deviceType: String,
        @Field("users_id") userID: String,
        @Field("offset") offset: String,
        @Field("scroll_to") scrollTo: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/getnotification_details")
    fun pushNotificationDetail(
        @Field("access_token") accessToken: String,
        @Field("push_id") pushID: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/leaveRequests")
    fun getLeaveRequests(
        @Field("access_token") accessToken: String,
        @Field("users_id") userID: String,
        @Field("student_id")student_id: String
    ): Call<ResponseBody>
    @FormUrlEncoded
    @POST("api/changepassword")
    fun changePassword(
        @Field("access_token")accessToken: String,
        @Field("userd_id")userId: String,
        @Field("current_password")toString: String,
        @Field("new_password")toString1: String,
        @Field("email")userEmail: Any,
        @Field("deviceid")token: String,
        @Field("devicetype")s: String
    ): Call<ResponseBody>
    @FormUrlEncoded
    @POST("api/sendemail")
    fun sendEmailToStaffCall(
        @Field("access_token")accessToken: String,
        @Field("email")emailNas: String?,
        @Field("users_id")userId: String,
        @Field("title")toString: String,
        @Field("message")toString1: String
    ): Call<ResponseBody>
    @FormUrlEncoded
    @POST("api/about_us")
    fun aboutUsListCall(
        @Field("access_token")accessToken: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/parent_essentials")
    fun newsLetterCategoryCall(
        @Field("access_token")accessToken: String
    ): Call<ResponseBody>
    @FormUrlEncoded
    @POST("api/nastoday")
    fun nasTodayListCall(
        @Field("access_token")accessToken: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/contact_us")
    fun contactUsCall(
        @Field("access_token")accessToken: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/getstaffcategorylist")
    fun staffDirectoryListCall(
        @Field("access_token" )accessToken: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/studentlist")
    fun getStudentListFirstCall(
        @Field("access_token")accessToken: String,
        @Field("users_id")userId: String
    ): Call<ResponseBody>
}
