package com.mobatia.nasmanila.manager


import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.login.LoginActivity
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.NameValueConstants
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AppUtils  {
    lateinit var preferenceManager: PreferenceManager
    fun checkInternet(context: Context): Boolean {
        val connectivityManager =  context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
//        if (netInfo != null && netInfo.isConnectedOrConnecting) 
        return netInfo != null && netInfo.isConnected
//        if (netInfo != null && netInfo.isConnected)
//            return true
//        else
//            return false

    }
//    fun GetAccessTokenInterface {
//        public fun getAccessToken()
//    }
    fun getButtonDrawableByScreenCategory(
        context: Context?,
        normalStateResID: Int,
        pressedStateResID: Int
): Drawable {
    val stateNormal: Drawable = context?.resources?.getDrawable(normalStateResID)!!.mutate()
    val statePressed: Drawable = context?.resources?.getDrawable(pressedStateResID)!!.mutate()
    val drawable = StateListDrawable()
    drawable.addState(intArrayOf(android.R.attr.state_pressed), statePressed)
    drawable.addState(intArrayOf(android.R.attr.state_enabled), stateNormal)
    return drawable
    }

    fun showDialogAlertDismiss(
            context: Context?,
            msgHead: String?,
            msg: String?,
            ico: Int,
            bgIcon: Int
    ) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_dialogue_ok_layout)
        val icon = dialog.findViewById<View>(R.id.iconImageView) as ImageView
        icon.setBackgroundResource(bgIcon)
        icon.setImageResource(ico)
        val text = dialog.findViewById<View>(R.id.textDialog) as TextView
        val textHead = dialog.findViewById<View>(R.id.alertHead) as TextView
        text.text = msg
        textHead.text = msgHead
        val  dialogButton = dialog.findViewById<View>(R.id.btnOK) as Button
        dialogButton.setOnClickListener{dialog.dismiss()}
        dialog.show()
    }

    fun hideKeyboard(context: Context?) {
//        if (editText != null) {
//            val imm = context
//                    ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(editText.windowToken, 0)
//        }
        val imm = context
                ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (imm.isAcceptingText) {

            imm.hideSoftInputFromWindow((context as Activity).currentFocus
                    ?.windowToken, 0)
        }
    }

    fun isValidEmail(string: String): Boolean {
        return if (string == null) {
            false
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(string).matches()
        }
    }
    fun getToken(context: Context) {
        val call: Call<ResponseBody> = ApiClient.getApiService().accessToken(
                NameValueConstants.VALUE_GRANT_TYPE,
                NameValueConstants.VALUE_CLIENT_ID,
                NameValueConstants.VALUE_CLIENT_SECRET,
                NameValueConstants.VALUE_USERNAME,
                NameValueConstants.VALUE_PASSWORD
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseData = response.body()
                if (responseData != null) {
                    val jsonObject = JSONObject(responseData.string())
                    if (jsonObject != null) {
                        val accessToken: String = jsonObject.optString("access_token")
                        preferenceManager.setAccessToken(context, accessToken)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

        })
    }

    fun replace(s: String): String {
        return s.replace(" ".toRegex(), "%20")
    }

    fun getVersionInfo(mContext: Context): Any {
        var versionName = ""
        var versionCode = -1
            val packageInfo = mContext.packageManager.getPackageInfo(mContext.packageName, 0)
            versionName = packageInfo.versionName
            versionCode = packageInfo.versionCode
        return versionName
    }

    fun showDialogAlertUpdate(mContext: Context) {
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_update_version)
        val btnUpdate = dialog.findViewById<View>(R.id.btnUpdate) as Button
        btnUpdate.setOnClickListener {
            dialog.dismiss()
            val appPackageName =
                mContext.packageName
            try {
                mContext.startActivity(
                        Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$appPackageName")
                        )
                )
            } catch (anfe: ActivityNotFoundException) {
                mContext.startActivity(
                        Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                )
            }
        }
        dialog.show()
    }

    fun showDialogAlertLogout(
            activity: FragmentActivity?,
            s: String,
            s1: String,
            questionMarkIcon: Int,
            round: Int
    ) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_dialogue_layout)
        val icon = dialog.findViewById<View>(R.id.iconImageView) as ImageView
        icon.setBackgroundResource(round)
        icon.setImageResource(questionMarkIcon)
        val text = dialog.findViewById<View>(R.id.text_dialog) as TextView
        val textHead = dialog.findViewById<View>(R.id.alertHead) as TextView
        text.text = s
        textHead.text = s1

        val dialogButton = dialog.findViewById<View>(R.id.btn_Ok) as Button
        dialogButton.setOnClickListener {
            if (preferenceManager.getUserId(activity)
                    .equals("", ignoreCase = true)
            ) {
                preferenceManager.setUserId(activity, "")
                dialog.dismiss()
                val mIntent = Intent(activity, LoginActivity::class.java)
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                activity.startActivity(mIntent)
            } else {
                callLogoutApi(activity, dialog)
            }
        }
        val dialogButtonCancel = dialog.findViewById<View>(R.id.btn_Cancel) as Button
        dialogButtonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun callLogoutApi(activity: Activity, dialog: Dialog) {
        val call: Call<ResponseBody> = ApiClient.getApiService().logOut(
                preferenceManager.getAccessToken(activity),
                preferenceManager.getUserId(activity),
                FirebaseInstanceId.getInstance().token!!,
                "2"
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseString = response.body()!!.string()
                val jsonObject = JSONObject(responseString)
                val responseCode = jsonObject.getString(JSONConstants.JTAG_RESPONSECODE)
                if (responseCode.equals("200")) {
                    val responseJSONObject = jsonObject.getJSONObject(JSONConstants.JTAG_RESPONSE)
                    val statusCode = responseJSONObject.getString(JSONConstants.JTAG_STATUSCODE)
                    if (statusCode.equals("303")) {
                        dialog.dismiss()
                        preferenceManager.setUserId(activity, "")
                        val mIntent = Intent(activity, LoginActivity::class.java)
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        activity.startActivity(mIntent)
                    }
                } else if (responseCode.equals("500", ignoreCase = true)) {
                    dialog.dismiss()
                    showDialogAlertDismiss(
                            activity as Activity?,
                            "Alert",
                            activity.getString(R.string.common_error),
                            R.drawable.exclamationicon,
                            R.drawable.round
                    )
                } else if (responseCode.equals("400", ignoreCase = true)) {
                    getToken(activity)
                } else if (responseCode.equals("401", ignoreCase = true)) {
                    getToken(activity)
                } else if (responseCode.equals("402", ignoreCase = true)) {
                    getToken(activity)
                } else {
                    dialog.dismiss()
                    showDialogAlertDismiss(
                            activity,
                            "Alert",
                            activity.getString(R.string.common_error),
                            R.drawable.exclamationicon,
                            R.drawable.round
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                dialog.dismiss()
                showDialogAlertDismiss(
                        activity,
                        "Alert",
                        activity.getString(R.string.common_error),
                        R.drawable.exclamationicon,
                        R.drawable.round
                )
            }

        })
    }

    fun showAlertFinish(activity: Activity?, message: String, okBtnTitle: String, cancelBtnTitle: String, okBtnVisibility: Boolean) {


        val dialog = Dialog(activity!!, R.style.NewDialog)
        dialog.setContentView(R.layout.custom_alert_dialog)
        dialog.setCancelable(false)
        val text = dialog.findViewById<View>(R.id.text) as TextView
        text.text = message
        val sdk = Build.VERSION.SDK_INT

        val dialogCancelButton = dialog
            .findViewById<View>(R.id.dialogButtonCancel) as Button
        dialogCancelButton.text = cancelBtnTitle

        dialogCancelButton.setOnClickListener {
            dialog.dismiss()
            activity.finish()
        }

        val dialogOkButton = dialog
            .findViewById<View>(R.id.dialogButtonOK) as Button
        dialogOkButton.visibility = View.GONE
        dialogOkButton.text = okBtnTitle
        if (okBtnVisibility) {
            dialogOkButton.visibility = View.VISIBLE
            dialogOkButton.setOnClickListener {
                dialog.dismiss()
                activity.finish()
            }
        }
        dialog.show()
    }

    fun durationInSecondsToString(sec: Int): CharSequence? {
        val hours: Int = sec / 3600
        val minutes: Int = sec / 60 - hours * 60
        val seconds: Int = sec - hours * 3600 - minutes * 60
        return String.format("%d:%02d:%02d", hours, minutes, seconds)
    }

    fun dateParsingToDdMmYyyy(date: String): CharSequence? {
        var strCurrentDate = ""
        var format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        var newDate: Date? = format.parse(date)
        format = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        strCurrentDate = format.format(newDate)
        return strCurrentDate
    }

    fun showDialogAlertFinish(activity: Activity?, msg: String, msgHead: String, ico: Int, bgIcon: Int) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_dialogue_ok_layout)
        val icon = dialog.findViewById<View>(R.id.iconImageView) as ImageView
        icon.setBackgroundResource(bgIcon)
        icon.setImageResource(ico)
        val text = dialog.findViewById<View>(R.id.text_dialog) as TextView
        val textHead = dialog.findViewById<View>(R.id.alertHead) as TextView
        text.text = msg
        textHead.text = msgHead

        val dialogButton = dialog.findViewById<View>(R.id.btn_Ok) as Button
        dialogButton.setOnClickListener {
            dialog.dismiss()
            activity!!.finish()
        }

        dialog.show()
    }
}