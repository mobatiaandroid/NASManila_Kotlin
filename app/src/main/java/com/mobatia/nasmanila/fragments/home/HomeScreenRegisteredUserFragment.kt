package com.mobatia.nasmanila.fragments.home

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.manager.PreferenceManager
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeScreenRegisteredUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeScreenRegisteredUserFragment(
        s: String,
        mDrawerLayout: DrawerLayout?,
        mHomeListView: ListView,
        mLinearLayout: LinearLayout?,
        mListItemArray: Array<String>,
        mListImgArray: TypedArray
) : Fragment() {
    private var preferenceManager: PreferenceManager = PreferenceManager()
    lateinit var rootView: View
    private var mContext: Context? = null
    var title: String = s
    var drawerLayout: DrawerLayout? = mDrawerLayout
    var listView: ListView? = mHomeListView
    var listItemArray: Array<String> = mListItemArray
    var linearLayout: LinearLayout? = mLinearLayout
    var listImageArray: TypedArray = mListImgArray
    var bannerImagePager: ViewPager? = null
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
    private val android_app_version: String? = null
    private val PERMISSION_CALLBACK_CONSTANT_CALENDAR = 1
    private val PERMISSION_CALLBACK_CONSTANT_EXTERNAL_STORAGE = 2
    private val PERMISSION_CALLBACK_CONSTANT_LOCATION = 3
    private val REQUEST_PERMISSION_CALENDAR = 101
    private val REQUEST_PERMISSION_EXTERNAL_STORAGE = 102
    private val REQUEST_PERMISSION_LOCATION = 103
    var permissionsRequiredCalendar = arrayOf(Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR)
    var permissionsRequiredExternalStorage = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    var permissionsRequiredLocation = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)
    private var calendarPermissionStatus: SharedPreferences? = null
    private var externalStoragePermissionStatus: SharedPreferences? = null
    private var locationPermissionStatus: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_home_screen_registered_user, container, false)
        mContext = activity
        calendarPermissionStatus = activity!!.getSharedPreferences("calendarPermissionStatus", Context.MODE_PRIVATE)
        externalStoragePermissionStatus = activity!!.getSharedPreferences("externalStoragePermissionStatus", Context.MODE_PRIVATE)
        locationPermissionStatus = activity!!.getSharedPreferences("locationPermissionStatus", Context.MODE_PRIVATE)
        initialiseUI()
        setListeners()
        setDragListenersForButtons()
        getButtonBgAndTextImages()
        return rootView
    }

    private fun getButtonBgAndTextImages() {
        if (preferenceManager.getButtonOneTextImage(mContext!!)!!.toInt() != 0) {
            val sdk = Build.VERSION.SDK_INT
            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                mImgOne!!.background = mContext!!.resources.getDrawable(R.drawable.news)
            } else {
                mImgOne!!.background = mContext!!.resources.getDrawable(R.drawable.news)
            }
            mTxtOne!!.text = "NAIS MANILA TODAY"
            mRelOne!!.setBackgroundColor(preferenceManager.getButtonOneBg(mContext!!))
        }
        if (preferenceManager.getButtonTwoTextImage(mContext!!)!!.toInt() != 0) {
            mImgTwo!!.setImageDrawable(listImageArray.getDrawable(preferenceManager.getButtonTwoTextImage(mContext!!)!!.toInt()))
            var relTwoStr = ""
            relTwoStr = if (listItemArray[preferenceManager.getButtonTwoTextImage(mContext!!)!!.toInt()].equals(NaisClassNameConstants.CCAS, ignoreCase = true)) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray[preferenceManager.getButtonTwoTextImage(mContext!!)!!.toInt()].toUpperCase()
            }
            mTxtTwo!!.text = relTwoStr
            mRelTwo!!.setBackgroundColor(preferenceManager.getButtonTwoBg(mContext!!))
        }
        if (preferenceManager.getButtonThreeTextImage(mContext!!)!!.toInt() != 0) {
            mImgThree!!.setImageDrawable(listImageArray.getDrawable(preferenceManager.getButtonThreeTextImage(mContext!!)!!.toInt()))
            var relTwoStr: String = if (listItemArray.get(preferenceManager.getButtonThreeTextImage(mContext!!)!!.toInt()).equals(NaisClassNameConstants.CCAS, ignoreCase = true)) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray[preferenceManager.getButtonThreeTextImage(mContext!!)!!.toInt()].toUpperCase(Locale.ROOT)
            }
            mTxtThree!!.text = relTwoStr
            mRelThree!!.setBackgroundColor(preferenceManager.getButtonThreeBg(mContext!!))
        }
        if (preferenceManager.getButtonFourTextImage(mContext!!)!!.toInt() != 0) {
            mImgFour!!.setImageDrawable(listImageArray.getDrawable(preferenceManager.getButtonFourTextImage(mContext!!)!!.toInt()))
            var relTwoStr: String = if (listItemArray[preferenceManager.getButtonFourTextImage(mContext!!)!!.toInt()].equals(NaisClassNameConstants.CCAS, ignoreCase = true)) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray[preferenceManager.getButtonFourTextImage(mContext!!)!!.toInt()].toUpperCase(Locale.ROOT)
            }
            mTxtFour!!.text = relTwoStr
            mRelFour!!.setBackgroundColor(preferenceManager.getButtonFourBg(mContext!!))
        }
        if (preferenceManager.getButtonFiveTextImage(mContext!!)!!.toInt() != 0) {
            mImgFive!!.setImageDrawable(listImageArray.getDrawable(preferenceManager.getButtonFiveTextImage(mContext!!)!!.toInt()))
            var relTwoStr = ""
            relTwoStr = if (listItemArray.get(preferenceManager.getButtonFiveTextImage(mContext!!)!!.toInt()).equals(NaisClassNameConstants.CCAS, ignoreCase = true)) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray.get(preferenceManager.getButtonFiveTextImage(mContext!!)!!.toInt()).toUpperCase()
            }
            mTxtFive!!.text = relTwoStr
            mRelFive!!.setBackgroundColor(preferenceManager
                    .getButtonFiveBg(mContext!!))
        }
        if (preferenceManager
                        .getButtonSixTextImage(mContext!!)!!.toInt() != 0) {
            mImgSix!!.setImageDrawable(listImageArray.getDrawable(preferenceManager
                    .getButtonSixTextImage(mContext!!)!!.toInt()))
            var relTwoStr = ""
            relTwoStr = if (listItemArray.get(preferenceManager
                            .getButtonSixTextImage(mContext!!)!!.toInt()).equals(NaisClassNameConstants.CCAS, ignoreCase = true)) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray.get(preferenceManager
                        .getButtonSixTextImage(mContext!!)!!.toInt()).toUpperCase()
            }
            mTxtSix!!.text = relTwoStr
            mRelSix!!.setBackgroundColor(preferenceManager
                    .getButtonSixBg(mContext!!))
        }
        if (preferenceManager
                        .getButtonSevenTextImage(mContext!!)!!.toInt() != 0) {
            mImgSeven!!.setImageDrawable(listImageArray.getDrawable(preferenceManager
                    .getButtonSevenTextImage(mContext!!)!!.toInt()))
            var relTwoStr = ""
            relTwoStr = if (listItemArray.get(preferenceManager
                            .getButtonSevenTextImage(mContext!!)!!.toInt()).equals(NaisClassNameConstants.CCAS, ignoreCase = true)) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray.get(preferenceManager
                        .getButtonSevenTextImage(mContext!!)!!.toInt()).toUpperCase()
            }
            mTxtSeven!!.text = relTwoStr
            mRelSeven!!.setBackgroundColor(preferenceManager
                    .getButtonSevenBg(mContext!!))
        }
        if (preferenceManager
                        .getButtonEightTextImage(mContext!!)!!.toInt() != 0) {
            mImgEight!!.setImageDrawable(listImageArray.getDrawable(preferenceManager
                    .getButtonEightTextImage(mContext!!)!!.toInt()))
            var relTwoStr = ""
            relTwoStr = if (listItemArray.get(preferenceManager
                            .getButtonEightTextImage(mContext!!)!!.toInt()).equals(NaisClassNameConstants.CCAS, ignoreCase = true)) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray.get(preferenceManager
                        .getButtonEightTextImage(mContext!!)!!.toInt()).toUpperCase()
            }
            mTxtEight!!.text = relTwoStr
            mRelEight!!.setBackgroundColor(preferenceManager
                    .getButtonEightBg(mContext!!))
        }
        if (preferenceManager
                        .getButtonNineTextImage(mContext!!)!!.toInt() != 0) {
            mImgNine!!.setImageDrawable(listImageArray.getDrawable(preferenceManager
                    .getButtonNineTextImage(mContext!!)!!.toInt()))
            var relTwoStr = ""
            relTwoStr = if (listItemArray.get(preferenceManager
                            .getButtonNineTextImage(mContext!!)!!.toInt()).equals(NaisClassNameConstants.CCAS, ignoreCase = true)) {
                NaisClassNameConstants.CCAS
            } else {
                listItemArray.get(preferenceManager
                        .getButtonNineTextImage(mContext!!)!!.toInt()).toUpperCase()
            }
            mTxtNine!!.text = relTwoStr
            mRelNine!!.setBackgroundColor(preferenceManager
                    .getButtonNineBg(mContext!!))
        }
    }

    private fun setDragListenersForButtons() {

    }

    private fun setListeners() {

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
    }

    companion object {
    }
}