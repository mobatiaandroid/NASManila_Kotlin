package com.mobatia.nasmanila.activities.pdf

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.print.PrintManager
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.HeaderManager
import com.mobatia.nasmanila.manager.PreferenceManager
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


var url: String? = null
class PDFViewActivity : AppCompatActivity() {
    lateinit var appUtils: AppUtils
    lateinit var preferenceManager: PreferenceManager
    var relativeHeader: RelativeLayout? = null
    var headermanager: HeaderManager? = null
    var back: ImageView? = null
    var download:android.widget.ImageView? = null
    var backImageView:android.widget.ImageView? = null
    var print:android.widget.ImageView? = null
    var share:android.widget.ImageView? = null
    var pdf: PDFView? = null
    private var pdfUrl: String? = null
    var title:kotlin.String? = null
    var name:kotlin.String? = null
    var extras: Bundle? = null
    var mContext: Context? = null
    var mProgressDialog: ProgressDialog? = null
    private var printManager: PrintManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfview)
        mContext = this
        val builder: VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        extras = intent.extras
        if (extras != null) {
            url = extras!!.getString("pdf_url")
            title = extras!!.getString("title")
            name = extras!!.getString("filename")
            pdfUrl = appUtils.replace(extras!!.getString("pdf_url")!!.replace("&", "%26"))
        }

        printManager = this.getSystemService(PRINT_SERVICE) as PrintManager
        relativeHeader = findViewById(R.id.relativeHeader)

        download = findViewById<ImageView>(R.id.pdfDownloadImgView)
        pdf = findViewById(R.id.pdfView)
        backImageView = findViewById<ImageView>(R.id.backImageView)
        headermanager = HeaderManager(this@PDFViewActivity, title)
        backImageView!!.setOnClickListener(View.OnClickListener { finish() })
        download!!.setOnClickListener(View.OnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl)))
        })
        backImageView!!.setOnClickListener { finish() }
        download!!.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl)))
        }
        if (appUtils.checkInternet(mContext as PDFViewActivity)) {
            PDFViewActivity.loadPDF().execute()
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
    class loadPDF: AsyncTask<String, Void, Void>() {
        private val exception: Exception? = null
        private var dialog: ProgressDialog? = null

        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun doInBackground(vararg params: String?): Void? {
            var u: URL? = null
            val fileName = "document.pdf"
            u = URL(url)
            val c = u.openConnection() as HttpURLConnection
            c.requestMethod = "GET"
            val auth = "SGHCXFTPUser" + ":" + "cXFTPu$3r"
            var encodedAuth: String = Base64.encodeToString(auth.toByteArray(), Base64.DEFAULT)
            encodedAuth = encodedAuth.replace("\n", "")

            c.addRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            c.addRequestProperty("Authorization", "Basic $encodedAuth")
            c.connect()

            val response = c.responseCode
            val PATH = Environment.getExternalStorageDirectory()
                .toString() + "/download/"
            val file = File(PATH)
            if (!file.exists()) {
                file.mkdirs()
            }
            val outputFile = File(file, fileName)
            val fos = FileOutputStream(outputFile)
            val `is` = c.inputStream
            val buffer = ByteArray(1024)
            var len1 = 0
            while (`is`.read(buffer).also { len1 = it } != -1) {
                fos.write(buffer, 0, len1)
            }
            fos.flush()
            fos.close()
            `is`.close()
            return null

        }

        override fun onPreExecute() {
            super.onPreExecute()

        }
    }
}


//todo downloadPDF
