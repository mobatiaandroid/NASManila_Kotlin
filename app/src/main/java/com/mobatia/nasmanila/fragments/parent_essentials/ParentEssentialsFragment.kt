package com.mobatia.nasmanila.fragments.parent_essentials

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.View.OnFocusChangeListener
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.parent_essentials.ParentEssentialActivity
import com.mobatia.nasmanila.activities.web_view.LoadUrlWebViewActivity
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.constants.URLConstants
import com.mobatia.nasmanila.fragments.parent_essentials.adapter.ParentEssentialsListAdapter
import com.mobatia.nasmanila.fragments.parent_essentials.model.ParentEssentialsModel
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.PreferenceManager
import com.mobatia.nasmanila.manager.recyclermanager.DividerItemDecoration
import com.mobatia.nasmanila.manager.recyclermanager.OnItemClickListener
import com.mobatia.nasmanila.manager.recyclermanager.addOnItemClickListener
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ParentEssentialsFragment(title: String, tabId: String) : Fragment() {
    var appUtils: AppUtils = AppUtils()
    var preferenceManager: PreferenceManager = PreferenceManager()
    var mTitleTextView: TextView? = null
    private var mRootView: View? = null
    private var mContext: Context? = null
    private var mListView: RecyclerView? = null
    private val mTitle: String? = title
    private val mTabId: String? = tabId
    var bannerImagePager: ImageView? = null
    private val mBannerImage: ImageView? = null
    var bannerUrlImageArray: ArrayList<String>? = null
    private val newsLetterModelArrayList = ArrayList<ParentEssentialsModel>()
    var relMain: RelativeLayout? = null
    var mtitle: RelativeLayout? = null
    var text_content: TextView? = null
    var text_dialog: TextView? = null
    var description = ""
    var contactEmail = ""
    var descriptionTV: TextView? = null
    var descriptionTitle: TextView? = null
    var sendEmail: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        mRootView =
            inflater.inflate(
                R.layout.fragment_parent_essentials, container,
                false
            )
        mContext = activity
        initialiseUI()
        return mRootView
        return inflater.inflate(R.layout.fragment_parent_essentials, container, false)
    }

    private fun initialiseUI() {
        mtitle = mRootView!!.findViewById<View>(R.id.title) as RelativeLayout
        descriptionTV = mRootView!!.findViewById<View>(R.id.descriptionTV) as TextView
        descriptionTitle = mRootView!!.findViewById<View>(R.id.descriptionTitle) as TextView
        sendEmail = mRootView!!.findViewById<View>(R.id.sendEmail) as ImageView
        mTitleTextView = mRootView!!.findViewById<View>(R.id.titleTextView) as TextView
        mTitleTextView!!.text = NaisClassNameConstants.PARENT_ESSENTIALS
        mListView = mRootView!!.findViewById<View>(R.id.mListView) as RecyclerView
        mListView!!.isNestedScrollingEnabled = false
        bannerImagePager = mRootView!!.findViewById<View>(R.id.bannerImagePager) as ImageView
        relMain = mRootView!!.findViewById<View>(R.id.relMain) as RelativeLayout
        relMain!!.setOnClickListener {

        }
        val llm = LinearLayoutManager(mContext)
        llm.orientation = LinearLayoutManager.VERTICAL
        mListView!!.addItemDecoration(DividerItemDecoration(resources.getDrawable(R.drawable.list_divider)))

        mListView!!.layoutManager = llm
        if (appUtils.checkInternet(mContext!!)) {
            getNewslettercategory()
        } else {
            appUtils.showDialogAlertDismiss(
                mContext as Activity?,
                "Network Error",
                getString(R.string.no_internet),
                R.drawable.nonetworkicon,
                R.drawable.roundred
            )
        }
        mListView!!.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                if (newsLetterModelArrayList[position].title.equals(
                        "Bus Service",
                        ignoreCase = true
                    ) || newsLetterModelArrayList[position].title.equals(
                        "Lunch Menu",
                        ignoreCase = true
                    )
                ) {
                    val intent = Intent(mContext, ParentEssentialActivity::class.java)
                    intent.putExtra(
                        "submenuArray",
                        newsLetterModelArrayList[position].newsLetterModelArrayList
                    )
                    intent.putExtra("tab_type", NaisClassNameConstants.PARENT_ESSENTIALS)
                    intent.putExtra("tab_typeName", newsLetterModelArrayList[position].title)
                    intent.putExtra("bannerImage", newsLetterModelArrayList[position].bannerImage)
                    intent.putExtra("contactEmail", newsLetterModelArrayList[position].contactEmail)
                    intent.putExtra("description", newsLetterModelArrayList[position].description)
                    mContext!!.startActivity(intent)
                } else if (newsLetterModelArrayList[position].title.equals(
                        "Parent Portal",
                        ignoreCase = true
                    )
                ) {
                    val intent = Intent(mContext, LoadUrlWebViewActivity::class.java)
                    intent.putExtra("tab_type", NaisClassNameConstants.PARENT_ESSENTIALS)
                    intent.putExtra("url", newsLetterModelArrayList[position].link)
                    (mContext as Activity).startActivity(intent)
                } else {
                    val intent = Intent(mContext, ParentEssentialActivity::class.java)
                    intent.putExtra(
                        "submenuArray",
                        newsLetterModelArrayList[position].newsLetterModelArrayList
                    )
                    intent.putExtra("tab_type", NaisClassNameConstants.PARENT_ESSENTIALS)
                    intent.putExtra("tab_typeName", newsLetterModelArrayList[position].title)
                    intent.putExtra("bannerImage", newsLetterModelArrayList[position].bannerImage)
                    (mContext as Activity).startActivity(intent)
                }
            }

        })
        sendEmail!!.setOnClickListener {
            if (!preferenceManager.getUserId(mContext as Activity).equals("", ignoreCase = true)) {
                val dialog = Dialog(mContext as Activity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.alert_send_email_dialog)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val dialogCancelButton = dialog.findViewById<View>(R.id.cancelButton) as Button
                val submitButton = dialog.findViewById<View>(R.id.submitButton) as Button
                text_dialog = dialog.findViewById<View>(R.id.text_dialog) as EditText
                text_content = dialog.findViewById<View>(R.id.text_content) as EditText
                (text_dialog as EditText).setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        (text_dialog as EditText).hint = ""
                        (text_dialog as EditText).gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                        (text_dialog as EditText).setPadding(5, 5, 0, 0)
                    } else {
                        (text_dialog as EditText).hint = "Enter your subject here..."
                        (text_dialog as EditText).gravity = Gravity.CENTER
                    }
                })
                (text_content as EditText).onFocusChangeListener =
                    OnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            (text_content as EditText).gravity = Gravity.LEFT
                        } else {
                            (text_content as EditText).gravity = Gravity.CENTER
                        }
                    }
                dialogCancelButton.setOnClickListener { dialog.dismiss() }
                submitButton.setOnClickListener { //						sendEmailToStaff(URL_SEND_EMAIL_TO_STAFF);
                    if ((text_dialog as EditText).getText().equals("")) {
                        appUtils.showDialogAlertDismiss(
                            mContext as Activity?,
                            (mContext as Activity).getString(R.string.alert_heading),
                            "Please enter  subject",
                            R.drawable.exclamationicon,
                            R.drawable.round
                        )
                    } else if ((text_content as EditText).getText().toString() == "") {
                        appUtils.showDialogAlertDismiss(
                            mContext as Activity?,
                            (mContext as Activity).getString(R.string.alert_heading),
                            "Please enter  content",
                            R.drawable.exclamationicon,
                            R.drawable.round
                        )
                    } else {
                        if (appUtils.checkInternet(mContext as Activity)) {
                            sendEmailToStaff(URLConstants.URL_SEND_EMAIL_TO_STAFF)
                        } else {
                            appUtils.showDialogAlertDismiss(
                                mContext as Activity?,
                                "Network Error",
                                (mContext as Activity).getString(R.string.no_internet),
                                R.drawable.nonetworkicon,
                                R.drawable.roundred
                            )
                        }
                    }
                }
                dialog.show()
            } else {
                appUtils.showDialogAlertDismiss(
                    mContext as Activity?,
                    (mContext as Activity).getString(R.string.alert_heading),
                    "This feature is available only for registered users. Login/register to see contents.",
                    R.drawable.exclamationicon,
                    R.drawable.round
                )
            }
        }
    }

    private fun getNewslettercategory() {
        val call: Call<ResponseBody> = ApiClient.getApiService().newsLetterCategoryCall(preferenceManager.getAccessToken(mContext))
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
                        if (description.equals("", ignoreCase = true) && contactEmail.equals("", ignoreCase = true)) {
                            mtitle!!.visibility = View.GONE
                        } else {
                            mtitle!!.visibility = View.VISIBLE
                        }
                        if (description.equals("", ignoreCase = true)) {
                            descriptionTV!!.visibility = View.GONE
                            descriptionTitle!!.visibility = View.GONE
                        } else {
                            descriptionTV!!.text = description
                            descriptionTitle!!.visibility = View.GONE
                            descriptionTV!!.visibility = View.VISIBLE
                            mtitle!!.visibility = View.VISIBLE
                        }
                        if (contactEmail.equals("", ignoreCase = true)) {
                            sendEmail!!.visibility = View.GONE
                        } else {
                            sendEmail!!.visibility = View.VISIBLE
                            mtitle!!.visibility = View.VISIBLE
                        }
                        val responses: JSONArray =
                            responseJSONObject.getJSONArray(JSONConstants.JTAG_RESPONSE_DATA_ARRAY)
                        if (responses.length() > 0) {
                            for (i in 0 until responses.length()) {
                                val dataObject = responses.getJSONObject(i)
                                newsLetterModelArrayList.add(addNewsLetterDetails(dataObject))
                            }
                            val newsLetterAdapter =
                                ParentEssentialsListAdapter(mContext, newsLetterModelArrayList)
                            mListView!!.adapter = newsLetterAdapter
                        } else {
                            Toast.makeText(mContext, "No data found", Toast.LENGTH_SHORT).show()
                        }

                    }
                } else if (responseCode.equals("500", ignoreCase = true)) {
                    appUtils.showDialogAlertDismiss(
                        mContext as Activity?,
                        "Alert",
                        getString(R.string.common_error),
                        R.drawable.exclamationicon,
                        R.drawable.round
                    )
                } else if (responseCode.equals("400", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                } else if (responseCode.equals("401", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                } else if (responseCode.equals("402", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
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

    private fun addNewsLetterDetails(dataObject: JSONObject?): ParentEssentialsModel {
        val model = ParentEssentialsModel()
        model.title = dataObject!!.optString(JSONConstants.JTAG_TAB_NAME)
        if (dataObject.has(JSONConstants.JTAG_BANNER_IMAGE)) {
            model.bannerImage = dataObject.optString(JSONConstants.JTAG_BANNER_IMAGE)
        } else {
            model.bannerImage = ""
        }
        if (dataObject.has(JSONConstants.JTAG_DESCRIPTION)) {
            model.description = dataObject.optString(JSONConstants.JTAG_DESCRIPTION)
        } else {
            model.description = ""
        }
        if (dataObject.has(JSONConstants.JTAG_CONTACT_EMAIL)) {
            model.contactEmail = dataObject.optString(JSONConstants.JTAG_CONTACT_EMAIL)
        } else {
            model.contactEmail = ""
        }
        if (dataObject.has("link")) {
            model.link = dataObject.optString("link")
        } else {
            model.link = ""
        }
        val submenuArray: JSONArray = dataObject.getJSONArray(JSONConstants.JTAG_TAB_SUBMENU_ARRAY)
        val subMenNewsLetterModels = ArrayList<ParentEssentialsModel>()
        for (i in 0 until submenuArray.length()) {
            val subObj = submenuArray.getJSONObject(i)
            val newsModel = ParentEssentialsModel()
            newsModel.filename = subObj.optString("filename")
            newsModel.submenu = subObj.optString("submenu")
            subMenNewsLetterModels.add(newsModel)
        }
        model.newsLetterModelArrayList = subMenNewsLetterModels
        return model
    }

    private fun sendEmailToStaff(url: String) {
        val call: Call<ResponseBody> = ApiClient.getApiService().sendEmailToStaffCall(
            preferenceManager.getAccessToken(mContext),
            contactEmail,
            preferenceManager.getUserId(mContext!!),
            text_dialog!!.text.toString(),
            text_content!!.text.toString()
        )
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
                        appUtils.showDialogAlertDismiss(
                            mContext as Activity?,
                            "Success",
                            "Successfully sent email to staff",
                            R.drawable.tick,
                            R.drawable.round
                        )
                    } else {
                        val toast = Toast.makeText(mContext, "Email not sent", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                } else if(responseCode.equals("500")) {
                    appUtils.getToken(context!!)
                } else if(responseCode.equals("400")) {
                    appUtils.getToken(context!!)
                } else if(responseCode.equals("401")) {
                    appUtils.getToken(context!!)
                } else if(responseCode.equals("402")) {
                    appUtils.getToken(context!!)
                } else {
                    appUtils.showDialogAlertDismiss(
                        mContext as Activity?,
                        "Alert",
                        (mContext as Activity).getString(R.string.common_error),
                        R.drawable.exclamationicon,
                        R.drawable.round
                    )
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                appUtils.showDialogAlertDismiss(
                    mContext as Activity?,
                    "Alert",
                    (mContext as Activity).getString(R.string.common_error),
                    R.drawable.exclamationicon,
                    R.drawable.round
                )

            }

        })
    }
}



