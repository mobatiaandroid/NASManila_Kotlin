package com.mobatia.nasmanila.activities.nas_today

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.HomeListActivity
import com.mobatia.nasmanila.activities.pdf.PDFViewActivity
import com.mobatia.nasmanila.constants.NaisClassNameConstants
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.HeaderManager
import com.mobatia.nasmanila.manager.PreferenceManager

class NasTodayDetailWebViewActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    private var mContext: Context? = null
    private var mWebView: WebView? = null
    private var mProgressRelLayout: RelativeLayout? = null
    private var mwebSettings: WebSettings? = null
    private lateinit var progressBar: ProgressBar
    private var loadingFlag = true
    private var mLoadUrl: String? = null
    private var mErrorFlag = false
    var extras: Bundle? = null
    var relativeHeader: LinearLayout? = null
    var headermanager: HeaderManager? = null
    var back: ImageView? = null
    var home: ImageView? = null
    var infoImg: ImageView? = null
    var anim: RotateAnimation? = null
    var pdf = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nas_today_detail_web_view)
        mContext = this
        extras = intent.extras
        if (extras != null) {
            mLoadUrl = extras!!.getString("webViewComingDetail")
            pdf = extras!!.getString("pdf")!!
            println("webViewComingUpDetail$mLoadUrl")
        }

        initialiseUI()
        getWebViewSettings()
    }

    private fun getWebViewSettings() {
        progressBar.visibility = View.VISIBLE
        anim = RotateAnimation(
            0F, 360F, Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim!!.setInterpolator(mContext, android.R.interpolator.linear)
        anim!!.repeatCount = Animation.INFINITE
        anim!!.duration = 1000
        progressBar.animation = anim
        progressBar.startAnimation(anim)
        mWebView!!.isFocusable = true
        mWebView!!.isFocusableInTouchMode = true
        mWebView!!.setBackgroundColor(0X00000000)
        mWebView!!.isVerticalScrollBarEnabled = false
        mWebView!!.isHorizontalScrollBarEnabled = false
        mWebView!!.webChromeClient = WebChromeClient()

        mwebSettings = mWebView!!.settings
        mwebSettings!!.saveFormData = true
        mwebSettings!!.builtInZoomControls = false
        mwebSettings!!.setSupportZoom(false)

        mwebSettings!!.pluginState = WebSettings.PluginState.ON
        mwebSettings!!.setRenderPriority(WebSettings.RenderPriority.HIGH)
        mwebSettings!!.javaScriptCanOpenWindowsAutomatically = true
        mwebSettings!!.domStorageEnabled = true
        mwebSettings!!.databaseEnabled = true
        mwebSettings!!.defaultTextEncodingName = "utf-8"
        mwebSettings!!.loadsImagesAutomatically = true

        mWebView!!.settings.setAppCacheMaxSize((10 * 1024 * 1024).toLong()) // 5MB

        mWebView!!.settings.setAppCachePath(
            mContext!!.cacheDir.absolutePath
        )
        mWebView!!.settings.allowFileAccess = true
        mWebView!!.settings.setAppCacheEnabled(true)
        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        mWebView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
            override fun onPageFinished(view: WebView, url: String) {
                progressBar.clearAnimation()
                if (appUtils.checkInternet(mContext!!) && loadingFlag) {
                    view.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                    view.loadUrl(url)
                    loadingFlag = false
                } else if (!appUtils.checkInternet(mContext!!) && loadingFlag) {
                    view.settings.cacheMode = WebSettings.LOAD_CACHE_ONLY
                    view.loadUrl(url)
                    println("CACHE LOADING")
                    loadingFlag = false
                }
            }

            override fun onReceivedError(
                view: WebView, errorCode: Int,
                description: String, failingUrl: String
            ) {
                progressBar.clearAnimation()
                progressBar.visibility = View.GONE
                if (appUtils.checkInternet(mContext!!)) {
                    appUtils.showAlertFinish(
                        mContext as Activity?, resources
                            .getString(R.string.common_error), "",
                        resources.getString(R.string.ok), false
                    )
                }
                super.onReceivedError(view, errorCode, description, failingUrl)
            }
        }

        mErrorFlag = mLoadUrl == ""
        if (mLoadUrl != null && !mErrorFlag) {
            println("BISAD load url $mLoadUrl")
            mWebView!!.loadUrl(mLoadUrl!!)
        } else {
            progressBar.clearAnimation()
            //progressBar.visibility = View.GONE
            appUtils.showAlertFinish(
                mContext as Activity?, resources
                    .getString(R.string.common_error_loading_page), "",
                resources.getString(R.string.ok), false
            )
        }
        mWebView!!.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun initialiseUI() {
        relativeHeader = findViewById<View>(R.id.relativeHeader) as LinearLayout
        mWebView = findViewById<View>(R.id.webView) as WebView
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
//        mProgressRelLayout = findViewById<View>(R.id.progressDialog) as RelativeLayout
        headermanager =
            HeaderManager(this@NasTodayDetailWebViewActivity, NaisClassNameConstants.NAS_TODAY)
        if (pdf.equals("", ignoreCase = true)) {
            headermanager!!.getHeader(relativeHeader!!, 0)
        } else {
            headermanager!!.getHeader(relativeHeader!!, 3)
            infoImg = headermanager!!.getRightInfoImage()
            infoImg!!.setBackgroundResource(R.drawable.pdfdownloadbutton)
            infoImg!!.setOnClickListener {
                if (pdf != "") {
                    val intent = Intent(mContext, PDFViewActivity::class.java)
                    intent.putExtra("pdf_url", pdf)
                    startActivity(intent)
                }
            }
        }
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
    }
}