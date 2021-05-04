package com.mobatia.nasmanila.activities.notifications

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.HomeListActivity
import com.mobatia.nasmanila.constants.IntentPassValueConstants
import com.mobatia.nasmanila.fragments.notifications.model.PushNotificationModel
import com.mobatia.nasmanila.manager.HeaderManager
import java.util.*

class TextAlertActivity : AppCompatActivity() {
    var txtmsg: TextView? = null
    var mDateTv: TextView? = null
    var img: ImageView? = null
    var home: ImageView? = null
    var extras: Bundle? = null
    var imglist: ArrayList<PushNotificationModel?>? = null
    var position = 0
    var context: Context = this
    var mActivity: Activity? = null
    var header: RelativeLayout? = null
    var back: ImageView? = null
    var relativeHeader: RelativeLayout? = null
    var headermanager: HeaderManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_alert)
        mActivity = this
        extras = intent.extras
        if (extras != null) {
            position = extras!!.getInt(IntentPassValueConstants.POSITION)
            imglist = extras!!
                .getSerializable(IntentPassValueConstants.PASS_ARRAY_LIST) as ArrayList<PushNotificationModel?>?
        }
        initialiseUI()
    }

    private fun initialiseUI() {
        img = findViewById<View>(R.id.image) as ImageView
        txtmsg = findViewById<View>(R.id.txt) as TextView
        mDateTv = findViewById<View>(R.id.mDateTv) as TextView
        relativeHeader = findViewById<View>(R.id.relativeHeader) as RelativeLayout
        headermanager = HeaderManager(mActivity, "Notification")
        headermanager!!.getHeader(relativeHeader!!, 0)
        back = headermanager!!.getLeftButton()
        headermanager!!.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        back!!.setOnClickListener { finish() }
        home = headermanager!!.getLogoButton()
        home!!.setOnClickListener {
            val `in` = Intent(mActivity, HomeListActivity::class.java)
            `in`.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(`in`)
        }
        setDetails()
    }

    private fun setDetails() {
        txtmsg!!.text = Html.fromHtml(
            imglist!![position]!!.pushTitle!!.replace("\n", "<br>").replace(" ", "&nbsp;")
        )
        mDateTv!!.text =
            imglist!![position]!!.day + "-" + imglist!![position]!!.monthString + "-" + imglist!![position]!!.year + " " + imglist!![position]!!.pushTime
    }
}