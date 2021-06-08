package com.mobatia.nasmanila.fragments.contact_us

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.staff_directory.StaffDirectoryActivity
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.constants.URLConstants
import com.mobatia.nasmanila.fragments.contact_us.adapter.ContactUsAdapter
import com.mobatia.nasmanila.fragments.contact_us.model.ContactUsModel
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.PreferenceManager
import com.mobatia.nasmanila.manager.recyclermanager.DividerItemDecoration
import com.mobatia.nasmanila.manager.recyclermanager.ItemOffsetDecoration
import com.mobatia.nasmanila.manager.recyclermanager.OnItemClickListener
import com.mobatia.nasmanila.manager.recyclermanager.addOnItemClickListener
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class ContactUsFragment(s: String, tabContactUsGuest: String) : Fragment() {
    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    private val mwebSettings: WebSettings? = null
    private var mRootView: View? = null
    private var mContext: Context? = null
    private val web: WebView? = null
    private val mTitle: String? = null
    private val mTabId: String? = null
    private var relMain: RelativeLayout? = null
    private val mBannerImage: ImageView? = null


    var mTitleTextView: TextView? = null
    var desc: TextView? = null
    var anim: RotateAnimation? = null
    private val loadingFlag = true
    var mLoadUrl: String? = null
    private val mErrorFlag = false
    var latitude: String? = null
    var longitude:kotlin.String? = null
    var description:kotlin.String? = null
    var c_latitude:kotlin.String? = null
    var c_longitude:kotlin.String? = null
    var contactUsModelsArrayList = ArrayList<ContactUsModel>()
    var contactList: RecyclerView? = null
    private var mMap: GoogleMap? = null
    var mapFragment: SupportMapFragment? = null
    private var lm: LocationManager? = null
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var lat: Double? = null
    var lng:kotlin.Double? = null
    val cView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (cView != null) {
            val parent = cView.parent as ViewGroup
            parent.removeView(cView)
        }
            mRootView = inflater.inflate(
                R.layout.fragment_contact_us, container,
                false
            )
        mContext = activity
        initialiseUI()
        if (appUtils.checkInternet(mContext!!)) {
            getlatlong()
            callcontactUsApi(URLConstants.URL_GET_CONTACTUS)
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
//    override fun onItemClick(
//        parent: AdapterView<*>?, view: View?, position: Int,
//        id: Long
//    ) {
//        println("clicked me")
//        println("position$position")
//        println("list size" + contactUsModelsArrayList.size)
//        if (position == contactUsModelsArrayList.size - 1) {
//            val mIntent = Intent(activity, StaffDirectoryActivity::class.java)
//            mContext!!.startActivity(mIntent)
//        }
//    }

    private fun initialiseUI() {
        mTitleTextView = mRootView!!.findViewById<View>(R.id.titleTextView) as TextView
        mTitleTextView!!.text = NaisClassNameConstants.CONTACT_US
        contactList = mRootView!!.findViewById<View>(R.id.mnewsLetterListView) as RecyclerView
        desc = mRootView!!.findViewById<View>(R.id.description) as TextView
        mapFragment = childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment?

        contactList!!.setHasFixedSize(true)
        val llm = LinearLayoutManager(mContext)
        llm.orientation = LinearLayoutManager.VERTICAL
        contactList!!.layoutManager = llm
        val spacing = 10 // 50px

        val itemDecoration = ItemOffsetDecoration(mContext!!, spacing)
        contactList!!.addItemDecoration(itemDecoration)
        contactList!!.addItemDecoration(DividerItemDecoration(resources.getDrawable(R.drawable.list_divider)))

        relMain = mRootView!!.findViewById<View>(R.id.relMain) as RelativeLayout
        relMain!!.setOnClickListener {  }
        contactList!!.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                if (position == contactUsModelsArrayList.size - 1) {
                    val mIntent = Intent(activity, StaffDirectoryActivity::class.java)
                    mContext!!.startActivity(mIntent)
                }
            }

        })
    }


    private fun getlatlong() {
        var location: Location
        lm = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGPSEnabled = lm!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = lm!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGPSEnabled && !isNetworkEnabled) {
        } else {
            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            mContext!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            mContext!!, Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    } else {
//                        lm.requestLocationUpdates(
//                            NETWORK_PROVIDER, 0L,
//                            0.0f, this
//                        )
                        location = lm!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                        if (location != null) {
                            lat = location.latitude
                            lng = location.longitude
                        }
                    }
                }
                if (isGPSEnabled) {
//                    lm.requestLocationUpdates(
//                        NETWORK_PROVIDER, 0L,
//                        0.0f, this
//                    )
                    location = lm!!
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                    if (location != null) {
                        lat = location.latitude
                        lng = location.longitude
                    }
                }
            }
        }
    }

    private fun callcontactUsApi(urlGetContactus: String) {
        anim = RotateAnimation(
            0F, 360F, Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim!!.setInterpolator(mContext, android.R.interpolator.linear)
        anim!!.repeatCount = Animation.INFINITE
        anim!!.duration = 1000
        val call: Call<ResponseBody> = ApiClient.getApiService().contactUsCall(preferenceManager.getAccessToken(mContext))
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
                        val data: JSONObject = responseJSONObject.getJSONObject("data")
                        latitude = data.getString(JSONConstants.JTAG_EVENT_LATITUDE)
                        longitude = data.getString(JSONConstants.JTAG_EVENT_LONGITUDE)
                        description = data.getString(JSONConstants.JTAG_DESCRIPTION)
                        desc!!.text = description
                        val contact = data.getJSONArray(JSONConstants.JTAG_CONTACTS)
                        if (contact.length() > 0) {
                            for (i in 0..contact.length()) {
                                println("length" + contact.length())
                                if (i < contact.length()) {
                                    println("working")
                                    val cObj = contact.getJSONObject(i)
                                    val contactUsModel = ContactUsModel()
                                    contactUsModel.contact_email =
                                        cObj.getString(JSONConstants.JTAG_EMAIL)
                                    contactUsModel.contact_phone =
                                        cObj.getString(JSONConstants.JTAG_EVENT_PHONE)
                                    contactUsModel.contact_name =
                                        cObj.getString(JSONConstants.JTAG_TAB_NAME)
                                    contactUsModelsArrayList.add(contactUsModel)
                                } else if (i == contact.length()) {
                                    println("working ****")
                                    val contactUsModel = ContactUsModel()
                                    contactUsModel.contact_phone = ""
                                    contactUsModel.contact_email = ""
                                    contactUsModel.contact_name = "Staff Directory"
                                    contactUsModelsArrayList.add(contactUsModel)
                                }
                            }
//                            mapFragment!!.getMapAsync { googleMap ->
//                                Log.d("Map Ready", "Bam.")
//                                mMap = googleMap
//                                mMap!!.uiSettings.isMapToolbarEnabled = false
//                                mMap!!.uiSettings.isZoomControlsEnabled = false
//                                val latLng = LatLng(
//                                    latitude!!.toDouble(),
//                                    longitude!!.toDouble()
//                                )
//                                mMap!!.addMarker(
//                                    MarkerOptions()
//                                        .position(latLng)
//                                        .draggable(true)
//                                        .title("NAIS Manila")
//                                )
//                                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//                                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(13f))
//                                mMap!!.setOnInfoWindowClickListener(OnInfoWindowClickListener {
//                                    if (appUtils.checkInternet(mContext!!)) {
//                                        if (!isGPSEnabled) {
//                                            val callGPSSettingIntent = Intent(
//                                                Settings.ACTION_LOCATION_SOURCE_SETTINGS
//                                            )
//                                            startActivity(callGPSSettingIntent)
//                                        } else {
//                                            val intent = Intent(
//                                                mContext,
//                                                LoadUrlWebViewActivity::class.java
//                                            )
//                                            intent.putExtra(
//                                                "url",
//                                                "http://maps.google.com/maps?saddr=$c_latitude,$c_longitude&daddr=Nord Anglia International School Manila - Manila"
//                                            )
//                                            intent.putExtra("tab_type", "Contact Us")
//                                            startActivity(intent)
//                                        }
//                                    } else {
//                                        appUtils.showDialogAlertDismiss(
//                                            mContext as Activity?,
//                                            "Network Error",
//                                            getString(R.string.no_internet),
//                                            R.drawable.nonetworkicon,
//                                            R.drawable.roundred
//                                        )
//                                    }
//                                })
//                            }
                            val contactUsAdapter =
                                ContactUsAdapter(mContext, contactUsModelsArrayList)
                            contactList!!.adapter = contactUsAdapter
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
                    callcontactUsApi(URLConstants.URL_GET_ABOUTUS_LIST)
                } else if (responseCode.equals("401", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    callcontactUsApi(URLConstants.URL_GET_ABOUTUS_LIST)
                } else if (responseCode.equals("402", ignoreCase = true)) {
                    appUtils.getToken(mContext!!)
                    callcontactUsApi(URLConstants.URL_GET_ABOUTUS_LIST)
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


}