package com.mobatia.nasmanila.fragments.about_us

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobatia.nasmanila.R
import com.github.barteksc.pdfviewer.PDFView
import com.mobatia.nasmanila.activities.home.HomeListActivity
import com.mobatia.nasmanila.activities.pdf.PDFViewActivity
import com.mobatia.nasmanila.activities.web_view.LoadUrlWebViewActivity
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.constants.URLConstants
import com.mobatia.nasmanila.fragments.about_us.adapter.FacilityRecyclerAdapterNew
import com.mobatia.nasmanila.fragments.about_us.model.AboutUsModel
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.HeaderManager
import com.mobatia.nasmanila.manager.PreferenceManager
import com.mobatia.nasmanila.manager.recyclermanager.ItemOffsetDecoration
import com.mobatia.nasmanila.manager.recyclermanager.OnItemClickListener
import com.mobatia.nasmanila.manager.recyclermanager.addOnItemClickListener
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FacilityActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    var extras: Bundle? = null
    var aboutusModelArrayList: ArrayList<AboutUsModel> = ArrayList<AboutUsModel>()
    private var mContext: Context = this
    private var mAboutUsList: RecyclerView? = null
    private val mBannerImage: ImageView? = null
    private  var sendEmail:android.widget.ImageView? = null
    var mTitleTextView: TextView? = null
    var weburl:TextView? = null
    var description:TextView? = null
    var descriptionTitle:TextView? = null

    private var mAboutUsListArray: ArrayList<AboutUsModel>? = null
    var relMain: RelativeLayout? = null
    var desc: String? = null
    var title:kotlin.String? = null
    var bannerimg:kotlin.String? = null
    var bannerImagePager: ImageView? = null

    var relativeHeader: RelativeLayout? = null
    var headermanager: HeaderManager? = null
    var back: ImageView? = null
    var home: ImageView? = null
    var bannerUrlImageArray = ArrayList<String>()
    var contactEmail: String? = null
    var text_dialog: EditText? = null
    var text_content: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facility)
        mContext = this
        initialiseUI()
    }

    private fun initialiseUI() {
        if (extras != null) {
            mAboutUsListArray = extras!!
                .getSerializable("array") as ArrayList<AboutUsModel>?
            desc = extras!!.getString("desc")
            title = extras!!.getString("title")
            bannerimg = extras!!.getString("banner_image")
            println("Image url--$bannerimg")
            if (bannerimg != "") {
                bannerUrlImageArray.add(bannerimg!!)
            }
        }
        relativeHeader = findViewById<View>(R.id.relativeHeader) as RelativeLayout
        bannerImagePager = findViewById<View>(R.id.bannerImagePager) as ImageView

        headermanager = HeaderManager(this@FacilityActivity, NaisClassNameConstants.ABOUT_US)
        headermanager!!.getHeader(relativeHeader!!, 0)
        back = headermanager!!.getLeftButton()
        headermanager!!.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        back!!.setOnClickListener {
            appUtils.hideKeyboard(mContext)
            finish()
        }
        home = headermanager!!.getLogoButton()
        home!!.setOnClickListener {
            val `in` = Intent(mContext, HomeListActivity::class.java)
            `in`.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(`in`)
        }

        mAboutUsList = findViewById<View>(R.id.mAboutUsListView) as RecyclerView
        description = findViewById<View>(R.id.descriptionTV) as TextView
        descriptionTitle = findViewById<View>(R.id.descriptionTitle) as TextView
        if (desc.equals("", ignoreCase = true)) {
            description!!.visibility = View.GONE
            descriptionTitle!!.visibility = View.GONE
        } else {
            description!!.text = desc
            descriptionTitle!!.visibility = View.GONE
            //			descriptionTitle.setVisibility(View.VISIBLE);
            description!!.visibility = View.VISIBLE
        }
        weburl = findViewById<View>(R.id.weburl) as TextView
        sendEmail = findViewById<View>(R.id.sendEmail) as ImageView
        sendEmail!!.visibility = View.GONE




        mAboutUsList!!.setHasFixedSize(true)
        val recyclerViewLayout = GridLayoutManager(mContext, 4)
        val spacing = 5 // 50px

        val itemDecoration = ItemOffsetDecoration(mContext, spacing)
        mAboutUsList!!.addItemDecoration(itemDecoration)
        mAboutUsList!!.layoutManager = recyclerViewLayout
        if (bannerimg != "") {
            Glide.with(mContext).load(appUtils.replace(bannerimg!!)).centerCrop()
                .into(bannerImagePager!!)
        }
        val mFacilityRecyclerAdapter = FacilityRecyclerAdapterNew(mContext, mAboutUsListArray)
        mAboutUsList!!.adapter = mFacilityRecyclerAdapter
        mAboutUsList!!.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                if (mAboutUsListArray!!.size <= 1) {
                    if (mAboutUsListArray!![position].itemPdfUrl!!.endsWith(".pdf")) {
                        val intent = Intent(mContext, PDFViewActivity::class.java)
                        intent.putExtra("pdf_url", NaisClassNameConstants.ABOUT_US)
                        startActivity(intent)
                    } else {
                        val intent = Intent(mContext, LoadUrlWebViewActivity::class.java)
                        intent.putExtra(
                            "url",
                            mAboutUsListArray!![position].itemPdfUrl
                        )
                        intent.putExtra("tab_type", NaisClassNameConstants.ABOUT_US)
                        startActivity(intent)
                    }
                } else {
                    if (mAboutUsListArray!![position].itemPdfUrl!!.endsWith(".pdf")) {
                        val intent = Intent(mContext, PDFViewActivity::class.java)
                        intent.putExtra(
                            "pdf_url",
                            mAboutUsListArray!![position].itemPdfUrl
                        )
                        startActivity(intent)
                    } else {
                        val intent = Intent(mContext, LoadUrlWebViewActivity::class.java)
                        intent.putExtra(
                            "url",
                            mAboutUsListArray!![position].itemPdfUrl
                        )
                        intent.putExtra("tab_type", NaisClassNameConstants.ABOUT_US)
                        startActivity(intent)
                    }
                }
            }

        })


        sendEmail!!.setOnClickListener {
            if (!preferenceManager.getUserId(mContext)
                    .equals("", ignoreCase = true)
            ) {
                val dialog = Dialog(mContext)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.alert_send_email_dialog)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val dialogCancelButton =
                    dialog.findViewById<View>(R.id.cancelButton) as Button
                val submitButton =
                    dialog.findViewById<View>(R.id.submitButton) as Button
                text_dialog = dialog.findViewById<View>(R.id.text_dialog) as EditText
                text_content = dialog.findViewById<View>(R.id.text_content) as EditText
                text_dialog!!.onFocusChangeListener =
                    OnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            text_dialog!!.hint = ""
                            text_dialog!!.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                            text_dialog!!.setPadding(5, 5, 0, 0)
                        } else {
                            text_dialog!!.hint = "Enter your subject here..."
                            text_dialog!!.gravity = Gravity.CENTER
                        }
                    }
                text_content!!.onFocusChangeListener =
                    OnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            text_content!!.gravity = Gravity.LEFT
                        } else {
                            text_content!!.gravity = Gravity.CENTER
                        }
                    }
                dialogCancelButton.setOnClickListener { dialog.dismiss() }
                submitButton.setOnClickListener {
                    if (text_dialog!!.text.equals("") ) {
                        appUtils.showDialogAlertDismiss(
                            mContext as Activity,
                            mContext.getString(R.string.alert_heading),
                            "Please enter subject",
                            R.drawable.exclamationicon,
                            R.drawable.round
                        )
                    } else if (text_content!!.text.toString() == "") {
                        appUtils.showDialogAlertDismiss(
                            mContext as Activity,
                            mContext.getString(R.string.alert_heading),
                            "Please enter content",
                            R.drawable.exclamationicon,
                            R.drawable.round
                        )
                    } else {
                        if (appUtils.checkInternet(mContext)) {
                            sendEmailToStaff(URLConstants.URL_SEND_EMAIL_TO_STAFF)
                        } else {
                            appUtils.showDialogAlertDismiss(
                                mContext as Activity,
                                "Network Error",
                                mContext.getString(R.string.no_internet),
                                R.drawable.nonetworkicon,
                                R.drawable.roundred
                            )
                        }
                    }
                }
                dialog.show()
            } else {
                appUtils.showDialogAlertDismiss(
                    mContext as Activity,
                    mContext.getString(R.string.alert_heading),
                    "This feature is available only for registered users. Login/register to see contents.",
                    R.drawable.exclamationicon,
                    R.drawable.round
                )
            }
        }
    }

    private fun sendEmailToStaff(urlSendEmailToStaff: String) {
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
                    appUtils.getToken(mContext!!)
                } else if(responseCode.equals("400")) {
                    appUtils.getToken(mContext!!)
                } else if(responseCode.equals("401")) {
                    appUtils.getToken(mContext!!)
                } else if(responseCode.equals("402")) {
                    appUtils.getToken(mContext!!)
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
                    mContext as Activity,
                    "Alert",
                    mContext.getString(R.string.common_error),
                    R.drawable.exclamationicon,
                    R.drawable.round
                )
            }

        })
    }


}