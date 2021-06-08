package com.mobatia.nasmanila.fragments.absences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.HomeListActivity
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.HeaderManager
import com.mobatia.nasmanila.manager.PreferenceManager

class LeavesDetailActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    var relativeHeader: RelativeLayout? = null
    var headermanager: HeaderManager? = null
    var back: ImageView? = null
    var home: ImageView? = null
    var mContext: Context = this
    var fromlayout: LinearLayout? = null
    var reasonlayout:LinearLayout? = null
    var stnameValue: TextView? = null
    var leaveDateFromValue:TextView? = null
    var leaveDateToValue:TextView? = null
    var reasonValue:TextView? = null
    var studClassValue:TextView? = null
    var extras: Bundle? = null
    var position = 0
    var studentNameStr = ""
    var studentClassStr = ""
    var fromDate = ""
    var toDate = ""
    var reasonForAbsence = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaves_detail)
        initUI()
        setValues()
    }

    private fun setValues() {
        stnameValue!!.text = studentNameStr
        studClassValue!!.text = studentClassStr
        leaveDateFromValue!!.text=(appUtils.dateParsingToDdMmYyyy(fromDate))
        leaveDateToValue!!.text=(appUtils.dateParsingToDdMmYyyy(toDate))
        reasonValue!!.text = reasonForAbsence
    }

    private fun initUI() {
        extras = intent.extras
        if (extras != null) {
            studentNameStr = extras!!.getString("studentName")!!
            studentClassStr = extras!!.getString("studentClass")!!
            fromDate = extras!!.getString("fromDate")!!
            toDate = extras!!.getString("toDate")!!
            reasonForAbsence = extras!!.getString("reasonForAbsence")!!
        }
        relativeHeader = findViewById<View>(R.id.relativeHeader) as RelativeLayout
        fromlayout = findViewById<View>(R.id.fromlayout) as LinearLayout
        reasonlayout = findViewById<View>(R.id.reasonlayout) as LinearLayout
        stnameValue = findViewById<View>(R.id.stnameValue) as TextView
        studClassValue = findViewById<View>(R.id.studClassValue) as TextView
        leaveDateFromValue = findViewById<View>(R.id.leaveDateFromValue) as TextView
        leaveDateToValue = findViewById<View>(R.id.leaveDateToValue) as TextView
        reasonValue = findViewById<View>(R.id.reasonValue) as TextView

        headermanager = HeaderManager(this@LeavesDetailActivity, NaisClassNameConstants.ABSENCE)
        headermanager!!.getHeader(relativeHeader!!, 0)

        back = headermanager!!.getLeftButton()
        headermanager!!.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        home = headermanager!!.getLogoButton()
        home!!.setOnClickListener {
            val `in` = Intent(mContext, HomeListActivity::class.java)
            `in`.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(`in`)
        }
        back!!.setOnClickListener { finish() }
    }
}