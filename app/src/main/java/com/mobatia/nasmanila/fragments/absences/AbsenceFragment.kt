package com.mobatia.nasmanila.fragments.absences

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.constants.URLConstants
import com.mobatia.nasmanila.fragments.absences.model.LeavesModel
import com.mobatia.nasmanila.fragments.parents_meeting.model.StudentModel
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.PreferenceManager
import com.mobatia.nasmanila.manager.recyclermanager.DividerItemDecoration
import com.mobatia.nasmanila.manager.recyclermanager.ItemOffsetDecoration
import com.mobatia.nasmanila.manager.recyclermanager.OnItemClickListener
import com.mobatia.nasmanila.manager.recyclermanager.addOnItemClickListener
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class AbsenceFragment(title: String, tabID: String) : Fragment() {

    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    private var mRootView: View? = null
    private var mContext: Context? = null
    private var mAbsenceListView: RecyclerView? = null
    private val mTitle: String? = null
    private val mTabId: String? = null
    var mTitleTextView: TextView? = null
    var studentName: TextView? = null
    var newRequest: TextView? = null
    private var relMain: RelativeLayout? = null
    private var belowViewRelative: RelativeLayout? = null
    var studentsModelArrayList = ArrayList<StudentModel>()
    private var mAbsenceListViewArray: ArrayList<LeavesModel>? = null
    var mStudentSpinner: LinearLayout? = null
    var stud_id = ""
    var studClass = ""
    var stud_img = ""
    var studImg: ImageView? = null
    var studentList = ArrayList<String>()
    var firstVisit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(
            R.layout.fragment_absence, container,
            false
        )
        setHasOptionsMenu(true)
        mContext = activity
        firstVisit = true
        initialiseUI()
        if (appUtils.checkInternet(mContext!!)) {
            getStudentsListFirstAPI(URLConstants.URL_GET_STUDENT_LIST)
        } else {
            appUtils.showDialogAlertDismiss(
                mContext as Activity?,
                "Network Error",
                getString(R.string.no_internet),
                R.drawable.nonetworkicon,
                R.drawable.roundred
            )
        }
        return mRootView
    }

    private fun getStudentsListFirstAPI(url: String) {
        val call: Call<ResponseBody> = ApiClient.getApiService().getStudentListFirstCall(
            preferenceManager.getAccessToken(mContext), preferenceManager.getUserId(mContext!!)
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
                        val data: JSONArray = responseJSONObject.getJSONArray("data")
                        studentsModelArrayList.clear()
                        studentList.clear()
                        if (data.length() > 0) {
                            //studentsModelArrayList.add(0,);
                            for (i in 0 until data.length()) {
                                val dataObject = data.getJSONObject(i)
                                studentsModelArrayList.add(addStudentDetails(dataObject))
                                studentList.add(studentsModelArrayList[i].mName!!)
                            }
                            studentName!!.text = (studentsModelArrayList[0].mName)
                            stud_id = studentsModelArrayList[0].mId!!
                            preferenceManager.setLeaveStudentId(mContext!!, stud_id)
                            preferenceManager.setLeaveStudentName(
                                mContext!!,
                                studentsModelArrayList[0].mName!!
                            )
                            studClass = studentsModelArrayList[0].mClass!!
                            belowViewRelative!!.visibility = View.VISIBLE
                            newRequest!!.visibility = View.VISIBLE
                            if (appUtils.checkInternet(mContext!!)) {
                                getList(URLConstants.URL_GET_LEAVEREQUEST_LIST, stud_id)
                            } else {
                                appUtils.showDialogAlertDismiss(
                                    mContext as Activity?,
                                    "Network Error",
                                    getString(R.string.no_internet),
                                    R.drawable.nonetworkicon,
                                    R.drawable.roundred
                                )
                            }

                        } else {
                            belowViewRelative!!.visibility = View.INVISIBLE
                            newRequest!!.visibility = View.INVISIBLE
                            appUtils.showDialogAlertDismiss(
                                mContext as Activity?,
                                "Alert",
                                getString(R.string.student_not_available),
                                R.drawable.exclamationicon,
                                R.drawable.round
                            )
                        }
                    }
                } else if (responseCode.equals("500", ignoreCase = true)) {
                    appUtils.showDialogAlertDismiss(
                        mContext as Activity?,
                        "Alert",
                        getString(R.string.common_error),
                        R.drawable.exclamationicon,
                        R.drawable.round
                    )
                } else if (responseCode.equals("400", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    getStudentsListFirstAPI(url)
                } else if (responseCode.equals("401", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    getStudentsListFirstAPI(url)
                } else if (responseCode.equals("402", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    getStudentsListFirstAPI(url)
                } else {

                    appUtils.showDialogAlertDismiss(
                        mContext as Activity?,
                        "Alert",
                        getString(R.string.common_error),
                        R.drawable.exclamationicon,
                        R.drawable.round
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                appUtils.showDialogAlertDismiss(
                    mContext as Activity?,
                    "Alert",
                    getString(R.string.common_error),
                    R.drawable.exclamationicon,
                    R.drawable.round
                )
            }

        })
    }

    private fun addStudentDetails(dataObject: JSONObject?): StudentModel {
        val studentModel = StudentModel()
        studentModel.mId=(dataObject!!.optString(JSONConstants.JTAG_ID))
        studentModel.mName=(dataObject!!.optString(JSONConstants.JTAG_TAB_NAME))
        studentModel.mClass=(dataObject!!.optString(JSONConstants.JTAG_TAB_CLASS))
        studentModel.mSection=(dataObject!!.optString(JSONConstants.JTAG_TAB_SECTION))
        studentModel.mHouse=(dataObject!!.optString("house"))
        studentModel.mPhoto=(dataObject!!.optString("photo"))
        return studentModel
    }

    private fun initialiseUI() {
        mTitleTextView = mRootView!!.findViewById<View>(R.id.titleTextView) as TextView
        mAbsenceListView = mRootView!!.findViewById<View>(R.id.mAbsenceListView) as RecyclerView
        relMain = mRootView!!.findViewById<View>(R.id.relMain) as RelativeLayout
        studImg = mRootView!!.findViewById<View>(R.id.studImg) as ImageView
        mStudentSpinner = mRootView!!.findViewById<View>(R.id.studentSpinner) as LinearLayout
        belowViewRelative = mRootView!!.findViewById<View>(R.id.belowViewRelative) as RelativeLayout
        relMain!!.setOnClickListener(View.OnClickListener { })
        studentName = mRootView!!.findViewById<View>(R.id.studentName) as TextView
        newRequest = mRootView!!.findViewById<View>(R.id.newRequest) as TextView
        mTitleTextView!!.text = NaisClassNameConstants.ABSENCE
        mAbsenceListView!!.setHasFixedSize(true)
        val llm = LinearLayoutManager(mContext)
        llm.orientation = LinearLayoutManager.VERTICAL
        mAbsenceListView!!.layoutManager = llm
        val itemDecoration = ItemOffsetDecoration(mContext!!, 2)
        mAbsenceListView!!.addItemDecoration(itemDecoration)
        mAbsenceListView!!.addItemDecoration(
            DividerItemDecoration(mContext!!.resources.getDrawable(R.drawable.list_divider))
        )
        mAbsenceListView!!.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                if (mAbsenceListViewArray!!.size > 0) {
                    val mIntent = Intent(mContext, LeavesDetailActivity::class.java)
                    mIntent.putExtra("studentName", studentName!!.text.toString())
                    mIntent.putExtra("studentClass", studClass)
                    mIntent.putExtra("studentImage", stud_img)
                    mIntent.putExtra("fromDate", mAbsenceListViewArray!![position].fromDate)
                    mIntent.putExtra("toDate", mAbsenceListViewArray!![position].toDate)
                    mIntent.putExtra(
                        "reasonForAbsence",
                        mAbsenceListViewArray!![position].reason
                    )
                    mContext!!.startActivity(mIntent)
                }
            }

        })
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
        newRequest!!.setOnClickListener {
            val mIntent = Intent(mContext, LeaveRequestSubmissionActivity::class.java)
            mIntent.putExtra("studentName", studentName!!.text.toString())
            mIntent.putExtra(
                "studentId",
                preferenceManager.getLeaveStudentId(mContext!!)
            )
            mIntent.putExtra("StudentModelArray", studentsModelArrayList)
            mIntent.putExtra("studentImage", stud_img)
            mContext!!.startActivity(mIntent)
        }
    }

    private fun showSocialmediaList(mStudentArray: ArrayList<StudentModel>) {
        val dialog = Dialog(mContext!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_student_media_list)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogDismiss = dialog.findViewById<View>(R.id.btn_dismiss) as Button
        val iconImageView = dialog.findViewById<View>(R.id.iconImageView) as ImageView
        iconImageView.setImageResource(R.drawable.boy)
        val socialMediaList =
            dialog.findViewById<View>(R.id.recycler_view_social_media) as RecyclerView
        val sdk = Build.VERSION.SDK_INT
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            dialogDismiss.setBackgroundDrawable(mContext!!.resources.getDrawable(R.drawable.button_new))
        } else {
            dialogDismiss.background = mContext!!.resources.getDrawable(R.drawable.button_new)
        }
        socialMediaList.addItemDecoration(DividerItemDecoration(mContext!!.resources.getDrawable(R.drawable.list_divider)))

        socialMediaList.setHasFixedSize(true)
        val llm = LinearLayoutManager(mContext)
        llm.orientation = LinearLayoutManager.VERTICAL
        socialMediaList.layoutManager = llm

        val studentAdapter = StudentSpinnerAdapter(mContext, mStudentArray)
        socialMediaList.adapter = studentAdapter
        dialogDismiss.setOnClickListener { dialog.dismiss() }
        socialMediaList.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                dialog.dismiss()
                studentName!!.text = (mStudentArray[position].mName)
                stud_id = mStudentArray[position].mId!!
                studClass = mStudentArray[position].mClass!!
                preferenceManager.setLeaveStudentId(mContext!!, stud_id)
                preferenceManager.setLeaveStudentName(
                    mContext!!,
                    mStudentArray.get(position).mName!!
                )
                stud_img = mStudentArray.get(position).mPhoto!!
                if (stud_img != "") {
                    Glide.with(mContext!!).load(appUtils.replace(stud_img))
                        .placeholder(R.drawable.student).into(studImg!!)
                } else {
                    studImg!!.setImageResource(R.drawable.student)
                }
                if (appUtils.checkInternet(mContext!!)) {
                    getList(
                        URLConstants.URL_GET_LEAVEREQUEST_LIST,
                        mStudentArray[position].mId
                    )
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

        })
        dialog.show()

    }

    private fun getList(url: String, student_id: String?) {
        mAbsenceListViewArray = ArrayList()
        val call: Call<ResponseBody> = ApiClient.getApiService().getLeaveRequests(
            preferenceManager.getAccessToken(mContext),
            preferenceManager.getUserId(mContext!!),
            student_id!!
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
                        val dataArray: JSONArray =
                            responseJSONObject.optJSONArray(JSONConstants.JTAG_REQUESTS)
                        if (dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val dataObject = dataArray.optJSONObject(i)
                                val mLeavesModel = LeavesModel()
                                mLeavesModel.toDate=(dataObject.optString(JSONConstants.JTAG_TODATE))
                                mLeavesModel.fromDate=(dataObject.optString(JSONConstants.JTAG_FROMDATE))
                                mLeavesModel.reason=(dataObject.optString(JSONConstants.JTAG_REASON))
                                mLeavesModel.status=(dataObject.optString(JSONConstants.JTAG_STATUs))
                                mAbsenceListViewArray!!.add(mLeavesModel)
                            }
                            val mAbsenceRecyclerAdapter =
                                AbsenceRecyclerAdapter(mContext, mAbsenceListViewArray)
                            mAbsenceListView!!.adapter = mAbsenceRecyclerAdapter
                        } else {
                            val mAbsenceRecyclerAdapter =
                                AbsenceRecyclerAdapter(mContext, mAbsenceListViewArray)
                            mAbsenceListView!!.adapter = mAbsenceRecyclerAdapter
                            appUtils.showDialogAlertDismiss(
                                context as Activity?,
                                "Alert",
                                "No data available.",
                                R.drawable.exclamationicon,
                                R.drawable.round
                            )
                        }
                    } else {
                        appUtils.showDialogAlertDismiss(
                            mContext as Activity?,
                            "Alert",
                            getString(R.string.common_error),
                            R.drawable.exclamationicon,
                            R.drawable.round
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                appUtils.showDialogAlertDismiss(
                    mContext as Activity?,
                    "Alert",
                    getString(R.string.common_error),
                    R.drawable.exclamationicon,
                    R.drawable.round
                )
            }

        })
        }
    }


}
