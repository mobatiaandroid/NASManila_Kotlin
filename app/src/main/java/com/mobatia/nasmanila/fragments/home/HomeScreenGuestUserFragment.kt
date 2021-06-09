package com.mobatia.nasmanila.fragments.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.constants.NaisTabConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.fragments.about_us.AboutUsFragment
import com.mobatia.nasmanila.fragments.category_main.CategoryMainFragment
import com.mobatia.nasmanila.fragments.communications.CommunicationsFragment
import com.mobatia.nasmanila.fragments.contact_us.ContactUsFragment
import com.mobatia.nasmanila.fragments.home.adapter.ImagePagerDrawableAdapter
import com.mobatia.nasmanila.fragments.nas_today.NasTodayFragment
import com.mobatia.nasmanila.fragments.notifications.NotificationsFragment
import com.mobatia.nasmanila.fragments.parent_essentials.ParentEssentialsFragment
import com.mobatia.nasmanila.fragments.settings.SettingsFragment
import com.mobatia.nasmanila.fragments.social_media.SocialMediaFragment
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.PreferenceManager
import com.mobatia.nasmanila.manager.ProgressBarDialog
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class HomeScreenGuestUserFragment(
    s: String,
    mDrawerLayout: DrawerLayout?,
    mHomeListView: ListView,
    linearLayout: LinearLayout?,
    mListItemArray: Array<String>,
    mListImgArray: TypedArray
) : Fragment() {
    private var preferenceManager: PreferenceManager = PreferenceManager()
    private var appUtils: AppUtils = AppUtils()
    lateinit var rootView: View
    private var mContext: Context? = null
    var progressBarDialog: ProgressBarDialog? = null
    var title: String = s
    var drawerLayout: DrawerLayout? = mDrawerLayout
    var listView: ListView? = mHomeListView
    var listItemArray: Array<String> = mListItemArray
    var linearLayout: LinearLayout? = linearLayout
    var listImageArray: TypedArray = mListImgArray
    var bannerImagePager: ViewPager? = null
    var currentPage = 0
    private var INTENT_TAB_ID: String? = null
    var tabIDToProceed = ""
    var versionName = ""
    var versionCode = -1
    private lateinit var mSectionText: Array<String?>
    private lateinit var homeBannerUrlImageArray: ArrayList<String>
    private var mRelOne: RelativeLayout? = null
    private var mRelTwo: RelativeLayout? = null
    private var mRelThree: RelativeLayout? = null
    private var mRelFour: RelativeLayout? = null
    private var mRelFive: RelativeLayout? = null
    private var mRelSix: RelativeLayout? = null
    private var mRelSeven: RelativeLayout? = null
    private var mRelEight: RelativeLayout? = null
    private var mRelNine: RelativeLayout? = null

    private var mTxtOne: TextView? = null
    private var mTxtTwo: TextView? = null
    private var mTxtThree: TextView? = null
    private var mTxtFour: TextView? = null
    private var mTxtFive: TextView? = null
    private var mTxtSix: TextView? = null
    private var mTxtSeven: TextView? = null
    private var mTxtEight: TextView? = null
    private var mTxtNine: TextView? = null
    private var mImgOne: ImageView? = null
    private var mImgTwo: ImageView? = null
    private var mImgThree: ImageView? = null
    private var mImgFour: ImageView? = null
    private var mImgFive: ImageView? = null
    private var mImgSix: ImageView? = null
    private var mImgSeven: ImageView? = null
    private var mImgEight: ImageView? = null
    private var mImgNine: ImageView? = null
    private var android_app_version: String? = null
    private val PERMISSION_CALLBACK_CONSTANT_CALENDAR = 1
    private val PERMISSION_CALLBACK_CONSTANT_EXTERNAL_STORAGE = 2
    private val PERMISSION_CALLBACK_CONSTANT_LOCATION = 3
    private val REQUEST_PERMISSION_CALENDAR = 101
    private val REQUEST_PERMISSION_EXTERNAL_STORAGE = 102
    private val REQUEST_PERMISSION_LOCATION = 103
    private val calendarToSettings = false
    private val externalStorageToSettings = false
    private var locationToSettings = false
    var permissionsRequiredCalendar = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )
    var permissionsRequiredExternalStorage = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    var permissionsRequiredLocation = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var calendarPermissionStatus: SharedPreferences? = null
    private var externalStoragePermissionStatus: SharedPreferences? = null
    private var locationPermissionStatus: SharedPreferences? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_home_screen_guest_user, container, false)
        mContext = activity
        calendarPermissionStatus = activity!!.getSharedPreferences(
            "calendarPermissionStatus",
            Context.MODE_PRIVATE
        )
        externalStoragePermissionStatus = activity!!.getSharedPreferences(
            "externalStoragePermissionStatus",
            Context.MODE_PRIVATE
        )
        locationPermissionStatus = activity!!.getSharedPreferences(
            "locationPermissionStatus",
            Context.MODE_PRIVATE
        )
        initialiseUI()
        setListeners()
        getButtonBgAndTextImages()
        return rootView
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getButtonBgAndTextImages() {
        if (preferenceManager
                .getButtonOneGuestTextImage(mContext!!)!!.toInt() != 0
        ) {
            val sdk = Build.VERSION.SDK_INT
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                mImgOne!!.setBackgroundDrawable(mContext!!.resources.getDrawable(R.drawable.news))
            } else {
                mImgOne!!.background = mContext!!.resources.getDrawable(R.drawable.news)
            }
            mTxtOne!!.text = "NAIS MANILA TODAY"
            mRelOne!!.setBackgroundColor(
                preferenceManager
                    .getButtonOneBg(mContext!!)
            )
        }
        if (preferenceManager
                .getButtonTwoGuestTextImage(mContext!!)!!.toInt() != 0
        ) {
            mImgTwo!!.setImageDrawable(
                listImageArray.getDrawable(
                    preferenceManager
                        .getButtonTwoGuestTextImage(mContext!!)!!.toInt()
                )
            )
            var relTwoStr = ""
            relTwoStr = if (listItemArray.get(
                    preferenceManager
                        .getButtonTwoGuestTextImage(mContext!!)!!.toInt()
                ).equals(NaisClassNameConstants.CCAS, ignoreCase = true)
            ) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray.get(
                    preferenceManager
                        .getButtonTwoGuestTextImage(mContext!!)!!.toInt()
                ).toUpperCase(Locale.ROOT)
            }
            mTxtTwo!!.text = relTwoStr
            mRelTwo!!.setBackgroundColor(
                preferenceManager
                    .getButtonTwoGuestBg(mContext!!)
            )
        }
        if (preferenceManager
                .getButtonThreeGuestTextImage(mContext!!)!!.toInt() != 0
        ) {
            mImgThree!!.setImageDrawable(
                listImageArray.getDrawable(
                    preferenceManager
                        .getButtonThreeGuestTextImage(mContext!!)!!.toInt()
                )
            )
            var relTwoStr = ""
            relTwoStr = if (listItemArray.get(
                    preferenceManager
                        .getButtonThreeGuestTextImage(mContext!!)!!.toInt()
                ).equals(NaisClassNameConstants.CCAS, ignoreCase = true)
            ) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray.get(
                    preferenceManager
                        .getButtonThreeGuestTextImage(mContext!!)!!.toInt()
                ).toUpperCase(Locale.ROOT)
            }
            mTxtThree!!.text = relTwoStr
            mRelThree!!.setBackgroundColor(
                preferenceManager
                    .getButtonThreeGuestBg(mContext!!)
            )
        }
        if (preferenceManager
                .getButtonFourGuestTextImage(mContext!!)!!.toInt() != 0
        ) {
            mImgFour!!.setImageDrawable(activity!!.getDrawable(R.drawable.settings_new))
            mTxtFour!!.text = "SETTINGS"
            mRelFour!!.setBackgroundColor(
                preferenceManager
                    .getButtonFourGuestBg(mContext!!)
            )
        }
        if (preferenceManager
                .getButtonFiveGuestTextImage(mContext!!)!!.toInt() != 0
        ) {
            mImgFive!!.setImageDrawable(
                listImageArray.getDrawable(
                    preferenceManager
                        .getButtonFiveGuestTextImage(mContext!!)!!.toInt()
                )
            )
            var relTwoStr = ""
            relTwoStr = if (listItemArray.get(
                    preferenceManager
                        .getButtonFiveGuestTextImage(mContext!!)!!.toInt()
                ).equals(NaisClassNameConstants.CCAS, ignoreCase = true)
            ) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray.get(
                    preferenceManager
                        .getButtonFiveGuestTextImage(mContext!!)!!.toInt()
                ).toUpperCase(Locale.ROOT)
            }
            mTxtFive!!.text = relTwoStr
            mRelFive!!.setBackgroundColor(
                preferenceManager
                    .getButtonFiveGuestBg(mContext!!)
            )
        }
        if (preferenceManager
                .getButtonSixGuestTextImage(mContext!!)!!.toInt() != 0
        ) {
            mImgSix!!.setImageDrawable(
                listImageArray.getDrawable(
                    preferenceManager
                        .getButtonSixGuestTextImage(mContext!!)!!.toInt()
                )
            )
            var relTwoStr = ""
            relTwoStr = if (listItemArray.get(
                    preferenceManager
                        .getButtonSixGuestTextImage(mContext!!)!!.toInt()
                ).equals(NaisClassNameConstants.CCAS, ignoreCase = true)
            ) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray.get(
                    preferenceManager
                        .getButtonSixGuestTextImage(mContext!!)!!.toInt()
                ).toUpperCase(Locale.ROOT)
            }
            mTxtSix!!.text = relTwoStr
            mRelSix!!.setBackgroundColor(
                preferenceManager
                    .getButtonSixGuestBg(mContext!!)
            )
        }
        if (preferenceManager
                .getButtonSevenGuestTextImage(mContext!!)!!.toInt() != 0
        ) {
            mImgSeven!!.setImageDrawable(
                listImageArray.getDrawable(
                    preferenceManager
                        .getButtonSevenGuestTextImage(mContext!!)!!.toInt()
                )
            )
            var relTwoStr = ""
            relTwoStr = if (listItemArray.get(
                    preferenceManager
                        .getButtonSevenGuestTextImage(mContext!!)!!.toInt()
                ).equals(NaisClassNameConstants.CCAS, ignoreCase = true)
            ) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray.get(
                    preferenceManager
                        .getButtonSevenGuestTextImage(mContext!!)!!.toInt()
                ).toUpperCase(Locale.ROOT)
            }
            mTxtSeven!!.text = relTwoStr
            mRelSeven!!.setBackgroundColor(
                preferenceManager
                    .getButtonSevenGuestBg(mContext!!)
            )
        }
        if (preferenceManager
                .getButtonEightGuestTextImage(mContext!!)!!.toInt() != 0
        ) {
            mImgEight!!.setImageDrawable(
                listImageArray.getDrawable(
                    preferenceManager
                        .getButtonEightGuestTextImage(mContext!!)!!.toInt()
                )
            )
            System.out.println(
                "value check:::" + preferenceManager
                    .getButtonEightGuestTextImage(mContext!!)
            )
            var relTwoStr = ""
            relTwoStr = if (listItemArray.get(
                    preferenceManager
                        .getButtonEightGuestTextImage(mContext!!)!!.toInt()
                ).equals(NaisClassNameConstants.CCAS, ignoreCase = true)
            ) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray.get(
                    preferenceManager
                        .getButtonEightGuestTextImage(mContext!!)!!.toInt()
                ).toUpperCase(Locale.ROOT)
            }
            mTxtEight!!.text = relTwoStr
            mRelEight!!.setBackgroundColor(
                preferenceManager
                    .getButtonEightGuestBg(mContext!!)
            )
        }
        if (preferenceManager
                .getButtonNineGuestTextImage(mContext!!)!!.toInt() != 0
        ) {
            mImgNine!!.setImageDrawable(activity!!.getDrawable(R.drawable.logoutnew))
            mTxtNine!!.text = "LOGOUT"
            mRelNine!!.setBackgroundColor(
                preferenceManager
                    .getButtonNineGuestBg(mContext!!)
            )
        }
    }

    private fun setListeners() {

        mRelOne!!.setOnClickListener {
            INTENT_TAB_ID = preferenceManager
                .getButtonOneGuestTabId(mContext!!)
            checkIntent(INTENT_TAB_ID)
        }
        mRelTwo!!.setOnClickListener {
            INTENT_TAB_ID = preferenceManager
                .getButtonTwoGuestTabId(mContext!!)
            checkIntent(INTENT_TAB_ID)
        }
        mRelThree!!.setOnClickListener {
            INTENT_TAB_ID = preferenceManager
                .getButtonThreeGuestTabId(mContext!!)
            checkIntent(INTENT_TAB_ID)
        }
        mRelFour!!.setOnClickListener {
            INTENT_TAB_ID = preferenceManager
                .getButtonFourGuestTabId(mContext!!)
            checkIntent(INTENT_TAB_ID)
        }
        mRelFive!!.setOnClickListener {
            INTENT_TAB_ID = preferenceManager
                .getButtonFiveGuestTabId(mContext!!)
            checkIntent(INTENT_TAB_ID)
        }
        mRelSix!!.setOnClickListener {
            INTENT_TAB_ID = preferenceManager
                .getButtonSixGuestTabId(mContext!!)
            checkIntent(INTENT_TAB_ID)
        }
        mRelSeven!!.setOnClickListener {
            INTENT_TAB_ID = preferenceManager
                .getButtonSevenGuestTabId(mContext!!)
            checkIntent(INTENT_TAB_ID)
        }
        mRelEight!!.setOnClickListener {
            INTENT_TAB_ID = preferenceManager
                .getButtonEightGuestTabId(mContext!!)
            checkIntent(INTENT_TAB_ID)
        }
        mRelNine!!.setOnClickListener {
            INTENT_TAB_ID = preferenceManager
                .getButtonNineGuestTabId(mContext!!)
            checkIntent(INTENT_TAB_ID)
        }
    }

    private fun checkIntent(intentTabId: String?) {
        tabIDToProceed = intentTabId!!
        var mFragment: Fragment? = null
//        HomeListActivity.settingsButton.setVisibility(View.VISIBLE)
        if (intentTabId.equals(NaisTabConstants.TAB_NOTIFICATIONS_GUEST, ignoreCase = true)) {
            mFragment = NotificationsFragment(
                NaisClassNameConstants.NOTIFICATIONS,
                NaisTabConstants.TAB_NOTIFICATIONS_GUEST
            )
            fragmentIntent(mFragment)
        } else if (intentTabId.equals(
                NaisTabConstants.TAB_COMMUNICATIONS_GUEST,
                ignoreCase = true
            )
        ) {
            mFragment = CommunicationsFragment(
                NaisClassNameConstants.COMMUNICATIONS,
                NaisTabConstants.TAB_COMMUNICATIONS_GUEST
            )
            fragmentIntent(mFragment!!)
        } else if (intentTabId.equals(
                NaisTabConstants.TAB_PARENT_ESSENTIALS_GUEST,
                ignoreCase = true
            )
        ) {
            mFragment = ParentEssentialsFragment(
                NaisClassNameConstants.PARENT_ESSENTIALS,
                NaisTabConstants.TAB_PARENT_ESSENTIALS_GUEST
            )
            fragmentIntent(mFragment)
        } else if (intentTabId.equals(NaisTabConstants.TAB_PROGRAMMES_GUEST, ignoreCase = true)) {
            mFragment = CategoryMainFragment("Programmes", NaisTabConstants.TAB_PROGRAMMES_GUEST)
            fragmentIntent(mFragment)
        } else if (intentTabId.equals(NaisTabConstants.TAB_SETTINGS, ignoreCase = true)) {
//            HomeListActivity.imageButton2.setVisibility(View.GONE)
            mFragment =
                SettingsFragment(NaisClassNameConstants.SETTINGS, NaisTabConstants.TAB_SETTINGS)
            fragmentIntent(mFragment)
        } else if (intentTabId.equals(NaisTabConstants.TAB_LOGOUT_GUEST, ignoreCase = true)) {
//            HomeListActivity.imageButton2.setVisibility(View.GONE)
            if (appUtils.checkInternet(mContext!!)) {
                appUtils.showDialogAlertLogout(
                    activity,
                    "Confirm?",
                    "Do you want to logout?",
                    R.drawable.questionmark_icon,
                    R.drawable.round
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
        } else if (intentTabId.equals(NaisTabConstants.TAB_SOCIAL_MEDIA_GUEST, ignoreCase = true)) {
            mFragment = SocialMediaFragment(
                NaisClassNameConstants.SOCIAL_MEDIA,
                NaisTabConstants.TAB_SOCIAL_MEDIA_GUEST
            )
            fragmentIntent(mFragment)
        } else if (intentTabId.equals("15", ignoreCase = true)) {
            mFragment = AboutUsFragment(NaisClassNameConstants.ABOUT_US, "15")
            fragmentIntent(mFragment)
        } else if (intentTabId.equals(NaisTabConstants.TAB_CONTACT_US_GUEST, ignoreCase = true)) {
            mFragment = ContactUsFragment(
                NaisClassNameConstants.CONTACT_US,
                NaisTabConstants.TAB_CONTACT_US_GUEST
            )
            if (Build.VERSION.SDK_INT < 23) {
                fragmentIntent(mFragment)
            } else {
                if (ActivityCompat.checkSelfPermission(
                        activity!!,
                        permissionsRequiredLocation[0]
                    ) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(
                        activity!!,
                        permissionsRequiredLocation[1]
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity!!,
                            permissionsRequiredLocation[0]
                        )
                        || ActivityCompat.shouldShowRequestPermissionRationale(
                            activity!!,
                            permissionsRequiredLocation[1]
                        )
                    ) {
                        val builder = AlertDialog.Builder(
                            activity!!
                        )
                        builder.setTitle("Need Location Permission")
                        builder.setMessage("This module needs location permissions.")
                        builder.setPositiveButton(
                            "Grant"
                        ) { dialog, which ->
                            dialog.cancel()
                            ActivityCompat.requestPermissions(
                                activity!!,
                                permissionsRequiredLocation,
                                PERMISSION_CALLBACK_CONSTANT_LOCATION
                            )
                        }
                        builder.setNegativeButton(
                            "Cancel"
                        ) { dialog, which -> dialog.cancel() }
                        builder.show()
                    } else if (locationPermissionStatus!!.getBoolean(
                            permissionsRequiredLocation[0],
                            false
                        )
                    ) {
                        val builder = AlertDialog.Builder(
                            activity!!
                        )
                        builder.setTitle("Need Location Permission")
                        builder.setMessage("This module needs location permissions.")
                        builder.setPositiveButton(
                            "Grant"
                        ) { dialog, which ->
                            dialog.cancel()
                            locationToSettings = true
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", activity!!.packageName, null)
                            intent.data = uri
                            startActivityForResult(
                                intent,
                                REQUEST_PERMISSION_LOCATION
                            )
                            Toast.makeText(
                                mContext,
                                "Go to settings and grant access to location",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        builder.setNegativeButton(
                            "Cancel"
                        ) { dialog, which ->
                            dialog.cancel()
                            locationToSettings = false
                        }
                        builder.show()
                    } else if (locationPermissionStatus!!.getBoolean(
                            permissionsRequiredLocation[1],
                            false
                        )
                    ) {
                        //Previously Permission Request was cancelled with 'Dont Ask Again',
                        // Redirect to Settings after showing Information about why you need the permission
                        val builder = AlertDialog.Builder(
                            activity!!
                        )
                        builder.setTitle("Need Location Permission")
                        builder.setMessage("This module needs location permissions.")
                        builder.setPositiveButton(
                            "Grant"
                        ) { dialog, which ->
                            dialog.cancel()
                            locationToSettings = true
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", activity!!.packageName, null)
                            intent.data = uri
                            startActivityForResult(
                                intent,
                                REQUEST_PERMISSION_LOCATION
                            )
                            Toast.makeText(
                                mContext,
                                "Go to settings and grant access to location",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        builder.setNegativeButton(
                            "Cancel"
                        ) { dialog, which ->
                            dialog.cancel()
                            locationToSettings = false
                        }
                        builder.show()
                    } else {
                        requestPermissions(
                            permissionsRequiredLocation,
                            PERMISSION_CALLBACK_CONSTANT_LOCATION
                        )
                    }
                    val editor = locationPermissionStatus!!.edit()
                    editor.putBoolean(permissionsRequiredLocation[0], true)
                    editor.commit()
                } else {
                    fragmentIntent(mFragment)
                }
            }
        } else if (intentTabId.equals(NaisTabConstants.TAB_NAS_TODAY, ignoreCase = true)) {
            mFragment =
                NasTodayFragment(NaisClassNameConstants.NAS_TODAY, NaisTabConstants.TAB_NAS_TODAY)
            fragmentIntent(mFragment)
        } else if (intentTabId.equals(NaisTabConstants.TAB_ABOUT_US_GUEST, ignoreCase = true)) {
            mFragment = AboutUsFragment(
                NaisClassNameConstants.ABOUT_US,
                NaisTabConstants.TAB_ABOUT_US_GUEST
            )
            fragmentIntent(mFragment)
        }
    }

    private fun fragmentIntent(mFragment: Fragment) {
        if (mFragment != null) {

            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction()
                .add(R.id.frame_container, mFragment, title)
                .addToBackStack(title).commitAllowingStateLoss()

        }
    }
        private fun initialiseUI() {
            bannerImagePager = rootView.findViewById<View>(R.id.bannerImagePager) as ViewPager
            mRelOne = rootView.findViewById<View>(R.id.relOne) as RelativeLayout?
            mRelTwo = rootView.findViewById<View>(R.id.relTwo) as RelativeLayout
            mRelThree = rootView.findViewById<View>(R.id.relThree) as RelativeLayout
            mRelFour = rootView.findViewById<View>(R.id.relFour) as RelativeLayout
            mRelFive = rootView.findViewById<View>(R.id.relFive) as RelativeLayout
            mRelSix = rootView.findViewById<View>(R.id.relSix) as RelativeLayout
            mRelSeven = rootView.findViewById<View>(R.id.relSeven) as RelativeLayout
            mRelEight = rootView.findViewById<View>(R.id.relEight) as RelativeLayout
            mRelNine = rootView.findViewById<View>(R.id.relNine) as RelativeLayout
            mTxtOne = rootView.findViewById<View>(R.id.relTxtOne) as TextView?
            mImgOne = rootView.findViewById<View>(R.id.relImgOne) as ImageView?
            mTxtTwo = rootView.findViewById<View>(R.id.relTxtTwo) as TextView
            mImgTwo = rootView.findViewById<View>(R.id.relImgTwo) as ImageView
            mTxtThree = rootView.findViewById<View>(R.id.relTxtThree) as TextView
            mImgThree = rootView.findViewById<View>(R.id.relImgThree) as ImageView
            mTxtFour = rootView.findViewById<View>(R.id.relTxtFour) as TextView
            mImgFour = rootView.findViewById<View>(R.id.relImgFour) as ImageView
            mTxtFive = rootView.findViewById<View>(R.id.relTxtFive) as TextView
            mImgFive = rootView.findViewById<View>(R.id.relImgFive) as ImageView
            mTxtSix = rootView.findViewById<View>(R.id.relTxtSix) as TextView
            mImgSix = rootView.findViewById<View>(R.id.relImgSix) as ImageView
            mTxtSeven = rootView.findViewById<View>(R.id.relTxtSeven) as TextView
            mImgSeven = rootView.findViewById<View>(R.id.relImgSeven) as ImageView
            mTxtEight = rootView.findViewById<View>(R.id.relTxtEight) as TextView
            mImgEight = rootView.findViewById<View>(R.id.relImgEight) as ImageView
            mTxtNine = rootView.findViewById<View>(R.id.relTxtNine) as TextView
            mImgNine = rootView.findViewById<View>(R.id.relImgNine) as ImageView

            progressBarDialog = ProgressBarDialog(context!!)
            homeBannerUrlImageArray = ArrayList<String>()
            getVersionInfo()
            if (appUtils.checkInternet(mContext!!)) {
                getBanner()
            } else {

                homeBannerUrlImageArray.add("")
                bannerImagePager!!.adapter = ImagePagerDrawableAdapter(
                    mContext!!,
                    homeBannerUrlImageArray
                )
                Toast.makeText(mContext, "Network Error", Toast.LENGTH_SHORT).show()
            }

            if (homeBannerUrlImageArray != null) {
                val handler = Handler()
                val update = Runnable {
                    if (currentPage == homeBannerUrlImageArray.size) {
                        currentPage = 0
                        bannerImagePager!!.setCurrentItem(
                            currentPage,
                            false
                        )
                    } else {
                        bannerImagePager!!
                            .setCurrentItem(currentPage++, true)
                    }
                }
                val swipeTimer = Timer()
                swipeTimer.schedule(object : TimerTask() {
                    override fun run() {
                        handler.post(update)
                    }
                }, 100, 3000)
            }
            mSectionText = arrayOfNulls<String>(9)
        }

        private fun getVersionInfo(): String {
            val packageInfo = mContext!!.packageManager.getPackageInfo(mContext!!.packageName, 0)
            versionName = packageInfo.versionName
            versionCode = packageInfo.versionCode
            return versionName
        }

        fun getBanner() {
            val call: Call<ResponseBody> = ApiClient.getApiService().getBannerImages(
                preferenceManager.getAccessToken(mContext),
                versionName,
                preferenceManager.getUserId(mContext!!),
                "2"
            )
            progressBarDialog!!.show()
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    progressBarDialog!!.dismiss()
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val responseCode = jsonObject.getString(JSONConstants.JTAG_RESPONSECODE)
                    if (responseCode == "200") {
                        val responseJSONObject =
                            jsonObject.getJSONObject(JSONConstants.JTAG_RESPONSE)
                        val statusCode = responseJSONObject.getString(JSONConstants.JTAG_STATUSCODE)
                        if (statusCode == "303") {
                            val dataArray: JSONArray =
                                responseJSONObject.getJSONArray(JSONConstants.JTAG_RESPONSE_DATA_ARRAY)
                            if (dataArray.length() > 0) {
                                for (i in 0 until dataArray.length()) {
//												JSONObject dataObject = dataArray.getJSONObject(i);
                                    homeBannerUrlImageArray.add(dataArray.optString(i))
                                }
                                bannerImagePager!!.adapter =
                                    ImagePagerDrawableAdapter(mContext!!, homeBannerUrlImageArray)
                            } else {
                                homeBannerUrlImageArray.add("default_banner_home")
                                bannerImagePager!!.adapter = ImagePagerDrawableAdapter(
                                    mContext, homeBannerUrlImageArray
                                )
                            }
//                            android_app_version =
//                                responseJSONObject.optString("android_app_version")
//                            preferenceManager.setVersionFromApi(mContext!!, android_app_version!!)
//                            val versionFromPreference: String =
//                                preferenceManager.getVersionFromApi(mContext!!) as String
//                            versionFromPreference.replace(".", "")
//                            val versionNumberAsInteger = Integer.parseInt(versionFromPreference)
//                            val replaceVersion: String =
//                                appUtils.getVersionInfo(mContext!!) as String
//                            replaceVersion.replace(".", "")
//                            val replaceCurrentVersion = replaceVersion.toInt()
//                            if (!(preferenceManager.getVersionFromApi(mContext!!) as String).equals(
//                                    "",
//                                    ignoreCase = true
//                                )
//                            ) {
//                                if (versionNumberAsInteger > replaceCurrentVersion) {
//                                    appUtils.showDialogAlertUpdate(mContext!!)
//                                }
//                            }
                        } else {
                            Toast.makeText(mContext, "Failure", Toast.LENGTH_SHORT).show()
                        }
                    } else if (responseCode.equals(
                            StatusConstants.RESPONSE_ACCESSTOKEN_MISSING,
                            ignoreCase = true
                        ) ||
                        responseCode.equals(
                            StatusConstants.RESPONSE_ACCESSTOKEN_EXPIRED,
                            ignoreCase = true
                        ) ||
                        responseCode.equals(
                            StatusConstants.RESPONSE_INVALID_TOKEN,
                            ignoreCase = true
                        )
                    ) {
                        appUtils.getToken(mContext!!)
                        getBanner()
                    } else if (responseCode == StatusConstants.RESPONSE_ERROR) {
                        Toast.makeText(mContext, "Failure", Toast.LENGTH_SHORT).show();
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    progressBarDialog!!.dismiss()
                }
            })
        }
}

