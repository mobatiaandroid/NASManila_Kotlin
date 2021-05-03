package com.mobatia.nasmanila.activities.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.TypedArray
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.view.GestureDetector
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.adapter.HomeListAdapter
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.constants.NaisTabConstants
import com.mobatia.nasmanila.fragments.about_us.AboutUsFragment
import com.mobatia.nasmanila.fragments.absences.AbsenceFragment
import com.mobatia.nasmanila.fragments.calendar.CalendarWebViewFragment
import com.mobatia.nasmanila.fragments.category_main.CategoryMainFragment
import com.mobatia.nasmanila.fragments.contact_us.ContactUsFragment
import com.mobatia.nasmanila.fragments.home.HomeScreenGuestUserFragment
import com.mobatia.nasmanila.fragments.home.HomeScreenRegisteredUserFragment
import com.mobatia.nasmanila.fragments.notifications.NotificationsFragment
import com.mobatia.nasmanila.fragments.parent_essentials.ParentEssentialsFragment
import com.mobatia.nasmanila.fragments.parents_evening.ParentsEveningFragment
import com.mobatia.nasmanila.fragments.settings.SettingsFragment
import com.mobatia.nasmanila.fragments.social_media.SocialMediaFragment
import com.mobatia.nasmanila.manager.PreferenceManager

class HomeListActivity : AppCompatActivity() {
    lateinit var preferenceManager: PreferenceManager
    var linearLayout: LinearLayout? = null
    private lateinit var mHomeListView: ListView
    private var mListAdapter: HomeListAdapter? = null
    private var mContext: Context? = null
    private var mActivity: Activity? = null
    private var mDrawerToggle: androidx.legacy.app.ActionBarDrawerToggle? = null
    lateinit var mDrawerLayout: DrawerLayout
    lateinit var mListItemArray: Array<String>
    lateinit var mListImgArray: TypedArray
    private var mDetector: GestureDetector? = null
    private var mFragment: Fragment? = null
    var sPosition = 0
    lateinit var downArrow: ImageView
    private val preLast = 0
    var notificationRecieved = 0
    var extras: Bundle? = null
    var drawerButton: ImageView? = null
    var settingsButton: ImageView? = null
    private val PERMISSION_CALLBACK_CONSTANT_CALENDAR = 1
    private val PERMISSION_CALLBACK_CONSTANT_EXTERNAL_STORAGE = 2
    private val PERMISSION_CALLBACK_CONSTANT_LOCATION = 3
    private val REQUEST_PERMISSION_CALENDAR = 101
    private val REQUEST_PERMISSION_EXTERNAL_STORAGE = 102
    private val REQUEST_PERMISSION_LOCATION = 103
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
    private var calendarToSettings = false
    private val externalStorageToSettings = false
    private var locationToSettings = false
    var tabPositionProceed = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_home_list)
        preferenceManager = PreferenceManager()
        mContext = this
        mActivity = this
        calendarPermissionStatus = getSharedPreferences("calendarPermissionStatus", MODE_PRIVATE)
        externalStoragePermissionStatus = getSharedPreferences(
            "externalStoragePermissionStatus",
            MODE_PRIVATE
        )
        locationPermissionStatus = getSharedPreferences("locationPermissionStatus", MODE_PRIVATE)
        extras = intent.extras
        if (extras != null) {
            notificationRecieved = extras!!.getInt("Notification_Recieved", 0)
        }
        initialiseUI()
        initialSettings()
        if (notificationRecieved == 1) {
            displayView(0)
            displayView(2)
        } else
            displayView(0)

    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        mDrawerToggle!!.syncState()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle!!.onConfigurationChanged(newConfig)

    }


    private fun displayView(position: Int) {
        mFragment = null
        tabPositionProceed = position
        if (preferenceManager.getUserId(mContext!!) != "") {
            settingsButton!!.visibility = View.VISIBLE
        } else {}
        if(preferenceManager.getUserId(mContext!!) == "") {
            when (position) {
                0 -> {
                    settingsButton!!.visibility = View.GONE
                    mFragment = HomeScreenGuestUserFragment(
                        mListItemArray[position], mDrawerLayout, mHomeListView, linearLayout,
                        mListItemArray, mListImgArray
                    )
                    replaceFragmentsSelected(position)
                }
                1 -> {
                    settingsButton!!.visibility = View.VISIBLE
                    mFragment = NotificationsFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_NOTIFICATIONS_GUEST
                    )
                    replaceFragmentsSelected(position)
                }
                2 -> {
                    settingsButton!!.visibility = View.VISIBLE
                    mFragment = ParentEssentialsFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_PARENT_ESSENTIALS_GUEST
                    )
                    replaceFragmentsSelected(position)
                }
                3 -> {
                    settingsButton!!.visibility = View.VISIBLE
                    mFragment = CategoryMainFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_PROGRAMMES_GUEST
                    )
                    replaceFragmentsSelected(position)
                }
                4 -> {
                    settingsButton!!.visibility = View.VISIBLE
                    mFragment = SocialMediaFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_SOCIAL_MEDIA
                    )
                    replaceFragmentsSelected(position)
                }
                5 -> {
                    // about us
                    settingsButton!!.visibility = View.VISIBLE
                    mFragment = AboutUsFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_ABOUT_US_GUEST
                    )
                    replaceFragmentsSelected(position)
                }
                6 -> {
                    settingsButton!!.visibility = View.VISIBLE
                    mFragment = ContactUsFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_CONTACT_US_GUEST
                    )
                    if (Build.VERSION.SDK_INT < 23) {
                        replaceFragmentsSelected(position)
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                mActivity!!,
                                permissionsRequiredLocation[0]
                            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                                mActivity!!,
                                permissionsRequiredLocation[1]
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    mActivity!!,
                                    permissionsRequiredLocation[0]
                                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                    mActivity!!,
                                    permissionsRequiredLocation[1]
                                )
                            ) {
                                val builder = AlertDialog.Builder(mActivity!!)
                                builder.setTitle("Need Location Permission")
                                builder.setMessage("This module needs location permissions.")
                                builder.setPositiveButton("Grant") { dialog, which ->
                                    dialog.cancel()
                                    ActivityCompat.requestPermissions(
                                        mActivity!!,
                                        permissionsRequiredLocation,
                                        PERMISSION_CALLBACK_CONSTANT_LOCATION
                                    )
                                }
                                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                                builder.show()
                            } else if (locationPermissionStatus!!.getBoolean(
                                    permissionsRequiredLocation[0],
                                    false
                                )
                            ) {
                                val builder = AlertDialog.Builder(mActivity!!)
                                builder.setTitle("Need Location Permission")
                                builder.setMessage("This module needs location permissions.")
                                builder.setPositiveButton("Grant") { dialog, which ->
                                    dialog.cancel()
                                    locationToSettings = true
                                    Toast.makeText(
                                        mContext,
                                        "Go to settings and grant access to location",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                builder.setNegativeButton("Cancel") { dialog, which ->
                                    dialog.cancel()
                                    locationToSettings = false
                                }
                                builder.show()
                            } else if (locationPermissionStatus!!.getBoolean(
                                    permissionsRequiredLocation[1],
                                    false
                                )
                            ) {
                                val builder = AlertDialog.Builder(mActivity!!)
                                builder.setTitle("Need Location Permission")
                                builder.setMessage("This module needs location permissions.")
                                builder.setPositiveButton("Grant") { dialog, which ->
                                    dialog.cancel()
                                    locationToSettings = true
                                    val intent =
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri =
                                        Uri.fromParts("package", mActivity!!.packageName, null)
                                    intent.data = uri
                                    startActivityForResult(intent, REQUEST_PERMISSION_LOCATION)
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
                            editor.apply()
                        } else {
                            replaceFragmentsSelected(position)
                        }
                    }
                }
            }
        } else {
            when (position) {
                0 -> {
                    mFragment = HomeScreenRegisteredUserFragment(
                        mListItemArray[position],
                        mDrawerLayout,
                        mHomeListView,
                        linearLayout,
                        mListItemArray,
                        mListImgArray
                    )
                    replaceFragmentsSelected(position)
                }
                1 -> {
                    mFragment = CalendarWebViewFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_CALENDAR_REG
                    )
                    replaceFragmentsSelected(position)
                }
                2 -> {
                    mFragment = NotificationsFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_NOTIFICATIONS_REG
                    )
                    replaceFragmentsSelected(position)
                }
                3 -> {
                    mFragment = AbsenceFragment(
                        mListItemArray[position], NaisTabConstants.TAB_ABSENCES_REG
                    )
                    replaceFragmentsSelected(position)
                }
                4 -> {
                    mFragment = ParentEssentialsFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_PARENT_ESSENTIALS_REG
                    )
                    replaceFragmentsSelected(position)
                }
                5 -> {
                    mFragment = CategoryMainFragment(
                        mListItemArray[position], NaisTabConstants.TAB_PROGRAMMES_REG
                    )
                    replaceFragmentsSelected(position)
                }
                6 -> {
                    mFragment = ParentsEveningFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_PARENTS_MEETING_REG
                    )
                    replaceFragmentsSelected(position)
                }
                7 -> {
                    mFragment = SocialMediaFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_SOCIAL_MEDIA_REG
                    )
                    replaceFragmentsSelected(position)
                }
                8 -> {
                    mFragment = AboutUsFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_ABOUT_US_REG
                    )
                    replaceFragmentsSelected(position)
                }
                9 -> {
                    mFragment = ContactUsFragment(
                        mListItemArray[position],
                        NaisTabConstants.TAB_CONTACT_US_REG
                    )
                    if (Build.VERSION.SDK_INT < 23) {
                        replaceFragmentsSelected(position)
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                mActivity!!,
                                permissionsRequiredLocation[0]
                            ) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(
                                mActivity!!,
                                permissionsRequiredLocation[1]
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    mActivity!!,
                                    permissionsRequiredLocation[0]
                                )
                                || ActivityCompat.shouldShowRequestPermissionRationale(
                                    mActivity!!,
                                    permissionsRequiredLocation[1]
                                )
                            ) {
                                val builder = AlertDialog.Builder(
                                    mActivity!!
                                )
                                builder.setTitle("Need Location Permission")
                                builder.setMessage("This module needs location permissions.")
                                builder.setPositiveButton(
                                    "Grant"
                                ) { dialog, which ->
                                    dialog.cancel()
                                    ActivityCompat.requestPermissions(
                                        mActivity!!,
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
                                    mActivity!!
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
                                    val uri =
                                        Uri.fromParts("package", mActivity!!.packageName, null)
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
                                val builder = AlertDialog.Builder(
                                    mActivity!!
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
                                    val uri =
                                        Uri.fromParts("package", mActivity!!.packageName, null)
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
                                ActivityCompat.requestPermissions(
                                    mActivity!!,
                                    permissionsRequiredLocation,
                                    PERMISSION_CALLBACK_CONSTANT_LOCATION
                                )
                            }
                            val editor = locationPermissionStatus!!.edit()
                            editor.putBoolean(permissionsRequiredLocation[0], true)
                            editor.commit()
                        } else {
                            replaceFragmentsSelected(position)
                        }
                    }
                }
            }
        }
    }

    private fun replaceFragmentsSelected(position: Int) {
        settingsButton!!.visibility = View.VISIBLE
        if (mFragment != null) {
            val fragmentManager = supportFragmentManager
            fragmentManager
                .beginTransaction()
                .replace(
                    R.id.frame_container, mFragment!!,
                    mListItemArray[position]
                )
                .addToBackStack(mListItemArray[position]).commitAllowingStateLoss()
            mHomeListView.setItemChecked(position, true)
            mHomeListView.setSelection(position)
            supportActionBar?.setTitle(R.string.null_value)
            if (mDrawerLayout!!.isDrawerOpen(linearLayout!!)) {
                mDrawerLayout!!.closeDrawer(linearLayout!!)
            }
        }
    }

    private fun initialSettings() {
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.custom_action_view_home)
        supportActionBar!!.elevation = 0f

        val view = supportActionBar!!.customView
        val toolbar = view.parent as Toolbar
        toolbar.setContentInsetsAbsolute(0, 0)

        drawerButton = view.findViewById<View>(R.id.action_bar_back) as ImageView
        drawerButton!!.setOnClickListener {
            val fm = supportFragmentManager
            fm.findFragmentById(R.id.frame_container)
            if (mDrawerLayout!!.isDrawerOpen(linearLayout!!)) {
                mDrawerLayout!!.closeDrawer(linearLayout!!)
            } else {
                mDrawerLayout!!.openDrawer(linearLayout!!)
            }
        }
        settingsButton = view.findViewById<View>(R.id.action_bar_forward) as ImageView
        val logoClickImgView = view.findViewById<View>(R.id.logoClickImgView) as ImageView
        logoClickImgView.setOnClickListener {
            val fm = supportFragmentManager
            val currentFragment = fm.findFragmentById(R.id.frame_container)
            if (currentFragment != null) {
                if (currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.about_us.AboutUsFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.absence.AbsenceFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.calendar.CalendarFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.calendar.CalendarWebViewFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.category1.CategoryMainFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.category2.CategoryFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.parentassociation.ParentAssociationsFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.cca.CcaFragment", ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.communications.CommunicationsFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.contact_us.ContactUsFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.gallery.GalleryFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.early_years.EarlyYearsFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.social_media.SocialMediaFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.ib_programme.IbProgrammeFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.nae_programmes.NaeProgrammesFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.notifications.NotificationsFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.parent_essentials.ParentEssentialsFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.performing_arts.PerformingArtsFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.primary.PrimaryFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.secondary.SecondaryFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.sports.SportsFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.contact_us.ContactUsFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.nas_today.NasTodayFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.parents_evening.ParentsEveningFragment",
                        ignoreCase = true
                    )
                    || currentFragment.javaClass.toString().equals(
                        "class com.mobatia.nasmanila.fragments.settings.SettingsFragment",
                        ignoreCase = true
                    )
                ) {
                    displayView(0)
                    if (preferenceManager.getUserId(mContext!!) == "") {
                        settingsButton!!.visibility = View.GONE
                    } else {
                        settingsButton!!.visibility = View.VISIBLE
                    }
                }
            }
        }
        if (preferenceManager.getUserId(mContext!!) == "") {
            settingsButton!!.visibility = View.GONE
        } else {
            settingsButton!!.visibility = View.VISIBLE
        }
        settingsButton!!.setOnClickListener {
            val fm = supportFragmentManager
            val currentFragment = fm.findFragmentById(R.id.frame_container)
            println(
                "nas current fragment "
                        + currentFragment!!.javaClass.toString()
            )
            if (!currentFragment.javaClass.toString().equals(
                    "class com.mobatia.nasmanila.fragments.settings.SettingsFragment",
                    ignoreCase = true
                )) {
                mFragment = SettingsFragment(
                    NaisClassNameConstants.SETTINGS,
                    NaisTabConstants.TAB_SETTINGS
                )
                if (mFragment != null) {
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction()
                            .add(R.id.frame_container, mFragment!!, NaisClassNameConstants.SETTINGS)
                            .addToBackStack(NaisClassNameConstants.SETTINGS).commit()
                    mDrawerLayout!!.closeDrawer(linearLayout!!)
                    supportActionBar!!.setTitle(R.string.null_value)
                }
                if (preferenceManager.getUserId(mContext!!) == "") {
                    settingsButton!!.visibility = View.GONE
                } else {
                    settingsButton!!.visibility = View.VISIBLE
                }
            }
        }
        mDrawerToggle!!.syncState()
    }
    private fun initialiseUI() {
        mHomeListView = findViewById(R.id.homeList)
        downArrow = findViewById(R.id.downarrow)
        linearLayout = findViewById(R.id.linearLayout)
        if (preferenceManager.getUserId(mContext!!) != "") {
            // registered user
            mListItemArray = mContext!!.resources.getStringArray(
                R.array.home_list_content_reg_items
            )
            mListImgArray = mContext!!.resources.obtainTypedArray(
                R.array.home_list_reg_icons
            )
        } else {
            // guest user
            mListItemArray = mContext!!.resources.getStringArray(
                R.array.home_list_content_guest_items
            )
            mListImgArray = mContext!!.resources.obtainTypedArray(
                R.array.home_list_guest_icons
            )
        }
        mListAdapter = HomeListAdapter(
            mContext!!, mListItemArray,
            mListImgArray, R.layout.custom_list_adapter, true
        )
        mHomeListView.adapter = mListAdapter
                mHomeListView.setBackgroundColor(
                    resources.getColor(
                        R.color.split_bg
                    )
                )
        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
//                mHomeListView.setOnItemLongClickListener(this);
//        mDetector = GestureDetector(this, GestureDetector.OnGestureListener)
        mDrawerToggle = object : androidx.legacy.app.ActionBarDrawerToggle(mContext as Activity?, mDrawerLayout, R.drawable.hamburgerbtn, R.string.null_value, R.string.null_value)

        {
            //Commented code--Nithin

//            override fun onDrawerClosed(view: View) {
//                mDrawerLayout!!.setOnTouchListener { v, event -> mDetector!!.onTouchEvent(event) }
//                supportInvalidateOptionsMenu()
//            }
//
//            override fun onDrawerOpened(drawerView: View) {
//                mDrawerLayout?.setOnTouchListener { v, event -> mDetector!!.onTouchEvent(event) }
//                supportInvalidateOptionsMenu()
//            }

        }
        mDrawerLayout!!.setDrawerListener(mDrawerToggle)
//        mDrawerLayout!!.setOnTouchListener { v, event -> mDetector!!.onTouchEvent(event) }
        mHomeListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            var mLastFirstVisibleItem = 0
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (view.id == mHomeListView.id) {
                    val currentFirstVisibleItem = mHomeListView.lastVisiblePosition
                    if (currentFirstVisibleItem == totalItemCount - 1) {
                        downArrow.visibility = View.INVISIBLE
                    } else {
                        downArrow.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_CALLBACK_CONSTANT_CALENDAR) {
//            //check if all permissions are granted
//            var allgranted = false
//            for (i in grantResults.indices) {
//                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                    allgranted = true
//                } else {
//                    allgranted = false
//                    break
//                }
//            }
//            if (allgranted) {
//                proceedAfterPermission(tabPositionProceed)
//            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    mActivity!!,
//                    Manifest.permission.READ_CALENDAR
//                )
//            ) {
//                val builder = AlertDialog.Builder(
//                    mActivity!!
//                )
//                builder.setTitle("Need Calendar Permissions")
//                builder.setMessage("This module needs calendar permissions.")
//                builder.setPositiveButton(
//                    "Grant"
//                ) { dialog, which ->
//                    dialog.cancel()
//                    calendarToSettings = false
//                    requestPermissions(
//                        permissionsRequiredCalendar,
//                        PERMISSION_CALLBACK_CONSTANT_CALENDAR
//                    )
//                }
//                builder.setNegativeButton(
//                    "Cancel"
//                ) { dialog, which ->
//                    calendarToSettings = false
//                    dialog.cancel()
//                }
//                builder.show()
//            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    mActivity!!,
//                    Manifest.permission.WRITE_CALENDAR
//                )
//            ) {
//                val builder = AlertDialog.Builder(
//                    mActivity!!
//                )
//                builder.setTitle("Need Calendar Permissions")
//                builder.setMessage("This module needs calendar permissions.")
//                builder.setPositiveButton(
//                    "Grant"
//                ) { dialog, which ->
//                    dialog.cancel()
//                    calendarToSettings = false
//                    requestPermissions(
//                        permissionsRequiredCalendar,
//                        PERMISSION_CALLBACK_CONSTANT_CALENDAR
//                    )
//                }
//                builder.setNegativeButton(
//                    "Cancel"
//                ) { dialog, which ->
//                    calendarToSettings = false
//                    dialog.cancel()
//                }
//                builder.show()
//            } else {
////                Toast.makeText(mActivity,"Unable to get Permission",Toast.LENGTH_LONG).show();
//                calendarToSettings = true
//                println("Permission4")
//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                val uri = Uri.fromParts("package", mActivity!!.packageName, null)
//                intent.data = uri
//                startActivityForResult(
//                    intent,
//                    REQUEST_PERMISSION_CALENDAR
//                )
//                Toast.makeText(
//                    mContext,
//                    "Go to settings and grant access to calendar",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        } else
//    }
}