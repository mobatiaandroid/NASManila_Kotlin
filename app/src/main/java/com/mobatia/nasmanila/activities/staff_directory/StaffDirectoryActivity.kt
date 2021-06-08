package com.mobatia.nasmanila.activities.staff_directory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.HomeListActivity
import com.mobatia.nasmanila.activities.staff_directory.adapter.CustomStaffDirectoryAdapter
import com.mobatia.nasmanila.activities.staff_directory.model.StaffModel
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.constants.URLConstants
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.HeaderManager
import com.mobatia.nasmanila.manager.PreferenceManager
import com.mobatia.nasmanila.manager.recyclermanager.DividerItemDecoration
import com.mobatia.nasmanila.manager.recyclermanager.OnItemClickListener
import com.mobatia.nasmanila.manager.recyclermanager.addOnItemClickListener
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StaffDirectoryActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    private var mContext: Context? = null
    var relativeHeader: RelativeLayout? = null
    var headermanager: HeaderManager? = null
    var back: ImageView? = null
    var home: ImageView? = null
    var mStaffDirectoryListArray: ArrayList<StaffModel> = ArrayList<StaffModel>()
    var mStaffDirectoryListView: RecyclerView? = null
    var bannerUrlImageArray: ArrayList<String>? = null
    var bannerImagePager: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_directory)
        mContext = this
        initialiseUI()
        if (appUtils.checkInternet(mContext as StaffDirectoryActivity)) {
            callStaffDirectoryListAPI(URLConstants.URL_STAFFDIRECTORY_LIST)
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

    private fun initialiseUI() {
        relativeHeader = findViewById<View>(R.id.relativeHeader) as RelativeLayout
        bannerImagePager = findViewById<View>(R.id.bannerImageViewPager) as ImageView
        mStaffDirectoryListView = findViewById<View>(R.id.mStaffDirectoryListView) as RecyclerView
        mStaffDirectoryListView!!.addItemDecoration(DividerItemDecoration(resources.getDrawable(R.drawable.list_divider)))
        mStaffDirectoryListView!!.setHasFixedSize(true)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        mStaffDirectoryListView!!.layoutManager = llm
        headermanager = HeaderManager(this@StaffDirectoryActivity, "Staff Directory")
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
            val intent = Intent(mContext, HomeListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        mStaffDirectoryListView!!.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val intent = Intent(this@StaffDirectoryActivity, StaffListActivity::class.java)
                intent.putExtra(
                    "category_id",
                    mStaffDirectoryListArray[position].staffCategoryId
                )
                intent.putExtra("title", "Staff Directory")
                startActivity(intent)
            }

        })
    }

    private fun callStaffDirectoryListAPI(url: String) {
        mStaffDirectoryListArray = ArrayList()
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
                        val data: JSONArray = responseJSONObject.getJSONArray("data")
                        val bannerImage: String = responseJSONObject.optString(JSONConstants.JTAG_BANNER_IMAGE)
                        if (!bannerImage.equals("", ignoreCase = true)) {
                            Glide.with(mContext!!).load(appUtils.replace(bannerImage)).centerCrop()
                                .into(bannerImagePager!!)
                        } else {
                            bannerImagePager!!.setBackgroundResource(R.drawable.staffdirectory)
                        }
                        if (data.length() > 0) {
                            for (i in 0 until data.length()) {
                                val dataObject = data.getJSONObject(i)
                                mStaffDirectoryListArray.add(addStaffDetails(dataObject))
                            }
                            val customStaffDirectoryAdapter =
                                CustomStaffDirectoryAdapter(mContext, mStaffDirectoryListArray)
                            mStaffDirectoryListView!!.adapter = customStaffDirectoryAdapter
                        } else {
                            Toast.makeText(
                                this@StaffDirectoryActivity,
                                "No data found",
                                Toast.LENGTH_SHORT
                            ).show()
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
                    callStaffDirectoryListAPI(URLConstants.URL_STAFFDIRECTORY_LIST)
                } else if (responseCode.equals("401", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    callStaffDirectoryListAPI(URLConstants.URL_STAFFDIRECTORY_LIST)
                } else if (responseCode.equals("402", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    callStaffDirectoryListAPI(URLConstants.URL_STAFFDIRECTORY_LIST)
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

    private fun addStaffDetails(dataObject: JSONObject?): StaffModel {
        val model = StaffModel()
            model.staffCategoryId = (dataObject!!.optString("id"))
            model.staffCategoryName = (dataObject.optString("category_name"))
        return model
    }
}