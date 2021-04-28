package com.example.nasmanila.activities.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.nasmanila.R
import com.example.nasmanila.activities.home.HomeListActivity
import com.example.nasmanila.constants.URLConstants.*
import com.example.nasmanila.manager.AppUtils
import com.example.nasmanila.manager.PreferenceManager

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
    private var mProgressBar: ConstraintLayout? = null

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
        mProgressBar = findViewById<View>(R.id.progressDialog) as ConstraintLayout
        mUserNameEdtTxt = findViewById<View>(R.id.userEditText) as EditText
        mUserNameEdtTxt!!.setOnEditorActionListener { v, actionId, event ->
            mUserNameEdtTxt!!.isFocusable = false
            mUserNameEdtTxt!!.isFocusableInTouchMode = false
            false
        }
        mPasswordEdtTxt = findViewById<View>(R.id.passwordEditText) as EditText
        mPasswordEdtTxt!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            mPasswordEdtTxt!!.isFocusable = false
            mPasswordEdtTxt!!.isFocusableInTouchMode = false
            false
        })
        mHelpButton = findViewById<View>(R.id.helpButton) as Button
        mNeedpasswordBtn = findViewById<View>(R.id.forgotPasswordButton) as Button
        mGuestUserButton = findViewById<View>(R.id.guestButton) as Button
        mLoginBtn = findViewById<View>(R.id.loginBtn) as Button
        mSignUpBtn = findViewById<View>(R.id.signUpButton) as Button

        val sdk = Build.VERSION.SDK_INT
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            mNeedpasswordBtn!!.setBackgroundDrawable(
                appUtils
                    .getButtonDrawableByScreenCategory(
                        mContext,
                        R.drawable.forgotpassword,
                        R.drawable.forgotpasswordpress
                    )
            )
            mGuestUserButton!!.setBackgroundDrawable(
                appUtils
                    .getButtonDrawableByScreenCategory(
                        mContext,
                        R.drawable.guest, R.drawable.guestpress
                    )
            )
            mLoginBtn!!.setBackgroundDrawable(
                appUtils
                    .getButtonDrawableByScreenCategory(
                        mContext,
                        R.drawable.login, R.drawable.loginpress
                    )
            )
            mSignUpBtn!!.setBackgroundDrawable(
                appUtils
                    .getButtonDrawableByScreenCategory(
                        mContext,
                        R.drawable.signup_new, R.drawable.signuppress_new
                    )
            )
            mHelpButton!!.setBackgroundDrawable(
                appUtils
                    .getButtonDrawableByScreenCategory(
                        mContext,
                        R.drawable.help, R.drawable.helppress
                    )
            )
        } else {
            mNeedpasswordBtn!!.background = appUtils
                .getButtonDrawableByScreenCategory(
                    mContext,
                    R.drawable.forgotpassword,
                    R.drawable.forgotpasswordpress
                )
            mGuestUserButton!!.background = appUtils
                .getButtonDrawableByScreenCategory(
                    mContext,
                    R.drawable.guest, R.drawable.guestpress
                )
            mLoginBtn!!.background = appUtils
                .getButtonDrawableByScreenCategory(
                    mContext,
                    R.drawable.login, R.drawable.loginpress
                )
            mSignUpBtn!!.background = appUtils
                .getButtonDrawableByScreenCategory(
                    mContext,
                    R.drawable.signup_new, R.drawable.signuppress_new
                )
            mHelpButton!!.background = appUtils
                .getButtonDrawableByScreenCategory(
                    mContext,
                    R.drawable.help, R.drawable.helppress
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
            appUtils.hideKeyboard(mContext, mPasswordEdtTxt)
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
            appUtils.hideKeyboard(mContext, mPasswordEdtTxt)
            preferenceManager.setUserID(mContext, "")
            var homeIntent: Intent = Intent(mContext, HomeListActivity::class.java)
            startActivity(homeIntent)
        }
        mSignUpBtn?.setOnClickListener {
            appUtils.hideKeyboard(mContext, mPasswordEdtTxt)
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
            emailIntent.putExtra(Intent.EXTRA_EMAIL, Uri.parse(deliveryAddress))
            emailIntent.type = "text/plain"
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            var packageManager: PackageManager = v!!.context.packageManager
            val activityList: List<ResolveInfo> = packageManager.queryIntentActivities(
                emailIntent, 0
            )
            for (app in activityList) {
                if (app.activityInfo.name.contains("com.google.android.gm")) {
                    val activity = app.activityInfo
                    val name = ComponentName(
                        activity.applicationInfo.packageName,
                        activity.name
                    )
                    emailIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    emailIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    emailIntent.component = name
                    v.context.startActivity(emailIntent)
                    break
                }
            }
        }
        mNeedpasswordBtn?.setOnClickListener {
            appUtils.hideKeyboard(mContext, mPasswordEdtTxt)
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
            appUtils.hideKeyboard(mContext, mMailEdtText)
            if (!mMailEdtText!!.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                if (appUtils.isValidEmail(mMailEdtText!!.text.toString())) {
                    if (appUtils.checkInternet(mContext!!))
                        sendForGotPassword(URL_FORGOTPASSWORD)
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
            appUtils.hideKeyboard(mContext, mMailEdtText)
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun showSignUpAlertDialog() {
        val dialog: Dialog = Dialog(mContext!!, R.style.NewDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_layout_signup)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        mMailEdtText = dialog.findViewById<View>(R.id.text_dialog) as EditText
        val dialogSubmitButton = dialog.findViewById<View>(R.id.btn_signup) as Button
        dialogSubmitButton.setOnClickListener {
            appUtils.hideKeyboard(mContext, mMailEdtText)
            if (!mMailEdtText!!.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                if (appUtils.isValidEmail(mMailEdtText!!.text.toString())) {
                    if (appUtils.checkInternet(mContext!!))
                        sendSignUpRequest(URL_PARENT_SIGNUP)
                    else
                        appUtils.showDialogAlertDismiss(mContext as Activity?, "Network Error", getString(R.string.no_internet), R.drawable.nonetworkicon, R.drawable.roundred)
                }
                else
                    appUtils.showDialogAlertDismiss(mContext as Activity?, getString(R.string.alert_heading), getString(R.string.enter_valid_email), R.drawable.exclamationicon, R.drawable.round)

            } else {
                appUtils.showDialogAlertDismiss(
                    mContext as Activity?, getString(R.string.alert_heading), getString(R.string.enter_email), R.drawable.exclamationicon, R.drawable.round)
            }
        }

        val dialogMayBelaterutton = dialog.findViewById<View>(R.id.button2) as Button
        dialogMayBelaterutton.setOnClickListener {
            appUtils.hideKeyboard(mContext, mMailEdtText)
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun sendSignUpRequest(URL: String) {
        /**   API CALL   **/
    }
    private fun sendForGotPassword(URL: String) {
        /**   API CALL   **/
    }
    private fun loginApiCall(URL: String) {
        /**   API CALL   **/
    }
}