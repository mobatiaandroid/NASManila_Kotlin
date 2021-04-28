package com.example.nasmanila.activities.splash

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.nasmanila.R
import com.example.nasmanila.activities.home.HomeListActivity
import com.example.nasmanila.activities.login.LoginActivity
import com.example.nasmanila.activities.tutorial.TutorialActivity
import com.example.nasmanila.constants.IntentPassValueConstants.TYPE
import com.example.nasmanila.manager.AppUtils
import com.example.nasmanila.manager.PreferenceManager
import java.util.*
import android.Manifest
import androidx.core.app.ActivityCompat


class SplashActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    private lateinit var context : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        appUtils = AppUtils()
        preferenceManager = PreferenceManager()
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        context = this
        if (appUtils.checkInternet(context)) {
            /**
             * Generate Token
             * **/
            goToNextView()
        }
        else
            appUtils.showDialogAlertDismiss(
                context as SplashActivity,
                "Network Error",
                getString(R.string.no_internet),
                R.drawable.nonetworkicon,
                R.drawable.roundred
            )
//        Handler().postDelayed({
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }, 3000)

    }

    fun hasPermissions(context: Context, permissions: Array<out String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission: String in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }
    private fun goToNextView() {
//        Handler().postDelayed({
        Handler(Looper.myLooper()!!).postDelayed({
            if (preferenceManager.getIsFirstLaunch(context) && preferenceManager.getUserId(context) == ""
            ) {
                var tutorialIntent: Intent = Intent(context, TutorialActivity::class.java)
                tutorialIntent.putExtra(TYPE, 1)
                startActivity(tutorialIntent)
                finish()
            } else if (preferenceManager.getUserId(context) == "") {
                var loginIntent: Intent = Intent(context, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
            } else {
                var homeIntent: Intent = Intent(context, HomeListActivity::class.java)
                startActivity(homeIntent)
                finish()
            }
        }, 3000)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                val perms: MutableMap<String, Int> = HashMap()
                perms[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.READ_EXTERNAL_STORAGE] =
                    PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.READ_CALENDAR] = PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.WRITE_CALENDAR] =
                    PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.CALL_PHONE] = PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.ACCESS_FINE_LOCATION] =
                    PackageManager.PERMISSION_GRANTED
                for (i in permissions.indices)
                    perms.put(permissions[i], grantResults[i])
                for (i in permissions.indices) {
                    var permission: String = permissions[i]
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        var showRationale = false
                        showRationale = shouldShowRequestPermissionRationale(permission)
                    }
                }
                if (perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.READ_CALENDAR] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.WRITE_CALENDAR] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.CALL_PHONE] == PackageManager.PERMISSION_GRANTED
                            && (grantResults.isNotEmpty()))
                                goToNextView()
                else
                    ActivityCompat.requestPermissions(this, permissions, 100)
                return
//                var allGranted = false
//                for (i in grantResults.indices) {
//                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        allGranted = true
//                    } else {
//                        allGranted = false
//                        break
//                    }
//                }
//                if(allGranted) {
//                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
//                    ||ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])
//                    ||ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[2])
//                    ||ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[3])
//                    ||ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[4])
//                    ||ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[5])) {
//                    getAlertDialog()
//                } else
//                    Toast.makeText(context, "Unable to get permission", Toast.LENGTH_SHORT).show()
            }
            else ->
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        }
    }
}


