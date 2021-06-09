package com.mobatia.nasmanila.activities.parent_essentials

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.HomeListActivity
import com.mobatia.nasmanila.activities.parent_essentials.adapter.ParentEssentialActivityListAdapter
import com.mobatia.nasmanila.activities.pdf.PDFViewActivity
import com.mobatia.nasmanila.activities.web_view.LoadUrlWebViewActivity
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.constants.URLConstants
import com.mobatia.nasmanila.fragments.parent_essentials.model.ParentEssentialsModel
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

class ParentEssentialActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    var extras: Bundle? = null
    var list: ArrayList<ParentEssentialsModel>? = null
    var tab_type: String? = null
    var tab_typeName: String? = null
    var mContext: Context = this
    var mNewsLetterListView: RecyclerView? = null
    var relativeHeader: LinearLayout? = null
    var headermanager: HeaderManager? = null
    var back: ImageView? = null
    var home: ImageView? = null
    var mailImageView: ImageView? = null
    var descriptionTV: TextView? = null
    var descriptionTitle: TextView? = null
    var bannerImagePager: ImageView? = null
    var bannerUrlImageArray: ArrayList<String>? = null
    var bannerImage = ""
    var contactEmail = ""
    var description = ""
    var text_content: EditText? = null
    var text_dialog: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_essential)
        initialiseUI()
        val newsDetailListAdapter = ParentEssentialActivityListAdapter(mContext, list)
        mNewsLetterListView!!.adapter = newsDetailListAdapter

    }

    private fun initialiseUI() {
        extras = intent.extras
        if (extras != null) {
            list = extras!!.getSerializable("submenuArray") as ArrayList<ParentEssentialsModel>?
            tab_type = extras!!.getString("tab_type")
            bannerImage = extras!!.getString("bannerImage")!!
            contactEmail = extras!!.getString("contactEmail")!!
            description = extras!!.getString("description")!!
            tab_typeName = extras!!.getString("tab_typeName")
        }
        relativeHeader = findViewById<View>(R.id.relativeHeader) as LinearLayout
        descriptionTV = findViewById<View>(R.id.descriptionTV) as TextView
        descriptionTitle = findViewById<View>(R.id.descriptionTitle) as TextView
        mailImageView = findViewById<View>(R.id.mailImageView) as ImageView
        bannerImagePager = findViewById<View>(R.id.bannerImagePager) as ImageView
        mNewsLetterListView = findViewById<View>(R.id.mListView) as RecyclerView
        headermanager = HeaderManager(this@ParentEssentialActivity, tab_type)
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

        if (!bannerImage.equals("", ignoreCase = true)) {

            Glide.with(mContext).load(appUtils.replace(bannerImage)).centerCrop()
                .placeholder(R.drawable.default_bannerr).error(R.drawable.default_bannerr)
                .into(bannerImagePager!!)
        } else {
            bannerImagePager!!.setBackgroundResource(R.drawable.default_bannerr)

        }
        if (!description.equals("", ignoreCase = true)) {
            descriptionTV!!.visibility = View.VISIBLE
            descriptionTV!!.text = description
            descriptionTitle!!.visibility = View.GONE
            ;
        } else {
            descriptionTV!!.visibility = View.GONE
            descriptionTitle!!.visibility = View.GONE
        }
        if (!contactEmail.equals(
                "",
                ignoreCase = true
            ) && preferenceManager.getUserId(mContext) != ""
        ) {
            mailImageView!!.visibility = View.VISIBLE
        } else {
            mailImageView!!.visibility = View.INVISIBLE
        }
        mailImageView!!.setOnClickListener {
            val dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.alert_send_email_dialog)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val dialogCancelButton = dialog.findViewById<View>(R.id.cancelButton) as Button
            val submitButton = dialog.findViewById<View>(R.id.submitButton) as Button
            text_dialog = dialog.findViewById<View>(R.id.text_dialog) as EditText
            text_content = dialog.findViewById<View>(R.id.text_content) as EditText

            dialogCancelButton.setOnClickListener {
                dialog.dismiss()
            }
            submitButton.setOnClickListener {
                if (text_dialog!!.text.toString() == "") {
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

        }
        mNewsLetterListView!!.setHasFixedSize(true)
        val recyclerViewLayout = GridLayoutManager(mContext, 4)
        val spacing = 5

        val itemDecoration = ItemOffsetDecoration(mContext, spacing)
        mNewsLetterListView!!.addItemDecoration(itemDecoration)
        mNewsLetterListView!!.layoutManager = recyclerViewLayout
        mNewsLetterListView!!.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                if (list!!.size <= 1) {
                    if (list!![position].filename!!.endsWith(".pdf")) {
                        val intent = Intent(mContext, PDFViewActivity::class.java)
                        intent.putExtra("pdf_url", list!![position].filename)
                        startActivity(intent)
                    } else {
                        val intent = Intent(mContext, LoadUrlWebViewActivity::class.java)
                        intent.putExtra("url", list!![position].filename)
                        intent.putExtra("tab_type", tab_type)
                        startActivity(intent)
                    }
                } else {
                    if (list!![position].filename!!.endsWith(".pdf") && tab_typeName.equals(
                            "Lunch Menu",
                            ignoreCase = true
                        )
                    ) {
                        val intent = Intent(mContext, PDFViewActivity::class.java)
                        intent.putExtra(
                            "pdf_url",
                            list!![position].filename
                        )
                        startActivity(intent)
                    } else if (list!![position].filename!!.endsWith(".pdf")) {
                        val intent = Intent(mContext, PDFViewActivity::class.java)
                        intent.putExtra("pdf_url", list!![position].filename)
                        startActivity(intent)
                    } else {
                        val intent = Intent(mContext, LoadUrlWebViewActivity::class.java)
                        intent.putExtra("url", list!![position].filename)
                        intent.putExtra("tab_type", tab_type)
                        startActivity(intent)
                    }
                }
            }

        })

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