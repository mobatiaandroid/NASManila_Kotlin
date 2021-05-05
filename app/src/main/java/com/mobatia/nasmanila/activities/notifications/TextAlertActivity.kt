package com.mobatia.nasmanila.activities.notifications

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.HomeListActivity
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.IntentPassValueConstants
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.HeaderManager
import com.mobatia.nasmanila.manager.PreferenceManager
import com.mobatia.nasmanila.manager.ProgressBarDialog
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TextAlertActivity : AppCompatActivity() {
    var preferenceManager: PreferenceManager = PreferenceManager()
    var appUtils: AppUtils = AppUtils()
    var txtmsg: TextView? = null
    var mDateTv: TextView? = null
    var img: ImageView? = null
    var home: ImageView? = null
    var extras: Bundle? = null
    var position = 0
    var context: Context? = null
    var mActivity: Activity? = null
    var header: RelativeLayout? = null
    var back: ImageView? = null
    var relativeHeader: LinearLayout? = null
    var headermanager: HeaderManager? = null
    var progressBarDialog: ProgressBarDialog? = null
    var id = ""
    var title = ""
    var message = ""
    var url = ""
    var date = ""
    private var day = ""
    private var month = ""
    private var year = ""
    private var pushDate = ""
    private var pushID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_alert)
        mActivity = this
        context = this
        extras = intent.extras
        if (extras != null) {
            position = extras!!.getInt(IntentPassValueConstants.POSITION)
            day = extras!!.getString("Day")!!
            month = extras!!.getString("Month")!!
            year = extras!!.getString("Year")!!
            pushDate = extras!!.getString("PushDate")!!
            pushID = extras!!.getString("PushID")!!
        }
        initialiseUI()
        if (appUtils.checkInternet(mActivity as TextAlertActivity)) {
            callPushNotification(pushID)
        } else {
            appUtils.showDialogAlertDismiss(mActivity, "Network Error", getString(R.string.no_internet), R.drawable.nonetworkicon, R.drawable.roundred)
        }
    }

    private fun callPushNotification(pushID: String) {
        val call: Call<ResponseBody> = ApiClient.getApiService().pushNotificationDetail(
                preferenceManager.getAccessToken(context),
                pushID
        )
        progressBarDialog!!.show()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressBarDialog!!.dismiss()
                val responseString = response.body()!!.string()
                val jsonObject = JSONObject(responseString)
                val responseCode = jsonObject.getString(JSONConstants.JTAG_RESPONSECODE)
                if (responseCode == "200") {
                    val responseJSONObject =
                            jsonObject.getJSONObject(JSONConstants.JTAG_RESPONSE)
                    val statusCode = responseJSONObject.getString(JSONConstants.JTAG_STATUSCODE)
                    if (statusCode == "303") {
                        val dataArray: JSONArray = responseJSONObject.getJSONArray(JSONConstants.JTAG_RESPONSE_DATA_ARRAY)
                        for (i in 0 until dataArray.length()) {
                            val dataObject = dataArray.getJSONObject(i)
                            id = dataObject.optString("id")
                            title = dataObject.optString("title")
                            message = dataObject.optString("message")
                            url = dataObject.optString("url")
                            date = dataObject.optString("time_Stamp")
                            setDetails()
                        }
                    } else if (statusCode.equals(StatusConstants.RESPONSE_ACCESSTOKEN_EXPIRED, ignoreCase = true) ||
                            statusCode.equals(StatusConstants.RESPONSE_ACCESSTOKEN_MISSING, ignoreCase = true) ||
                            statusCode.equals(StatusConstants.RESPONSE_INVALID_TOKEN, ignoreCase = true)) {
                        appUtils.getToken(mActivity!!)
                        callPushNotification(pushID)
                    } else if (responseCode.equals(StatusConstants.RESPONSE_ACCESSTOKEN_EXPIRED, ignoreCase = true) ||
                            responseCode.equals(StatusConstants.RESPONSE_ACCESSTOKEN_MISSING, ignoreCase = true) ||
                            responseCode.equals(StatusConstants.RESPONSE_INVALID_TOKEN, ignoreCase = true)) {
                        appUtils.getToken(mActivity!!)
                        callPushNotification(pushID)
                    } else {
                        Toast.makeText(mActivity, "Some Error Occured", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressBarDialog!!.dismiss()
            }

        })
    }

    private fun setDetails() {
        txtmsg!!.text = message
        mDateTv!!.text = "$day-$month-$year $pushDate"
    }

    private fun initialiseUI() {
        img = findViewById<View>(R.id.image) as ImageView?
        txtmsg = findViewById<View>(R.id.txt) as TextView
        txtmsg!!.movementMethod = LinkMovementMethod.getInstance()
        progressBarDialog = ProgressBarDialog(context!!)
        mDateTv = findViewById<View>(R.id.mDateTv) as TextView
        relativeHeader = findViewById<View>(R.id.relativeHeader) as LinearLayout
        headermanager = HeaderManager(mActivity, "Notification")
        headermanager!!.getHeader(relativeHeader!!, 0)
        back = headermanager!!.getLeftButton()
        headermanager!!.setButtonLeftSelector(R.drawable.back,
                R.drawable.back)
        back!!.setOnClickListener { finish() }
        home = headermanager!!.getLogoButton()
        home!!.setOnClickListener {
            val `in` = Intent(mActivity, HomeListActivity::class.java)
            `in`.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(`in`)
        }
        setDetails()
    }
}