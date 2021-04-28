package com.example.nasmanila.manager


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.net.ConnectivityManager
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.nasmanila.R
class AppUtils {
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
    val stateNormal: Drawable = context?.resources
            ?.getDrawable(normalStateResID)!!.mutate()
    val statePressed: Drawable = context?.resources
            ?.getDrawable(pressedStateResID)!!.mutate()
    val drawable = StateListDrawable()

    drawable.addState(
        intArrayOf(android.R.attr.state_pressed),
        statePressed
    )
    drawable.addState(
        intArrayOf(android.R.attr.state_enabled),
        stateNormal
    )
    return drawable
    }
    fun showDialogAlertDismiss(context: Context?, msgHead: String?, msg: String?, ico: Int, bgIcon: Int) {
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

    fun hideKeyboard(context: Context?, editText: EditText?) {
        if (editText != null) {
            val imm = context
                    ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }

    fun isValidEmail(string: String): Boolean {
        return if (string == null) {
            false
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(string).matches()
        }
    }

}