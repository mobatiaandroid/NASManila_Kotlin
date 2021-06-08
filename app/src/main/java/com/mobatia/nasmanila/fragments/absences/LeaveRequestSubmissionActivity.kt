package com.mobatia.nasmanila.fragments.absences

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.HomeListActivity
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.constants.URLConstants
import com.mobatia.nasmanila.fragments.parents_meeting.model.StudentModel
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.HeaderManager
import com.mobatia.nasmanila.manager.PreferenceManager
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class LeaveRequestSubmissionActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    var submitBtn: Button? = null
    var relativeHeader: RelativeLayout? = null

    var headermanager: HeaderManager? = null
    var back: ImageView? = null
    var home: ImageView? = null

    var mContext: Context? = null
    var enterMessage: EditText? = null
    var enterStartDate: TextView? = null
    var enterEndDate:TextView? = null

    var submitLayout: LinearLayout? = null
    var c: Calendar? = null
    var mYear = 0
    var mMonth = 0
    var mDay = 0
    var df: SimpleDateFormat? = null
    var formattedDate: String? = null
    var calendar: Calendar? = null
    var fromDate = ""
    var toDate:kotlin.String? = ""
    var tomorrowAsString: String? = null
    var sdate: Date? = null
    var edate: Date? = null
    var elapsedDays: Long = 0
    var studImg: ImageView? = null
    var stud_img = ""
    var outputFormats: SimpleDateFormat? = null
    var extras: Bundle? = null
    var studentNameStr = ""
    var studentClassStr = ""
    var studentIdStr = ""
    var studentName: TextView? = null
    var mStudentSpinner: LinearLayout? = null
    var studentsModelArrayList: ArrayList<StudentModel> = ArrayList<StudentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_request_submission)
        mContext = this
        initUI()
    }

    private fun initUI() {
        extras = intent.extras
        if (extras != null) {
            studentNameStr = extras!!.getString("studentName")!!
            studentIdStr = extras!!.getString("studentId")!!
            studentsModelArrayList = (extras!!
                .getSerializable("StudentModelArray") as ArrayList<StudentModel>?)!!
            stud_img = extras!!.getString("studentImage")!!
        }

        calendar = Calendar.getInstance()
        outputFormats = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        relativeHeader = findViewById<View>(R.id.relativeHeader) as RelativeLayout

        mStudentSpinner = findViewById<View>(R.id.studentSpinner) as LinearLayout
        studentName = findViewById<View>(R.id.studentName) as TextView

        enterMessage = findViewById<View>(R.id.enterMessage) as EditText
        enterStartDate = findViewById<View>(R.id.enterStratDate) as TextView

        enterEndDate = findViewById<View>(R.id.enterEndDate) as TextView
        submitLayout = findViewById<View>(R.id.submitLayout) as LinearLayout
        submitBtn = findViewById<View>(R.id.submitBtn) as Button

        headermanager =
            HeaderManager(this@LeaveRequestSubmissionActivity, NaisClassNameConstants.ABSENCE)
        headermanager!!.getHeader(relativeHeader!!, 0)

        back = headermanager!!.getLeftButton()

        home = headermanager!!.getLogoButton()
        home!!.setOnClickListener {
            val `in` = Intent(mContext, HomeListActivity::class.java)
            `in`.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(`in`)
        }
        headermanager!!.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )

        back!!.setOnClickListener { finish() }
        mStudentSpinner!!.setOnClickListener {
            if (studentsModelArrayList.size > 0) {
                showSocialmediaList(studentsModelArrayList)
            } else {
                appUtils.showDialogAlertDismiss(
                    mContext as Activity?,
                    "Alert",
                    getString(R.string.student_not_available),
                    R.drawable.exclamationicon,
                    R.drawable.round
                )
            }
        }
        studentName!!.text = studentNameStr
        studImg = findViewById<View>(R.id.studImg) as ImageView
        if (stud_img != "") {
            Glide.with(mContext!!).load(appUtils.replace(stud_img)).placeholder(R.drawable.boy)
                .into(studImg!!)
        } else {
            studImg!!.setImageResource(R.drawable.boy)
        }
        preferenceManager.setLeaveStudentId(mContext!!, studentIdStr)

        enterMessage!!.clearFocus()
        enterMessage!!.isFocusable = false

        enterMessage!!.setOnTouchListener { v, event ->
            enterMessage!!.isFocusableInTouchMode = true
            false
        }
submitBtn!!.setOnClickListener {

            if (appUtils.isEditTextFocused(this@LeaveRequestSubmissionActivity)) {
                appUtils.hideKeyboard(mContext)
            }
            if (enterStartDate!!.text.toString() == "") {
                appUtils.showDialogAlertDismiss(
                    mContext as Activity?,
                    getString(R.string.alert_heading),
                    getString(R.string.enter_startdate),
                    R.drawable.infoicon,
                    R.drawable.round
                )
            } else if (enterEndDate!!.text.toString() == "") {
                appUtils.showDialogAlertDismiss(
                    mContext as Activity?,
                    getString(R.string.alert_heading),
                    getString(R.string.enter_enddate),
                    R.drawable.infoicon,
                    R.drawable.round
                )
            } else if (enterMessage!!.text.toString().trim { it <= ' ' } == "") {
                appUtils.showDialogAlertDismiss(
                    mContext as Activity?,
                    getString(R.string.alert_heading),
                    getString(R.string.enter_reason),
                    R.drawable.infoicon,
                    R.drawable.round
                )
            } else {
                if (appUtils.checkInternet(mContext!!)) {
                    submitLeave(URLConstants.URL_GET_LEAVEREQUESTSUBMISSION)
                } else {
                    appUtils.showDialogAlertDismiss(
                        mContext as Activity?,
                        "Network Error",
                        getString(R.string.no_internet),
                        R.drawable.nonetworkicon,
                        R.drawable.roundred
                    )
                }
            }
        }

        enterStartDate!!.setOnClickListener(View.OnClickListener {
            if (appUtils.isEditTextFocused(this@LeaveRequestSubmissionActivity)) {
                appUtils.hideKeyboard(mContext)
            }
            enterMessage!!.isFocusable = false

            enterMessage!!.clearFocus()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var strDate: Date? = null
            val items1: Array<String> = appUtils.getCurrentDateToday() as Array<String>
            val date1 = items1[0]
            //if()
            val month = items1[1]
            val year = items1[2]
            try {
                strDate = sdf.parse(appUtils.getCurrentDateToday().toString())
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val strDatePicker = DatePickerDialog(
                mContext!!, startDate, year.toInt(),
                month.toInt() - 1, date1.toInt()
            )
            strDatePicker.datePicker.minDate = strDate!!.time
            strDatePicker.show()
        })

        enterEndDate!!.setOnClickListener {
            if (appUtils.isEditTextFocused(this@LeaveRequestSubmissionActivity)) {
                appUtils.hideKeyboard(mContext)
            }
            enterMessage!!.isFocusable = false
            enterMessage!!.clearFocus()
            if (enterStartDate!!.getText().toString() == "") {
                Toast.makeText(mContext, "Please select first day of absence.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                var strDate: Date? = null
                val items1: Array<String> = appUtils.getCurrentDateToday() as Array<String>
                val date1 = items1[0]
                //if()
                val month = items1[1]
                val year = items1[2]
                try {
                    strDate = sdf.parse(appUtils.getCurrentDateToday() as String)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val endDatePicker = DatePickerDialog(
                    mContext!!, endDate, year.toInt(),
                    month.toInt() - 1, date1.toInt()
                )
                endDatePicker.datePicker.minDate = strDate!!.time
                endDatePicker.show()
            }
        }
    }

    private fun submitLeave(urlGetLeaverequestsubmission: String) {

    }

    private fun showSocialmediaList(studentsModelArrayList: ArrayList<StudentModel>) {

    }
}