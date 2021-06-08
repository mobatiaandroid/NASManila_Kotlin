package com.mobatia.nasmanila.activities.staff_directory.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.view.View.OnFocusChangeListener
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.staff_directory.model.StaffModel
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.constants.URLConstants
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.PreferenceManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*

class CustomStaffDeptRecyclerAdapter(
    mContext: Context?,
    mStaffDeptList: ArrayList<StaffModel>,
    s: String
) : RecyclerView.Adapter<CustomStaffDeptRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

        lateinit var appUtils: AppUtils
        lateinit var preferenceManager: PreferenceManager
        var departmentName: TextView? = null
        var staffName:TextView? = null
        var staffRole:TextView? = null
        var deptLayout: LinearLayout? = null
        var separator: View? = null
        var mail: ImageView? = null
        var staffImg:android.widget.ImageView? = null
        init {
            departmentName = view.findViewById<View>(R.id.departmentName) as TextView
            staffName = view.findViewById<View>(R.id.staffName) as TextView
            deptLayout = view.findViewById<View>(R.id.deptLayout) as LinearLayout
            separator = view.findViewById(R.id.separator) as View
            mail = view.findViewById<View>(R.id.mailImage) as ImageView
            staffImg = view.findViewById<View>(R.id.staffImg) as ImageView
            staffRole = view.findViewById<View>(R.id.staffRole) as TextView
        }

    }

    private val mContext: Context? = null
    private val mStaffList: ArrayList<StaffModel>? = null
    var dept: String? = null
    var text_dialog: EditText? = null
    var text_content:EditText? = null
    var pos = 0
    var dialog: Dialog? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_adapter_stafflist_item, parent, false)
        dialog = Dialog(mContext!!)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        lateinit var appUtils: AppUtils
        lateinit var preferenceManager: PreferenceManager
        if (dept == "") {
            holder.deptLayout!!.visibility = View.GONE
            holder.separator!!.visibility = View.GONE
        } else if (dept == "list") {
            holder.departmentName!!.text = dept
            holder.separator!!.visibility = View.VISIBLE
            holder.deptLayout!!.visibility = View.GONE
        } else {
            holder.deptLayout!!.visibility = View.GONE
            holder.separator!!.visibility = View.GONE
        }
        if (mStaffList!![position].staffImage != "") {
            Picasso.with(mContext).load(appUtils.replace(mStaffList!![position].staffImage!!)).fit()
                .into(holder.staffImg, object : Callback {
                    override fun onSuccess() {

                    }
                    override fun onError(e: Exception?) {
                        TODO("Not yet implemented")
                    }

                })
        }
        holder.staffName!!.text = mStaffList[position].staffName
        holder.staffRole!!.text = mStaffList[position].role
        holder.mail!!.setOnClickListener {
            println("click on mail--")
            if (preferenceManager.getUserId(mContext!!) != "") {
                dialog = Dialog(mContext!!)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.setContentView(R.layout.alert_send_email_dialog)
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                val dialogCancelButton =
                    dialog!!.findViewById<View>(R.id.cancelButton) as Button
                val submitButton =
                    dialog!!.findViewById<View>(R.id.submitButton) as Button
                text_dialog = dialog!!.findViewById<View>(R.id.text_dialog) as EditText
                text_content =
                    dialog!!.findViewById<View>(R.id.text_content) as EditText

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
                dialogCancelButton.setOnClickListener { dialog!!.dismiss() }
                submitButton.setOnClickListener {
                    println("submit btn clicked")
                    if (text_dialog!!.text.toString() == "") {
                    appUtils.showDialogAlertDismiss(
                        mContext as Activity?,
                        mContext!!.getString(R.string.alert_heading),
                        "Please enter subject",
                        R.drawable.exclamationicon,
                        R.drawable.round
                    )
                } else if (text_content!!.text.toString() == "") {
                    appUtils.showDialogAlertDismiss(
                        mContext as Activity?,
                        mContext!!.getString(R.string.alert_heading),
                        "Please enter content",
                        R.drawable.exclamationicon,
                        R.drawable.round
                    )
                } else {
                    if (appUtils.checkInternet(mContext)) {
                        println("clicked position count$position")
                        println("Email id Passing" + mStaffList[position].staffEmail)
                        sendEmailToStaff(
                            URLConstants.URL_SEND_EMAIL_TO_STAFF,
                            mStaffList[position].staffEmail
                        )
                    } else {
                        appUtils.showDialogAlertDismiss(
                            mContext as Activity?,
                            "Network Error",
                            mContext!!.getString(R.string.no_internet),
                            R.drawable.nonetworkicon,
                            R.drawable.roundred
                        )
                    }
                }
                }
                dialog!!.show()
            } else {
                appUtils.showDialogAlertDismiss(
                    mContext as Activity?,
                    mContext!!.getString(R.string.alert_heading),
                    "This feature is available only for registered users. Login/register to see contents.",
                    R.drawable.exclamationicon,
                    R.drawable.round
                )
            }
        }
    }

    private fun sendEmailToStaff(urlSendEmailToStaff: String, staffEmail: String?) {
        lateinit var preferenceManager: PreferenceManager
        lateinit var appUtils: AppUtils
        val call: Call<ResponseBody> = ApiClient.getApiService().sendEmailToStaffCall(
            preferenceManager.getAccessToken(mContext),
            staffEmail,
            preferenceManager.getUserId(mContext!!),
            text_dialog!!.text.toString(),
            text_content!!.text.toString()
        )
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseString = response.body()!!.string()
                val jsonObject = JSONObject(responseString)
                val responseCode = jsonObject.getString(JSONConstants.JTAG_RESPONSECODE)
                if (responseCode == "200") {
                    val responseJSONObject =
                        jsonObject.getJSONObject(JSONConstants.JTAG_RESPONSE)
                    val statusCode = responseJSONObject.getString(JSONConstants.JTAG_STATUSCODE)
                    if (statusCode.equals(StatusConstants.STATUS_SUCCESS, ignoreCase = true)) {
                        dialog!!.dismiss()
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
                    sendEmailToStaff(URLConstants.URL_SEND_EMAIL_TO_STAFF, staffEmail)

                } else if(responseCode.equals("400")) {
                    appUtils.getToken(mContext!!)
                    sendEmailToStaff(URLConstants.URL_SEND_EMAIL_TO_STAFF, staffEmail)

                } else if(responseCode.equals("401")) {
                    appUtils.getToken(mContext!!)
                    sendEmailToStaff(URLConstants.URL_SEND_EMAIL_TO_STAFF, staffEmail)

                } else if(responseCode.equals("402")) {
                    appUtils.getToken(mContext!!)
                    sendEmailToStaff(URLConstants.URL_SEND_EMAIL_TO_STAFF, staffEmail)

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
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}