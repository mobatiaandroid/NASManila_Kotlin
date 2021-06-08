package com.mobatia.nasmanila.activities.staff_directory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.HomeListActivity
import com.mobatia.nasmanila.activities.staff_directory.adapter.CustomStaffDeptRecyclerAdapter
import com.mobatia.nasmanila.activities.staff_directory.adapter.StaffAdapterAdapter
import com.mobatia.nasmanila.activities.staff_directory.model.StaffModel
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.constants.URLConstants
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.HeaderManager
import com.mobatia.nasmanila.manager.PreferenceManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StaffListActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    private var mContext: Context? = null
    var relativeHeader: RelativeLayout? = null
    var headermanager: HeaderManager? = null
    var back: ImageView? = null
    var home: ImageView? = null

    private var mStaffListView: RecyclerView? = null
    var extras: Bundle? = null
    var category_id: String? = null
    var title:String? = null
    var mStaffDeptList: ArrayList<StaffModel>? = null
    var hashmap = HashMap<String, ArrayList<StaffModel>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_list)
        mContext = this
        initialiseUI()
        if (appUtils.checkInternet(mContext as StaffListActivity)) {
            callStaffListAPI(URLConstants.URL_GETSTAFFLDEPT_LIST)
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

    private fun callStaffListAPI(urlGetstaffldeptList: String) {
        val call: Call<ResponseBody> = ApiClient.getApiService().staffDirectoryListCall(preferenceManager.getAccessToken(mContext))
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
                        val dataObject: JSONObject = responseJSONObject.getJSONObject("data")
                        val deptArrayList = ArrayList<String>()
                        val staffArray = dataObject.getJSONArray("staffs")
                        val depArray = dataObject.getJSONArray("departments")
                        if (depArray.length() > 0) {
                            var depObject = ""
                            for (i in 0 until depArray.length()) {
                                depObject = depArray.getString(i)
                                deptArrayList.add(depObject)
                            }
                            for (j in deptArrayList.indices) {
                                val staffObject = staffArray.getJSONObject(j)
                                val keyArray = staffObject.getJSONArray(deptArrayList[j])
                                println("staffdept----name--1--$j---$keyArray")
                                mStaffDeptList = ArrayList()
                                if (keyArray.length() > 0) {
                                    for (l in 0 until keyArray.length()) {
                                        val keyObj = keyArray.getJSONObject(l)
                                        mStaffDeptList!!.add(addStaffDeptDetails(keyObj))
                                    }
                                } /*else{
                                        deptArrayList.remove(j);
                                    }*/
                                //if(mStaffDeptList.size()>0) {
                                hashmap[deptArrayList[j]] = mStaffDeptList!!
                                // }
                            }
                            println("hashmap size--" + hashmap.size + "--" + mStaffDeptList!!.size)

                            if (hashmap.size == 1 && mStaffDeptList!!.size == 0) {
                                appUtils.showDialogAlertFinish(
                                    mContext as Activity?,
                                    mContext!!.getString(R.string.alert_heading),
                                    mContext!!.getString(R.string.no_details_available),
                                    R.drawable.exclamationicon,
                                    R.drawable.round
                                )
                            } else {
                                val customStaffDeptAdapter =
                                    StaffAdapterAdapter(mContext, deptArrayList, hashmap)
                                mStaffListView!!.adapter = customStaffDeptAdapter
                            }
                        } else {
                            mStaffDeptList = ArrayList()
                            for (j in 0 until staffArray.length()) {
                                val staffObject = staffArray.getJSONObject(j)
                                mStaffDeptList!!.add(addStaffDeptDetails(staffObject))
                            }
                            val customStaffDeptAdapter =
                                CustomStaffDeptRecyclerAdapter(mContext, mStaffDeptList!!, "")
                            mStaffListView!!.adapter = customStaffDeptAdapter
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
                    callStaffListAPI(URLConstants.URL_GETSTAFFLDEPT_LIST)
                } else if (responseCode.equals("401", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    callStaffListAPI(URLConstants.URL_GETSTAFFLDEPT_LIST)
                } else if (responseCode.equals("402", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    callStaffListAPI(URLConstants.URL_GETSTAFFLDEPT_LIST)
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

    private fun addStaffDeptDetails(staffObject: JSONObject?): StaffModel {
        val model = StaffModel()
            model.staffId = (staffObject!!.optString("id"))
            model.staffName = (staffObject!!.optString("name"))
            model.staffPhoneNo = (staffObject!!.optString("phone"))
            model.staffAbout = (staffObject!!.getString("about"))
            model.staffEmail = (staffObject!!.optString("email"))
            model.role = (staffObject!!.optString("role"))
            model.staffContactNo = (staffObject!!.optString("contact"))
            model.staffImage = (staffObject!!.optString("staff_photo"))
        return model
    }

    private fun initialiseUI() {
        extras = intent.extras
        if (extras != null) {
            category_id = extras!!.getString("category_id")
            title = extras!!.getString("title")
        }
        relativeHeader = findViewById<View>(R.id.relativeHeader) as RelativeLayout
        mStaffListView = findViewById<View>(R.id.mStaffListView) as RecyclerView
        mStaffListView!!.setHasFixedSize(true)
        headermanager = HeaderManager(this@StaffListActivity, title)
        headermanager!!.getHeader(relativeHeader!!, 0)
        back = headermanager!!.getLeftButton()
        headermanager!!.setButtonLeftSelector(
            R.drawable.back,
            R.drawable.back
        )
        back!!.setOnClickListener {
            appUtils.hideKeyboard(mContext)
            finish()
        }
        home = headermanager!!.getLogoButton()
        home!!.setOnClickListener {
            val `in` = Intent(mContext, HomeListActivity::class.java)
            `in`.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(`in`)
        }
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        mStaffListView!!.layoutManager = llm
    }
}