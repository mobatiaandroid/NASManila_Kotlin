package com.mobatia.nasmanila.activities.notifications

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.home.HomeListActivity
import com.mobatia.nasmanila.api.ApiClient
import com.mobatia.nasmanila.constants.JSONConstants
import com.mobatia.nasmanila.constants.StatusConstants
import com.mobatia.nasmanila.fragments.notifications.model.PushNotificationModel
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.HeaderManager
import com.mobatia.nasmanila.manager.PreferenceManager
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AudioAlertActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnPreparedListener, View.OnClickListener {
    private var preferenceManager: PreferenceManager = PreferenceManager()
    private var appUtils: AppUtils = AppUtils()
    private var seekBarProgress: SeekBar? = null
    private var linearLayoutMediaController: LinearLayout? = null
    var btnplay: TextView? = null
    var position = 0
    private var isReset = false
    var mcontext: Context = this
    var player: MediaPlayer? = null
    var id = ""
    var title = ""
    var message = ""
    var date = ""
    var day = ""
    var month = ""
    var year = ""
    var pushDate = ""
    private var pushID = ""
    private var textViewPlayed: TextView? = null
    private var textViewLength: TextView? = null
    private var textcontent: TextView? = null
    private var updateTimer: Timer? = null
    var playerIamge: ImageView? = null
    var alertlist: ArrayList<PushNotificationModel?>? = null
    private var progressBarWait: ProgressBar? = null
    private var url = ""
    private var isplayclicked = false
    var backImg: ImageView? = null
    var home: ImageView? = null
    var mContext: Context? = null
    var relativeHeader: LinearLayout? = null
    var headermanager: HeaderManager? = null
    var mActivity: Activity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_alert)
        mContext = this
        mActivity = this
        val extras = intent.extras
        if (extras != null) {
            day = extras.getString("Day").toString()
            month = extras.getString("Month").toString()
            year = extras.getString("Year").toString()
            pushDate = extras.getString("PushDate").toString()
            pushID = extras.getString("PushID").toString()
        }
        initialiseUI()

    }

    private fun initialiseUI() {
        relativeHeader = findViewById<View>(R.id.relativeHeader) as LinearLayout
        headermanager = HeaderManager(mActivity, "Notification")
        headermanager!!.getHeader(relativeHeader!!, 0)
        backImg = headermanager!!.getLeftButton()
        headermanager!!.setButtonLeftSelector(R.drawable.back,
                R.drawable.back)
        backImg!!.setOnClickListener(this)

        home = headermanager!!.getLogoButton()
        home!!.setOnClickListener {
            val `in` = Intent(mContext, HomeListActivity::class.java)
            `in`.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(`in`)
        }
        linearLayoutMediaController = findViewById<View>(R.id.linearLayoutMediaController) as LinearLayout
        btnplay = findViewById<View>(R.id.btn_play) as TextView
        textViewPlayed = findViewById<View>(R.id.textViewPlayed) as TextView
        textViewLength = findViewById<View>(R.id.textViewLength) as TextView
        textcontent = findViewById<View>(R.id.txt) as TextView
        seekBarProgress = findViewById<View>(R.id.seekBarProgress) as SeekBar
        playerIamge = findViewById<View>(R.id.imageViewPauseIndicator) as ImageView
//        Log.e("Error", alertlist!![position]!!.pushURL!!)
//        url = alertlist!![position]!!.pushURL!!
//        textcontent!!.text = alertlist!![position]!!.pushTitle
//        println("check url$url")
        seekBarProgress!!.progress = 0
        seekBarProgress!!.setOnSeekBarChangeListener(this)
        player = MediaPlayer()
        player!!.setOnPreparedListener(this)
        player!!.setOnCompletionListener(this)
        player!!.setOnSeekCompleteListener(this)
        progressBarWait = findViewById<View>(R.id.progressBarWait) as ProgressBar
        if (appUtils.checkInternet(mcontext)) {
            callPushNotification(pushID)
        } else {
            Toast.makeText(mcontext, resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
        btnplay!!.setOnClickListener(this)

    }

    private fun callPushNotification(pushID: String) {
        val call: Call<ResponseBody> = ApiClient.getApiService().pushNotificationDetail(
                preferenceManager.getAccessToken(mActivity),
                pushID
        )
        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetJavaScriptEnabled")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseString = response.body()!!.string()
                val jsonObject = JSONObject(responseString)
                val responseCode = jsonObject.getString(JSONConstants.JTAG_RESPONSECODE)
                if (responseCode == "200") {
                    val responseJSONObject =
                            jsonObject.getJSONObject(JSONConstants.JTAG_RESPONSE)
                    val statusCode = responseJSONObject.getString(JSONConstants.JTAG_STATUSCODE)
                    if (statusCode.equals(StatusConstants.STATUS_SUCCESS, ignoreCase = true)) {
                        val dataArray: JSONArray = responseJSONObject.getJSONArray(JSONConstants.JTAG_RESPONSE_DATA_ARRAY)
                        for (i in 0 until dataArray.length()) {
                            val dataObject = dataArray.getJSONObject(i)
                            id = dataObject.optString("id")
                            title = dataObject.optString("title")
                            message = dataObject.optString("message")
                            url = dataObject.optString("url")
                            date = dataObject.optString("time_Stamp")
                            player!!.setDataSource(url)
                            player!!.prepare()
                            player!!.start()


                        }
                    } else if (statusCode.equals(StatusConstants.RESPONSE_ACCESSTOKEN_EXPIRED, ignoreCase = true) ||
                            statusCode.equals(StatusConstants.RESPONSE_ACCESSTOKEN_MISSING, ignoreCase = true) ||
                            statusCode.equals(StatusConstants.RESPONSE_INVALID_TOKEN, ignoreCase = true)) {
                        appUtils.getToken(mActivity!!)
                        callPushNotification(pushID)
                    }
                } else if (responseCode.equals(StatusConstants.RESPONSE_ACCESSTOKEN_EXPIRED, ignoreCase = true) ||
                        responseCode.equals(StatusConstants.RESPONSE_ACCESSTOKEN_MISSING, ignoreCase = true) ||
                        responseCode.equals(StatusConstants.RESPONSE_INVALID_TOKEN, ignoreCase = true)) {
                    appUtils.getToken(mActivity!!)
                    callPushNotification(pushID)
                } else {
                    Toast.makeText(mActivity, "Some Error Occured", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }

    private fun updateMediaProgress() {
        updateTimer = Timer("progress Updater")
        updateTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (player != null) {
                        seekBarProgress!!.progress = player!!
                                .currentPosition / 1000
                    }
                }
            }
        }, 0, 1000)
    }


    override fun onCompletion(mp: MediaPlayer?) {
        mp!!.stop()
        btnplay!!.text = resources.getString(R.string.play)
        updateTimer?.cancel()
        player!!.reset()
        isReset = true
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        progressBarWait!!.visibility = View.GONE
        btnplay!!.visibility = View.VISIBLE
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        if (!fromUser) {
            textViewPlayed!!.text = (appUtils.durationInSecondsToString(progress))
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        TODO("Not yet implemented")
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

        // TODO Auto-generated method stub
        if (player!!.isPlaying) {
            progressBarWait!!.visibility = View.GONE
            player!!.seekTo(seekBar!!.progress * 1000)
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {


        val duration = mp!!.duration / 1000


        seekBarProgress!!.max = duration
        textViewLength!!.text = (appUtils.durationInSecondsToString(duration))
        progressBarWait!!.visibility = View.GONE

        if (!mp.isPlaying) {
            playerIamge!!.setBackgroundResource(R.drawable.mic)
            btnplay!!.visibility = View.VISIBLE
            btnplay!!.text = resources.getString(R.string.pause)
            mp.start()
            updateMediaProgress()
            linearLayoutMediaController!!.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {

        // TODO Auto-generated method stub
        if (v === btnplay) {
            if (!isplayclicked) {
                if (player!!.isPlaying) {
                    println("is come click second")
                    player!!.pause()
                    playerIamge!!.background = resources
                            .getDrawable(R.drawable.mic)
                    btnplay!!.text = resources.getString(R.string.play)
                }
                isplayclicked = true
            } else {
                if (player == null || isReset) {
                    if (appUtils.checkInternet(mcontext)) {
                        player!!.start()
                        playerIamge
                                ?.setBackgroundResource(R.drawable.michover)
                        isReset = false
                    } else {
                        Toast.makeText(mcontext, resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (appUtils.checkInternet(mcontext)) {
                        player!!.start()
                        playerIamge
                                ?.setBackgroundResource(R.drawable.michover)
                        btnplay!!.text = resources
                                .getString(R.string.pause)
                    } else {
                        Toast.makeText(mcontext, resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                    }
                }
                isplayclicked = false
            }
        } else if (v === backImg) {
            finish()
        }
    }


}