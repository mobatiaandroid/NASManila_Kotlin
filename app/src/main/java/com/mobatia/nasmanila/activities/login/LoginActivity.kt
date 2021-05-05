package com.mobatia.nasmanila.activities.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.HomeListActivity
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants.*
import com.mobatia.nasmanila.constants.URLConstants.*
import com.mobatia.nasmanila.manager.PreferenceManager
import com.mobatia.nasmanila.manager.AppUtils
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private var mContext: Context? = null
    private var mUserNameEdtTxt: EditText? = null
    private var mPasswordEdtTxt: EditText? = null
    private var mNeedpasswordBtn: Button? = null
    private var mGuestUserButton: Button? = null
    private var mLoginBtn: Button? = null
    private var mSignUpBtn: Button? = null
    private var mMailEdtText: EditText? = null
    private var mHelpButton: Button? = null
    private var mProgressBar: ProgressBar? = null
    lateinit var dialog: Dialog
    lateinit var preferenceManager: PreferenceManager
    lateinit var appUtils: AppUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mContext = this
        preferenceManager = PreferenceManager()
        appUtils = AppUtils()
        preferenceManager.setIsFirstLaunch(mContext as LoginActivity, false)
        initialiseUI()
        setListeners()

    }
    private fun initialiseUI() {
        mProgressBar = findViewById<View>(R.id.progressBar) as ProgressBar?
        mUserNameEdtTxt = findViewById<View>(R.id.userEditText) as EditText
        dialog = Dialog(mContext!!, R.style.NewDialog)
        mProgressBar = findViewById(R.id.progressBar)
        mUserNameEdtTxt!!.setOnEditorActionListener { v, actionId, event ->
            mUserNameEdtTxt!!.isFocusable = false
            mUserNameEdtTxt!!.isFocusableInTouchMode = false
            false
        }
        mPasswordEdtTxt = findViewById<View>(R.id.passwordEditText) as EditText
        mPasswordEdtTxt!!.setOnEditorActionListener { v, actionId, event ->
            mPasswordEdtTxt!!.isFocusable = false
            mPasswordEdtTxt!!.isFocusableInTouchMode = false
            false
        }
        mHelpButton = findViewById<View>(R.id.helpButton) as Button
        mNeedpasswordBtn = findViewById<View>(R.id.forgotPasswordButton) as Button
        mGuestUserButton = findViewById<View>(R.id.guestButton) as Button
        mLoginBtn = findViewById<View>(R.id.loginBtn) as Button
        mSignUpBtn = findViewById<View>(R.id.signUpButton) as Button

        val sdk = Build.VERSION.SDK_INT
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            mNeedpasswordBtn!!.setBackgroundDrawable(
                    appUtils.getButtonDrawableByScreenCategory(
                            mContext,
                            R.drawable.forgotpassword,
                            R.drawable.forgotpasswordpress
                    )
            )
            mGuestUserButton!!.setBackgroundDrawable(
                    appUtils.getButtonDrawableByScreenCategory(
                            mContext,
                            R.drawable.guest,
                            R.drawable.guestpress
                    )
            )
            mLoginBtn!!.setBackgroundDrawable(
                    appUtils.getButtonDrawableByScreenCategory(
                            mContext,
                            R.drawable.login,
                            R.drawable.loginpress
                    )
            )
            mSignUpBtn!!.setBackgroundDrawable(
                    appUtils.getButtonDrawableByScreenCategory(
                            mContext,
                            R.drawable.signup_new,
                            R.drawable.signuppress_new
                    )
            )
            mHelpButton!!.setBackgroundDrawable(
                    appUtils.getButtonDrawableByScreenCategory(
                            mContext,
                            R.drawable.help,
                            R.drawable.helppress
                    )
            )
        } else {
            mNeedpasswordBtn!!.background = appUtils.getButtonDrawableByScreenCategory(
                    mContext,
                    R.drawable.forgotpassword,
                    R.drawable.forgotpasswordpress
            )
            mGuestUserButton!!.background = appUtils.getButtonDrawableByScreenCategory(
                    mContext,
                    R.drawable.guest,
                    R.drawable.guestpress
            )
            mLoginBtn!!.background = appUtils.getButtonDrawableByScreenCategory(
                    mContext,
                    R.drawable.login,
                    R.drawable.loginpress
            )
            mSignUpBtn!!.background = appUtils.getButtonDrawableByScreenCategory(
                    mContext,
                    R.drawable.signup_new,
                    R.drawable.signuppress_new
            )
            mHelpButton!!.background = appUtils.getButtonDrawableByScreenCategory(
                    mContext,
                    R.drawable.help,
                    R.drawable.helppress
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        mUserNameEdtTxt?.setOnTouchListener { v, event ->
            mUserNameEdtTxt!!.isFocusable = true
            mUserNameEdtTxt!!.isFocusableInTouchMode = true
            false
        }
        mPasswordEdtTxt?.setOnTouchListener { v, event ->
            mPasswordEdtTxt!!.isFocusable = true
            mPasswordEdtTxt!!.isFocusableInTouchMode = true
            false
        }
        mLoginBtn?.setOnClickListener {
            appUtils.hideKeyboard(mContext)
            if (mUserNameEdtTxt?.text.toString().trim().equals("", ignoreCase = true))
                appUtils.showDialogAlertDismiss(
                        mContext as Activity?, getString(R.string.alert_heading), getString(
                        R.string.enter_email
                ), R.drawable.exclamationicon, R.drawable.round
                )
            else if (!appUtils.isValidEmail(mUserNameEdtTxt?.text.toString()))
                appUtils.showDialogAlertDismiss(
                        mContext as Activity?, getString(R.string.alert_heading), getString(
                        R.string.enter_valid_email
                ), R.drawable.exclamationicon, R.drawable.round
                )
            else if (mPasswordEdtTxt!!.text.toString().equals("", ignoreCase = true))
                appUtils.showDialogAlertDismiss(
                        mContext as Activity?, getString(R.string.alert_heading), getString(
                        R.string.enter_password
                ), R.drawable.exclamationicon, R.drawable.round
                )
            else
                loginApiCall(URL_LOGIN)
        }
        mGuestUserButton?.setOnClickListener {
            appUtils.hideKeyboard(mContext)
            preferenceManager.setUserID(mContext, "")
            var homeIntent: Intent = Intent(mContext, HomeListActivity::class.java)
            startActivity(homeIntent)
        }
        mSignUpBtn?.setOnClickListener {
            appUtils.hideKeyboard(mContext)
            if (mContext?.let { appUtils.checkInternet(it) } == true)
                showSignUpAlertDialog()
            else
                appUtils.showDialogAlertDismiss(
                        mContext as Activity?, "Network Error", getString(
                        R.string.no_internet
                ), R.drawable.nonetworkicon, R.drawable.roundred
                )
        }
        mHelpButton?.setOnClickListener { v ->
            var emailIntent: Intent = Intent(Intent.ACTION_SEND)
            var deliveryAddress = "appsupport@naismanila.edu.ph"
//            emailIntent.putExtra(Intent.EXTRA_EMAIL, Uri.parse(deliveryAddress))
//            emailIntent.data = Uri.parse("mailto:")
//            emailIntent.type = "text/plain"
//            emailIntent.putExtra(Intent.EXTRA_EMAIL, "appsupport@naismanila.edu.ph")
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "The subject");
//            emailIntent.putExtra(Intent.EXTRA_TEXT, "The email body");
//            emailIntent.putExtra(Intent.EXTRA_CC,"sabusanju2@gmail.com")
//            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            var packageManager: PackageManager = v!!.context.packageManager
//            v.context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$deliveryAddress"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "subject")
            intent.putExtra(Intent.EXTRA_TEXT, "message")
            v.context.startActivity(intent)
//            val activityList: List<ResolveInfo> = packageManager.queryIntentActivities(emailIntent, 0)
//            for (app in activityList) {
//                if (app.activityInfo.name.contains("com.google.android.gm")) {
//                    val activity = app.activityInfo
//                    val name = ComponentName(
//                            activity.applicationInfo.packageName,
//                            activity.name
//                    )
//                    emailIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//                    emailIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
//                            or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
//                    emailIntent.component = name
//                    v.context.startActivity(emailIntent)
//                    break
//                }
//            }
        }
        mNeedpasswordBtn?.setOnClickListener {
            appUtils.hideKeyboard(mContext)
            if (mContext?.let { appUtils.checkInternet(it) } == true)
                forgotPasswordApiCall()
            else
                appUtils.showDialogAlertDismiss(
                        mContext as Context, "Network Error", getString(
                        R.string.no_internet
                ), R.drawable.nonetworkicon, R.drawable.roundred
                )
        }
    }

    private fun forgotPasswordApiCall() {
        val dialog: Dialog = Dialog(mContext!!, R.style.NewDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_forgot_password)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        mMailEdtText = dialog.findViewById<View>(R.id.text_dialog) as EditText
//        mMailEdtText!!.setOnTouchListener(this)
        val alertHead = dialog.findViewById<View>(R.id.alertHead) as TextView
        val dialogSubmitButton = dialog.findViewById<View>(R.id.btn_signup) as Button
        dialogSubmitButton.setOnClickListener {
            appUtils.hideKeyboard(mContext)
            if (!mMailEdtText!!.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                if (appUtils.isValidEmail(mMailEdtText!!.text.toString())) {
                    if (appUtils.checkInternet(mContext!!))
                        sendForgotPassword(URL_FORGOTPASSWORD)
                    else
                        appUtils.showDialogAlertDismiss(
                                mContext as Activity?, "Network Error", getString(
                                R.string.no_internet
                        ), R.drawable.nonetworkicon, R.drawable.roundred
                        )
                    dialog.dismiss()
                } else 
                    appUtils.showDialogAlertDismiss(
                            mContext as Activity?, getString(R.string.alert_heading), getString(
                            R.string.invalid_email
                    ), R.drawable.exclamationicon, R.drawable.round
                    )
            } else 
                appUtils.showDialogAlertDismiss(
                        mContext as Activity?, getString(R.string.alert_heading), getString(
                        R.string.enter_email
                ), R.drawable.exclamationicon, R.drawable.round
                )
        }
        val dialogMaybeLaterutton = dialog.findViewById<View>(R.id.button2) as Button
        dialogMaybeLaterutton.setOnClickListener {
            appUtils.hideKeyboard(mContext)
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun showSignUpAlertDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_layout_signup)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        mMailEdtText = dialog.findViewById<View>(R.id.text_dialog) as EditText
        val dialogSubmitButton = dialog.findViewById<View>(R.id.btn_signup) as Button
        dialogSubmitButton.setOnClickListener {
            appUtils.hideKeyboard(mContext)
            if (!mMailEdtText!!.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                if (appUtils.isValidEmail(mMailEdtText!!.text.toString())) {
                    if (appUtils.checkInternet(mContext!!))
                        sendSignUpRequest(URL_PARENT_SIGNUP)
                    else
                        appUtils.showDialogAlertDismiss(
                                mContext as Activity?, "Network Error", getString(
                                R.string.no_internet
                        ), R.drawable.nonetworkicon, R.drawable.roundred
                        )
                }
                else
                    appUtils.showDialogAlertDismiss(
                            mContext as Activity?, getString(R.string.alert_heading), getString(
                            R.string.enter_valid_email
                    ), R.drawable.exclamationicon, R.drawable.round
                    )
            } else {
                appUtils.showDialogAlertDismiss(
                        mContext as Activity?, getString(R.string.alert_heading), getString(
                        R.string.enter_email
                ), R.drawable.exclamationicon, R.drawable.round
                )
            }
        }

        val dialogMayBelaterutton = dialog.findViewById<View>(R.id.button2) as Button
        dialogMayBelaterutton.setOnClickListener {
            appUtils.hideKeyboard(mContext)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun sendForgotPassword(URL: String) {
        val call: Call<ResponseBody> = ApiClient.getApiService().forgotPassword(
                preferenceManager.getAccessToken(mContext),
                mMailEdtText!!.text.toString(),
                FirebaseInstanceId.getInstance().id,
                "2"
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                val responseData = response.body()
//                if (responseData != null) {

                val responseString = response.body()!!.string()
                val jsonObject = JSONObject(responseString)
                val responseCode = jsonObject.getString(JTAG_RESPONSECODE)
                if (responseCode.equals("200")) {
                    val responseJSONObject = jsonObject.getJSONObject(JTAG_RESPONSE)
                    val statusCode = responseJSONObject.getString(JTAG_STATUSCODE)
                    if (statusCode.equals("303", ignoreCase = true)) {
                        appUtils.showDialogAlertDismiss(
                                mContext as Activity?, "Success", getString(
                                R.string.frgot_success_alert
                        ), R.drawable.tick, R.drawable.round
                        )
                    } else if (statusCode.equals("301", ignoreCase = true)) {
                        appUtils.showDialogAlertDismiss(
                                mContext as Activity?, getString(R.string.error_heading), getString(
                                R.string.missing_parameter
                        ), R.drawable.infoicon, R.drawable.round
                        )
                    } else if (statusCode.equals("304", ignoreCase = true)) {
                        appUtils.showDialogAlertDismiss(
                                mContext as Activity?, getString(R.string.error_heading), getString(
                                R.string.email_exists
                        ), R.drawable.infoicon, R.drawable.round
                        )
                    } else if (statusCode.equals("305", ignoreCase = true)) {
                        appUtils.showDialogAlertDismiss(
                                mContext as Activity?, getString(R.string.error_heading), getString(
                                R.string.incrct_usernamepswd
                        ), R.drawable.exclamationicon, R.drawable.round
                        )
                    } else if (statusCode.equals("306", ignoreCase = true)) {
                        appUtils.showDialogAlertDismiss(
                                mContext as Activity?, getString(R.string.error_heading), getString(
                                R.string.invalid_email
                        ), R.drawable.exclamationicon, R.drawable.round
                        )
                    } else {
                        appUtils.showDialogAlertDismiss(
                                mContext as Activity?, getString(R.string.error_heading), getString(
                                R.string.common_error
                        ), R.drawable.exclamationicon, R.drawable.round
                        )
                    }
                } else if (responseCode.equals("500", ignoreCase = true)) {
                    appUtils.showDialogAlertDismiss(
                            mContext as Activity?, "Alert", mContext!!.getString(
                            R.string.common_error
                    ), R.drawable.exclamationicon, R.drawable.round
                    )
                } else if (responseCode.equals("400", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    sendForgotPassword(URL_FORGOTPASSWORD)
                } else if (responseCode.equals("401", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    sendForgotPassword(URL_FORGOTPASSWORD)
                } else if (responseCode.equals("402", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    sendForgotPassword(URL_FORGOTPASSWORD)
                } else
                    appUtils.showDialogAlertDismiss(
                            mContext as Activity?, "Alert", mContext!!.getString(
                            R.string.common_error
                    ), R.drawable.exclamationicon, R.drawable.round
                    )
//                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }
        })
    }
    private fun sendSignUpRequest(URL: String) {
        val call: Call<ResponseBody> = ApiClient.getApiService().signUp(
                preferenceManager.getAccessToken(mContext),
                mMailEdtText!!.text.toString(),
                FirebaseInstanceId.getInstance().id,
                "2"
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                val responseData = response.body()
//                if (responseData != null)
                try {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val responseCode: String = jsonObject.getString(JTAG_RESPONSECODE)
                    if (responseCode.equals("200", ignoreCase = true)) {
                        val responseJSONObject: JSONObject = jsonObject.getJSONObject(JTAG_RESPONSE)
                        val statusCode = responseJSONObject.getString(JTAG_STATUSCODE)
                        if (statusCode.equals("303", ignoreCase = true)) {
                            dialog.dismiss()
                            appUtils.showDialogAlertDismiss(
                                    mContext as Activity?,
                                    "Success",
                                    getString(R.string.signup_success_alert),
                                    R.drawable.tick,
                                    R.drawable.round
                            )
                        } else if (statusCode.equals("301", ignoreCase = true)) {
                            appUtils.showDialogAlertDismiss(
                                    mContext as Activity?,
                                    getString(R.string.error_heading),
                                    getString(R.string.missing_parameter),
                                    R.drawable.infoicon,
                                    R.drawable.round
                            )
                        } else if (statusCode.equals("304", ignoreCase = true)) {
                            appUtils.showDialogAlertDismiss(
                                    mContext as Activity?,
                                    getString(R.string.error_heading),
                                    getString(R.string.email_exists),
                                    R.drawable.infoicon,
                                    R.drawable.round
                            )
                        } else if (statusCode.equals("306", ignoreCase = true)) {
                            appUtils.showDialogAlertDismiss(
                                    mContext as Activity?,
                                    getString(R.string.error_heading),
                                    getString(R.string.invalid_email_first) + mMailEdtText!!.text.toString() + getString(
                                            R.string.invalid_email_last
                                    ),
                                    R.drawable.exclamationicon,
                                    R.drawable.round
                            )
                        }
                    } else if (responseCode.equals("500", ignoreCase = true)) {
                        appUtils.showDialogAlertDismiss(
                                mContext as Activity?,
                                "Alert",
                                mContext!!.getString(R.string.common_error),
                                R.drawable.exclamationicon,
                                R.drawable.round
                        )
                    } else if (responseCode.equals("400", ignoreCase = true)) {
                        appUtils.getToken(mContext!!)
                        sendSignUpRequest(URL_PARENT_SIGNUP)
                    } else if (responseCode.equals("401", ignoreCase = true)) {
                        appUtils.getToken(mContext!!)
                        sendSignUpRequest(URL_PARENT_SIGNUP)
                    } else if (responseCode.equals("402", ignoreCase = true)) {
                        appUtils.getToken(mContext!!)
                        sendSignUpRequest(URL_PARENT_SIGNUP)
                    } else {
                        appUtils.showDialogAlertDismiss(
                                mContext as Activity?,
                                "Alert",
                                mContext!!.getString(R.string.common_error),
                                R.drawable.exclamationicon,
                                R.drawable.round
                        )
                    }
                } catch (e: JSONException) {
                    Log.e("JSON Parser", "Error parsing data [" + e.message + "] " + e)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

        })
    }
    private fun loginApiCall(URL: String) {
        val call: Call<ResponseBody> = ApiClient.getApiService().login(
                preferenceManager.getAccessToken(mContext),
                mUserNameEdtTxt!!.text.toString(),
                mPasswordEdtTxt!!.text.toString(),
                FirebaseInstanceId.getInstance().id,
                "2"
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                val responseData = response.body()
//                if (responseData != null) {
                val responseString = response.body()!!.string()
                Log.e("Login Response:", responseString)
                Log.e("Device ID:", FirebaseInstanceId.getInstance().id)
                Log.e("Token:", preferenceManager.getAccessToken(mContext))
                val jsonObject = JSONObject(responseString)
                val responseCode: String = jsonObject.getString(JTAG_RESPONSECODE)
                if (responseCode.equals("200", ignoreCase = true)) {
                    val responseJSONObject: JSONObject = jsonObject.getJSONObject(JTAG_RESPONSE)
                    val statusCode = responseJSONObject.getString(JTAG_STATUSCODE)
                    if (statusCode.equals("303", ignoreCase = true)) {
                        val respObj = responseJSONObject.getJSONObject(JTAG_RESPONSE_ARRAY)
                        preferenceManager.setUserID(mContext, respObj.optString(JTAG_USER_ID))
                        preferenceManager.setUserEmail(mContext, mUserNameEdtTxt!!.text.toString())
                        showDialogSignUpAlert((mContext as Activity?)!!, "Success", "Successfully Logged In", R.drawable.tick, R.drawable.round)
                    } else if (statusCode.equals("301", ignoreCase = true)) {
                        appUtils.showDialogAlertDismiss(mContext as Activity?, getString(R.string.error_heading), getString(R.string.missing_parameter), R.drawable.infoicon, R.drawable.round)
                    } else if (statusCode.equals("304", ignoreCase = true)) {
                        appUtils.showDialogAlertDismiss(mContext as Activity?, getString(R.string.error_heading), getString(R.string.email_exists), R.drawable.infoicon, R.drawable.round)
                    } else if (statusCode.equals("305", ignoreCase = true)) {
                        appUtils.showDialogAlertDismiss(mContext as Activity?, getString(R.string.error_heading), getString(R.string.incrct_usernamepswd), R.drawable.exclamationicon, R.drawable.round)
                    } else if (statusCode.equals("306", ignoreCase = true)) {
                        appUtils.showDialogAlertDismiss(mContext as Activity?, getString(R.string.error_heading), getString(R.string.invalid_email), R.drawable.exclamationicon, R.drawable.round)
                    } else {
                        appUtils.showDialogAlertDismiss(mContext as Activity?, getString(R.string.error_heading), getString(R.string.common_error), R.drawable.exclamationicon, R.drawable.round)
                    }
                } else if (responseCode.equals("500", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    loginApiCall(URL_LOGIN)
                } else if (responseCode.equals("400", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    loginApiCall(URL_LOGIN)
                } else if (responseCode.equals("401", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    loginApiCall(URL_LOGIN)
                } else if (responseCode.equals("402", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    loginApiCall(URL_LOGIN)
                } else {
                    appUtils.showDialogAlertDismiss(mContext as Activity?, "Alert", mContext!!.getString(R.string.common_error), R.drawable.exclamationicon, R.drawable.round)
                }
//                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }
        })
    }
    private fun showDialogSignUpAlert(activity: Activity, messageHead: String, message: String, ico: Int, bgIcon: Int) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_dialogue_ok_layout)
        val icon = dialog.findViewById<View>(R.id.iconImageView) as ImageView
        icon.setBackgroundResource(bgIcon)
        icon.setImageResource(ico)
        val text = dialog.findViewById<View>(R.id.text_dialog) as? TextView
        val textHead = dialog.findViewById<View>(R.id.alertHead) as? TextView
        if (text != null) {
            text.text = message
        }
        if (textHead != null) {
            textHead.text = messageHead
        }

        val dialogButton = dialog.findViewById<View>(R.id.btnOK) as Button
        dialogButton.setOnClickListener {
            dialog.dismiss()
            val homeIntent = Intent(mContext, HomeListActivity::class.java)
            startActivity(homeIntent)
            finish()
        }

        dialog.show()
    }
}