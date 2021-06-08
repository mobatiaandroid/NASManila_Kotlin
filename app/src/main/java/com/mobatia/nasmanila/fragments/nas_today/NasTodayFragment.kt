package com.mobatia.nasmanila.fragments.nas_today

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.fragments.nas_today.adapter.NasTodayAdapter
import com.mobatia.nasmanila.fragments.nas_today.model.NasTodayModel
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.PreferenceManager
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class NasTodayFragment(nasToday: String, tabNasToday: String) : Fragment() {
    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    var mTitleTextView: TextView? = null
    private var mRootView: View? = null
    private var mContext: Context? = null
    private var mNasTodayListView: ListView? = null
    private val mTitle: String? = null
    private val mTabId: String? = null
    private var relMain: RelativeLayout? = null
    private val mBannerImage: ImageView? = null
    private val mListViewArray: ArrayList<NasTodayModel>? = null
    var bannerImagePager: ImageView? = null

    var bannerUrlImageArray: ArrayList<String>? = null
    var myFormatCalender = "yyyy-MM-dd"
    var sdfcalender: SimpleDateFormat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(
            R.layout.fragment_nas_today, container,
            false
        )

        mContext = activity
        myFormatCalender = "yyyy-MM-dd hh:mm:ss"
        sdfcalender = SimpleDateFormat(myFormatCalender, Locale.ENGLISH)
        initialiseUI()
        return mRootView

    }

    private fun initialiseUI() {
        mTitleTextView = mRootView!!.findViewById<View>(R.id.titleTextView) as TextView
        mNasTodayListView = mRootView!!.findViewById<View>(R.id.mNasTodayListView) as ListView
        mTitleTextView!!.setText(NaisClassNameConstants.NAS_TODAY)
        bannerImagePager = mRootView!!.findViewById<View>(R.id.bannerImageViewPager) as ImageView
        relMain = mRootView!!.findViewById<View>(R.id.relMain) as RelativeLayout
        relMain!!.setOnClickListener(View.OnClickListener { })
        if (appUtils.checkInternet(mContext!!)) {
            getList()
        } else {
            appUtils.showDialogAlertDismiss(
                mContext as Activity?,
                "Network Error",
                getString(R.string.no_internet),
                R.drawable.nonetworkicon,
                R.drawable.roundred
            )
        }
    }

    private fun getList() {
        val call: Call<ResponseBody> = ApiClient.getApiService().nasTodayListCall(preferenceManager.getAccessToken(mContext))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseString = response.body()!!.string()
                val jsonObject = JSONObject(responseString)
                val responseCode = jsonObject.getString(JSONConstants.JTAG_RESPONSECODE)
                if (responseCode == "200") {
                    val responseJSONObject =
                        jsonObject.getJSONObject(JSONConstants.JTAG_RESPONSE)
                    val statusCode = responseJSONObject.getString(JSONConstants.JTAG_STATUSCODE)
                    if (statusCode.equals(StatusConstants.STATUS_SUCCESS, ignoreCase = true)) {
                        val bannerImage: String =
                            responseJSONObject.optString(JSONConstants.JTAG_BANNER_IMAGE)
                        if (!bannerImage.equals("", ignoreCase = true)) {
                            Glide.with(mContext!!).load(appUtils.replace(bannerImage)).centerCrop()
                                .into(bannerImagePager!!)
                        } else {
                            bannerImagePager!!.setBackgroundResource(R.drawable.default_bannerr)
                        }
                        val dataArray: JSONArray =
                            responseJSONObject.getJSONArray(JSONConstants.JTAG_RESPONSE_DATA_ARRAY)
                        if (dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val dataObject = dataArray.getJSONObject(i)
                                mListViewArray!!.add(getSearchValues(dataObject))
                            }
                            mNasTodayListView!!.adapter = NasTodayAdapter(activity, mListViewArray)
                        } else {
                            appUtils.showDialogAlertDismiss(
                                mContext as Activity?,
                                "Alert",
                                getString(R.string.nodatafound),
                                R.drawable.exclamationicon,
                                R.drawable.round
                            )
                        }
                    } else {
//
                        appUtils.showDialogAlertDismiss(
                            mContext as Activity?,
                            "Alert",
                            getString(R.string.common_error),
                            R.drawable.exclamationicon,
                            R.drawable.round
                        )
                    }
                } else if (responseCode.equals(
                        StatusConstants.RESPONSE_ACCESSTOKEN_MISSING,
                        ignoreCase = true
                    ) ||
                    responseCode.equals(
                        StatusConstants.RESPONSE_ACCESSTOKEN_EXPIRED,
                        ignoreCase = true
                    ) ||
                    responseCode.equals(StatusConstants.RESPONSE_INVALID_TOKEN, ignoreCase = true)
                ) {
                    appUtils.getToken(activity!!)
                    getList()
                } else if (responseCode == StatusConstants.RESPONSE_ERROR) {

                    appUtils.showDialogAlertDismiss(
                        mContext as Activity?,
                        "Alert",
                        getString(R.string.common_error),
                        R.drawable.exclamationicon,
                        R.drawable.round
                    )
                } else {

                    appUtils.showDialogAlertDismiss(
                        mContext as Activity?,
                        "Alert",
                        getString(R.string.common_error),
                        R.drawable.exclamationicon,
                        R.drawable.round
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                appUtils.showDialogAlertDismiss(
                    mContext as Activity?,
                    "Alert",
                    getString(R.string.common_error),
                    R.drawable.exclamationicon,
                    R.drawable.round
                )
            }

        })
    }

    private fun getSearchValues(dataObject: JSONObject?): NasTodayModel {
        val model = NasTodayModel()
        model.id = (dataObject!!.getString(JSONConstants.JTAG_ID))
        model.image = (dataObject.getString(JSONConstants.JTAG_IMAGE))
        model.title = (dataObject.getString(JSONConstants.JTAG_TITLE))
        model.description = (dataObject.getString(JSONConstants.JTAG_DESCRIPTION))
        model.date = (dataObject.getString(JSONConstants.JTAG_DATE))
        model.pdf = (dataObject.getString(JSONConstants.JTAG_PDF))
        var mNoticeDate: Date? = Date()
        val mDate: String = dataObject.optString(JSONConstants.JTAG_DATE)
        try {
            mNoticeDate = sdfcalender!!.parse(mDate)
        } catch (ex: ParseException) {
            Log.e("Date", "Parsing error")
        }
        val mDateTime: String = dataObject.getString(JSONConstants.JTAG_DATE)
        var mTime: Date? = Date()

        try {
            mTime = sdfcalender!!.parse(mDateTime)
            val format2 = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val startTime = format2.format(mTime)
            model.time = (startTime)
        } catch (ex: ParseException) {
            Log.e("Date", "Parsing error")
        }

        val day = DateFormat.format("dd", mNoticeDate) as String

        val monthString = DateFormat.format("MMM", mNoticeDate) as String

        val year = DateFormat.format("yyyy", mNoticeDate) as String

        model.day = (day)
        model.month = (monthString.toUpperCase())
        model.year = (year)
        return model
    }
}

