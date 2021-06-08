package com.mobatia.nasmanila.fragments.contact_us.adapter

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.fragments.contact_us.model.ContactUsModel
import java.util.*

class ContactUsAdapter(mContext: Context?, contactUsModelsArrayList: ArrayList<ContactUsModel>) : RecyclerView.Adapter<ContactUsAdapter.MyViewHolder>() {
    private val mContext: Context? = null
    private val mStaffList: ArrayList<ContactUsModel>? = null
    var phone = ""
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var mTitleTxt: TextView? = null
        var arrowImg: ImageView? = null
        var subTitle: TextView? = null
        var cotactEmail:TextView? = null
        init {
            mTitleTxt = view.findViewById<android.view.View?>(R.id.contactName) as TextView?
            subTitle = view.findViewById<android.view.View?>(R.id.cotactNumber) as TextView?
            cotactEmail = view.findViewById<android.view.View?>(R.id.cotactEmail) as TextView?
            arrowImg = view.findViewById<android.view.View?>(R.id.arrowImg) as android.widget.ImageView?
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_adapter_contact_us, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.mTitleTxt!!.text = mStaffList!![position].contact_name
        if (mStaffList[position].contact_phone != "") {
            holder.subTitle!!.text = mStaffList[position].contact_phone
        } else {
            holder.subTitle!!.visibility = View.GONE
        }
        if (position == mStaffList.size - 1) {
            holder.arrowImg!!.visibility = View.VISIBLE
        } else {
            holder.arrowImg!!.visibility = View.GONE
        }
        if (mStaffList[position].contact_email.equals(
                "",
                ignoreCase = true
            ) && mStaffList[position].contact_phone.equals("", ignoreCase = true)
            && !mStaffList[position].contact_name.equals("", ignoreCase = true)
        ) {
            val lp = holder.mTitleTxt!!.layoutParams as RelativeLayout.LayoutParams
            lp.addRule(RelativeLayout.CENTER_VERTICAL)
            holder.mTitleTxt!!.layoutParams = lp
        }
        holder.cotactEmail!!.text = mStaffList[position].contact_email
        if (mStaffList[position].contact_email != "") {
            holder.cotactEmail!!.paintFlags = holder.cotactEmail!!.paintFlags
        } else {
            holder.cotactEmail!!.visibility = View.INVISIBLE
        }
        holder.subTitle!!.setOnClickListener {
            var callIntent: Intent? = null
            phone = mStaffList[position].contact_phone!!
            if (Build.VERSION.SDK_INT >= 23) {
//                TedPermission.with(mContext)
//                    .setPermissionListener(permissionlistenerContact)
//                    .setDeniedMessage("If you reject permission,you cannot use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
//                    .setPermissions(Manifest.permission.CALL_PHONE)
//                    .check()
            } else {
                callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + mStaffList[position].contact_phone)
                if (ActivityCompat.checkSelfPermission(
                        mContext!!,
                        Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@setOnClickListener
                }
                mContext!!.startActivity(callIntent)
            } }
        holder.cotactEmail!!.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SEND_MULTIPLE
            )
            val deliveryAddress = arrayOf(holder.cotactEmail!!.text.toString())
            emailIntent.putExtra(Intent.EXTRA_EMAIL, deliveryAddress)
            emailIntent.type = "text/plain"
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val pm: PackageManager = it.context.packageManager
            val activityList = pm.queryIntentActivities(
                emailIntent, 0
            )
            for (app in activityList) {
                println("packge name" + app.activityInfo.name)
                if (app.activityInfo.name.contains("com.google.android.gm")) {
                    val activity = app.activityInfo
                    val name = ComponentName(
                        activity.applicationInfo.packageName, activity.name
                    )
                    emailIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    emailIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    emailIntent.component = name
                    it.context.startActivity(emailIntent)
                    break
                }
            }
        }
    }
//    val permissionlistenerContact: PermissionListener()


    override fun getItemCount(): Int {
        return mStaffList!!.size
    }

}