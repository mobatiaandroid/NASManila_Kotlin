package com.mobatia.nasmanila.fragments.notifications

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.*
import com.mobatia.nasmanila.fragments.notifications.adapter.PushNotificationListAdapter
import com.mobatia.nasmanila.fragments.notifications.model.PushNotificationModel
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.PreferenceManager
import com.mobatia.nasmanila.manager.recyclermanager.OnItemClickListener
import com.mobatia.nasmanila.manager.recyclermanager.addOnItemClickListener
import com.mobatia.nasmanila.recycler_view_manager.DividerItemDecoration
import com.mobatia.nasmanila.recycler_view_manager.ItemOffsetDecoration
import com.mobatia.nasmanila.recycler_view_manager.RecycleItemListener
import me.leolin.shortcutbadger.ShortcutBadger
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class NotificationsFragment(title: String, tabID: String) : Fragment() {
    var preferenceManager: PreferenceManager = PreferenceManager()
    var appUtils: AppUtils = AppUtils()
    private var mRootView: View? = null
    private var mContext: Context? = null
    private var notificationRecycler: RecyclerView? = null
    private var swipeNotification: SwipeRefreshLayout? = null
    private val mTitle: String = title
    private val mTabId: String = tabID
    private var constraintMain: ConstraintLayout? = null
    private val mBannerImage: ImageView? = null
    var progressBar: ProgressBar? = null
    var mTitleTextView: TextView? = null
    var pushNotificationArrayList: ArrayList<PushNotificationModel>? = null
    var mPushNotificationListAdapter: PushNotificationListAdapter? = null
    var mIntent: Intent? = null
    var myFormatCalender = "yyyy-MM-dd HH:mm:ss"
    var sdfcalendar: SimpleDateFormat? = null
    var isFromBottom = false
    var swipeRefresh = false
    var pageFrom = ""
    var scrollTo = ""
    var notificationSize = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(
            R.layout.fragment_notifications, container,
            false
        )

        mContext = activity
        myFormatCalender = "yyyy-MM-dd HH:mm:ss"
        sdfcalendar = SimpleDateFormat(myFormatCalender, Locale.ENGLISH)
        ShortcutBadger.applyCount(mContext, 0) //badge

        initialiseUI()
        if (appUtils.checkInternet(mContext!!)) {
//            clearBadge()
            val isFromBottom = false
            callPushNotification(
                URLConstants.URL_GET_NOTICATIONS_LIST,
                JSONConstants.JTAG_ACCESSTOKEN,
                JSONConstants.JTAG_DEVICE_iD,
                JSONConstants.JTAG_DEVICE_tYPE,
                JSONConstants.JTAG_USERS_ID,
                pageFrom,
                scrollTo
            )
        } else {
            appUtils.showDialogAlertDismiss(
                mContext as Activity?,
                "Network Error",
                getString(R.string.no_internet),
                R.drawable.nonetworkicon,
                R.drawable.roundred
            )
        }
        return mRootView
    }

    private fun clearBadge() {
        pushNotificationArrayList = ArrayList()
    }


    private fun callPushNotification(
        URL: String,
        accessToken: String,
        deviceID: String,
        deviceType: String,
        userID: String,
        pageFrom: String,
        scrollTo: String
    ) {
        val call: Call<ResponseBody> = ApiClient.getApiService().pushNotificationsCall(
            preferenceManager.getAccessToken(mContext),
            FirebaseInstanceId.getInstance().token!!,
            deviceType,
            preferenceManager.getUserId(mContext!!),
            pageFrom,
            scrollTo
        )
        progressBar!!.visibility = View.VISIBLE
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressBar!!.visibility = View.GONE
                val responseString = response.body()!!.string()
                val jsonObject = JSONObject(responseString)
                val responseCode = jsonObject.getString(JSONConstants.JTAG_RESPONSECODE)
                if (responseCode == "200") {
                    val responseJSONObject =
                        jsonObject.getJSONObject(JSONConstants.JTAG_RESPONSE)
                    val statusCode = responseJSONObject.getString(JSONConstants.JTAG_STATUSCODE)
                    if (statusCode.equals(StatusConstants.STATUS_SUCCESS, ignoreCase = true)) {
                        val dataArray: JSONArray =
                            responseJSONObject.getJSONArray(JSONConstants.JTAG_RESPONSE_DATA_ARRAY)
                        if (dataArray.length() > 0) {
                            notificationSize = dataArray.length()
                            for (i in 0 until dataArray.length()) {
                                val dataObject = dataArray.getJSONObject(i)
                                if (pushNotificationArrayList!!.size == 0) {
                                    pushNotificationArrayList!!.add(getSearchValues(dataObject))
                                } else {
                                    var isFound = false
                                    for (j in pushNotificationArrayList!!.indices) {
                                        val listId = dataObject.optString("id")
                                        if (listId.equals(
                                                pushNotificationArrayList!![j].id,
                                                ignoreCase = true
                                            )
                                        ) {
                                            isFound = true
                                        }
                                    }
                                    if (!isFound) {
                                        pushNotificationArrayList!!.add(getSearchValues(dataObject))
                                    }
                                }
                            }
                            mPushNotificationListAdapter = PushNotificationListAdapter(
                                mContext!!,
                                pushNotificationArrayList!!
                            )
                            notificationRecycler!!.adapter =
                                mPushNotificationListAdapter
                            if (isFromBottom) {
                                notificationRecycler!!.scrollToPosition(pushNotificationArrayList!!.size - 16)
                            }
                            if (swipeRefresh) {
                                notificationRecycler!!.scrollToPosition(pushNotificationArrayList!!.size)
                            }
                            mPushNotificationListAdapter!!.onBottomReachedListener =
                                object : OnBottomReachedListener {
                                    override fun onBottomReached(position: Int) {
//
                                    }
                                }
                        } else {
                            if (swipeRefresh) {
                            } else {
                                Toast.makeText(
                                    mContext,
                                    "Currently you do not have any notification.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else if (statusCode.equals(
                            StatusConstants.RESPONSE_ACCESSTOKEN_EXPIRED, ignoreCase = true
                        ) ||
                        statusCode.equals(
                            StatusConstants.RESPONSE_ACCESSTOKEN_MISSING, ignoreCase = true
                        ) ||
                        statusCode.equals(StatusConstants.RESPONSE_INVALID_TOKEN, ignoreCase = true)
                    ) {
                        appUtils.getToken(mContext!!)
                        callPushNotification(
                            URLConstants.URL_GET_NOTICATIONS_LIST,
                            JSONConstants.JTAG_ACCESSTOKEN,
                            JSONConstants.JTAG_DEVICE_iD,
                            JSONConstants.JTAG_DEVICE_tYPE,
                            JSONConstants.JTAG_USERS_ID,
                            this@NotificationsFragment.pageFrom,
                            this@NotificationsFragment.scrollTo
                        )
                        isFromBottom = false
                        swipeRefresh = false
                    }
                } else if (responseCode.equals(
                        StatusConstants.RESPONSE_ACCESSTOKEN_EXPIRED,
                        ignoreCase = true
                    ) ||
                    responseCode.equals(
                        StatusConstants.RESPONSE_ACCESSTOKEN_MISSING,
                        ignoreCase = true
                    ) ||
                    responseCode.equals(StatusConstants.RESPONSE_INVALID_TOKEN, ignoreCase = true)
                ) {
                    appUtils.getToken(mContext!!)
                    callPushNotification(
                        URLConstants.URL_GET_NOTICATIONS_LIST,
                        JSONConstants.JTAG_ACCESSTOKEN,
                        JSONConstants.JTAG_DEVICE_iD,
                        JSONConstants.JTAG_DEVICE_tYPE,
                        JSONConstants.JTAG_USERS_ID,
                        this@NotificationsFragment.pageFrom,
                        this@NotificationsFragment.scrollTo
                    )
                    isFromBottom = false
                    swipeRefresh = false
                } else {
                    Toast.makeText(mContext, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressBar!!.visibility = View.GONE
            }



        })

        //click for recyclerview inside activity/fragment (refer: com.mobatia.nasmanila.manager.recyclermanager)
        notificationRecycler!!.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {

            }

        })


    }

    private fun getSearchValues(dataObject: JSONObject?): PushNotificationModel {
        val mPushNotificationModel = PushNotificationModel()
        mPushNotificationModel.pushType = dataObject!!.optString("alert_type")
        mPushNotificationModel.id = dataObject.optString("id")
        mPushNotificationModel.headTitle = dataObject.optString("title")
        mPushNotificationModel.pushDate = dataObject.optString("time_Stamp")
        val mDate: String? = mPushNotificationModel.pushDate
        var mEventDate = Date()
        mEventDate = sdfcalendar!!.parse(mDate)
        val format2 = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val startTime = format2.format(mEventDate)
        mPushNotificationModel.pushTime = startTime
        val dayOfTheWeek = DateFormat.format("EEEE", mEventDate) as String // Thursday
        val day = DateFormat.format("dd", mEventDate) as String // 20
        val monthString = DateFormat.format("MMM", mEventDate) as String // June
        val monthNumber = DateFormat.format("MM", mEventDate) as String // 06
        val year = DateFormat.format("yyyy", mEventDate) as String // 2013
        mPushNotificationModel.dayOfTheWeek = dayOfTheWeek
        mPushNotificationModel.day = day
        mPushNotificationModel.monthString = monthString
        mPushNotificationModel.monthNumber = monthNumber
        mPushNotificationModel.year = year
        return mPushNotificationModel
    }

    private fun initialiseUI() {
        mTitleTextView = mRootView!!.findViewById<View>(R.id.titleTextView) as TextView
        mTitleTextView!!.text = NaisClassNameConstants.NOTIFICATIONS
        isFromBottom = false
        swipeRefresh = false
        pushNotificationArrayList = ArrayList()
        notificationRecycler = mRootView!!.findViewById(R.id.notification_recycler)
        swipeNotification = mRootView!!.findViewById(R.id.swipe_notification)
        constraintMain = mRootView!!.findViewById<View>(R.id.relMain) as ConstraintLayout
        progressBar = mRootView!!.findViewById(R.id.progressBar)
        constraintMain!!.setOnClickListener {  }
        notificationRecycler!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(mContext)
        val spacing = 10
        val itemDecoration = ItemOffsetDecoration(mContext!!, spacing)
        notificationRecycler!!.addItemDecoration(
            DividerItemDecoration(mContext!!.resources.getDrawable(R.drawable.list_divider))
        )
        notificationRecycler!!.addItemDecoration(itemDecoration)

        notificationRecycler!!.layoutManager = linearLayoutManager
//        notificationRecycler!!.addOnItemTouchListener(object : RecycleItemListener(
//            mContext!!,
//            notificationRecycler!!, object : RecycleItemListener.RecyclerTouchListener {
//                override fun onClickItem(v: View?, position: Int) {
//                    if (pushNotificationArrayList!![position].pushType.equals(
//                            "",
//                            ignoreCase = true
//                        )
//                    ) {
//                        mIntent = Intent(mContext, TextAlertActivity::class.java)
//                        mIntent!!.putExtra(IntentPassValueConstants.POSITION, position)
//                        mIntent!!.putExtra("PushID", pushNotificationArrayList!![position].id)
//                        mIntent!!.putExtra("Day", pushNotificationArrayList!![position].day)
//                        mIntent!!.putExtra(
//                            "Month",
//                            pushNotificationArrayList!![position].monthString
//                        )
//                        mIntent!!.putExtra("Year", pushNotificationArrayList!![position].year)
//                        mIntent!!.putExtra(
//                            "PushDate",
//                            pushNotificationArrayList!![position].pushTime
//                        )
//                        mContext!!.startActivity(mIntent)
//                    }
//                    if (pushNotificationArrayList!![position].pushType.equals(
//                            "Image",
//                            ignoreCase = true
//                        ) || pushNotificationArrayList!![position].pushType.equals(
//                            "Text",
//                            ignoreCase = true
//                        ) || pushNotificationArrayList!![position].pushType.equals(
//                            "Text",
//                            ignoreCase = true
//                        ) || pushNotificationArrayList!![position].pushType.equals(
//                            "Image",
//                            ignoreCase = true
//                        )
//                    ) {
//                        mIntent = Intent(mContext, ImageAlertActivity::class.java)
//                        mIntent!!.putExtra("PushID", pushNotificationArrayList!![position].id)
//                        mIntent!!.putExtra("Day", pushNotificationArrayList!![position].day)
//                        mIntent!!.putExtra(
//                            "Month",
//                            pushNotificationArrayList!![position].monthString
//                        )
//                        mIntent!!.putExtra("Year", pushNotificationArrayList!![position].year)
//                        mIntent!!.putExtra(
//                            "PushDate",
//                            pushNotificationArrayList!![position].pushTime
//                        )
//                        println("pushID" + pushNotificationArrayList!![position].id)
//                        mContext!!.startActivity(mIntent)
//                    }
//                    if (pushNotificationArrayList!![position].pushType.equals(
//                            "Voice",
//                            ignoreCase = true
//                        )
//                    ) {
//                        mIntent = Intent(mContext, AudioAlertActivity::class.java)
//                        mIntent!!.putExtra("PushID", pushNotificationArrayList!![position].id)
//                        mIntent!!.putExtra("Day", pushNotificationArrayList!![position].day)
//                        mIntent!!.putExtra(
//                            "Month",
//                            pushNotificationArrayList!![position].monthString
//                        )
//                        mIntent!!.putExtra("Year", pushNotificationArrayList!![position].year)
//                        mIntent!!.putExtra(
//                            "PushDate",
//                            pushNotificationArrayList!![position].pushTime
//                        )
//                        mContext!!.startActivity(mIntent)
//                    }
//                    if (pushNotificationArrayList!![position].pushType.equals(
//                            "Video",
//                            ignoreCase = true
//                        )
//                    ) {
//                        mIntent = Intent(mContext, VideoAlertActivity::class.java)
//                        mIntent!!.putExtra("PushID", pushNotificationArrayList!![position].id)
//                        mIntent!!.putExtra("Day", pushNotificationArrayList!![position].day)
//                        mIntent!!.putExtra(
//                            "Month",
//                            pushNotificationArrayList!![position].monthString
//                        )
//                        mIntent!!.putExtra("Year", pushNotificationArrayList!![position].year)
//                        mIntent!!.putExtra(
//                            "PushDate",
//                            pushNotificationArrayList!![position].pushTime
//                        )
//                        mContext!!.startActivity(mIntent)
//                    }
//
//                }
//                    override fun onLongClickItem(v: View?, position: Int) {
//
//            }
//
//    }){
//
//        })
    }

    companion object {

    }
}