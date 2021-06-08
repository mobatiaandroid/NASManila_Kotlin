package com.mobatia.nasmanila.fragments.absences

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.model.StudentModel
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.constants.URLConstants
import com.mobatia.nasmanila.fragments.absences.model.LeavesModel
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.PreferenceManager
import com.mobatia.nasmanila.manager.recyclermanager.DividerItemDecoration
import com.mobatia.nasmanila.manager.recyclermanager.ItemOffsetDecoration
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class AbsenceFragment(title: String, tabID: String) : Fragment() {
    var preferenceManager: PreferenceManager = PreferenceManager()
    var appUtils: AppUtils = AppUtils()
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
    var studentsModelArrayList: ArrayList<StudentModel> = ArrayList<StudentModel>()
    private val mAbsenceListViewArray: ArrayList<LeavesModel>? = null
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
            false)
        setHasOptionsMenu(true)
        mContext = activity
        firstVisit = true
        initialiseUI()
        if (appUtils.checkInternet(mContext!!)) {
//            getStudentsListFirstAPI(URLConstants.URL_GET_STUDENT_LIST)
            if (preferenceManager.getStudentList(context!!) != null) {
                val call: Call<ResponseBody> = ApiClient.getApiService().getStudentListCall(
                    preferenceManager.getAccessToken(mContext),
                    preferenceManager.getUserId(mContext!!)
                )
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        val responseCode = jsonObject.getString(JSONConstants.JTAG_RESPONSECODE)
                        if (responseCode.equals("200")) {
                            val responseJSONObject = jsonObject.getJSONObject(JSONConstants.JTAG_RESPONSE)
                            val statusCode = responseJSONObject.getString(JSONConstants.JTAG_STATUSCODE)
                            if (statusCode == "303") {
                                val dataArray: JSONArray =
                                    responseJSONObject.getJSONArray(JSONConstants.JTAG_RESPONSE_DATA_ARRAY)
                                preferenceManager.setStudentList(mContext!!, responseJSONObject)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
            } else
                studentsModelArrayList = preferenceManager.getStudentList(context)

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

    private fun initialiseUI() {
//        mTitleTextView = mRootView!!.findViewById<View>(R.id.titleTextView) as TextView
//        mAbsenceListView = mRootView!!.findViewById<View>(R.id.mAbsenceListView) as RecyclerView
//        relMain = mRootView!!.findViewById<View>(R.id.relMain) as RelativeLayout
//        studImg = mRootView!!.findViewById<View>(R.id.studImg) as ImageView
//        mStudentSpinner = mRootView!!.findViewById<View>(R.id.studentSpinner) as LinearLayout
//        belowViewRelative = mRootView!!.findViewById<View>(R.id.belowViewRelative) as RelativeLayout
//        relMain!!.setOnClickListener(View.OnClickListener { })
//        studentName = mRootView!!.findViewById<View>(R.id.studentName) as TextView
//        newRequest = mRootView!!.findViewById<View>(R.id.newRequest) as TextView
//
//        mTitleTextView!!.text = NaisClassNameConstants.ABSENCE
//        mAbsenceListView!!.setHasFixedSize(true)
//        val llm = LinearLayoutManager(mContext)
//        llm.orientation = LinearLayoutManager.VERTICAL
//        mAbsenceListView!!.layoutManager = llm
//        val itemDecoration = ItemOffsetDecoration(mContext!!, 2)
//        mAbsenceListView!!.addItemDecoration(itemDecoration)
//
//        mAbsenceListView!!.addItemDecoration(
//            DividerItemDecoration(mContext!!.resources.getDrawable(R.drawable.list_divider))
//        )
//        mAbsenceListView!!.addOnItemTouchListener(
//            R)
//        )
    }


}
}