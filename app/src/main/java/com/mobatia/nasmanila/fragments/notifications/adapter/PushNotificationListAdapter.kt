package com.mobatia.nasmanila.fragments.notifications.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.notifications.AudioAlertActivity
import com.mobatia.nasmanila.activities.notifications.ImageAlertActivity
import com.mobatia.nasmanila.activities.notifications.TextAlertActivity
import com.mobatia.nasmanila.activities.notifications.VideoAlertActivity
import com.mobatia.nasmanila.constants.IntentPassValueConstants
import com.mobatia.nasmanila.fragments.notifications.OnBottomReachedListener
import com.mobatia.nasmanila.fragments.notifications.model.PushNotificationModel

class PushNotificationListAdapter(
    mContext: Context,
    mPushNotificationList: ArrayList<PushNotificationModel>
): RecyclerView.Adapter<PushNotificationListAdapter.ViewHolder>(){
    private val context: Context = mContext
    private val pushNotificationList: java.util.ArrayList<PushNotificationModel> = mPushNotificationList
    var onBottomReachedListener: OnBottomReachedListener? = null
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView? = null
        var title: TextView? = null
        init {
            image = itemView.findViewById<android.widget.ImageView?>(R.id.Img)
            title = itemView.findViewById<TextView?>(R.id.title)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_adapter_pushlist_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == pushNotificationList.size - 1) {
            onBottomReachedListener!!.onBottomReached(position)
        }

        if (pushNotificationList[position].pushType.equals("Video", ignoreCase = true)) {
            holder.image!!.setImageResource(R.drawable.alerticon_video)
        } else if (pushNotificationList[position].pushType.equals("Text", ignoreCase = true)) {
            holder.image!!.setImageResource(R.drawable.alerticon_text)
        } else if (pushNotificationList[position].pushType.equals("image", ignoreCase = true)) {
            holder.image!!.setImageResource(R.drawable.alerticon_image)
        } else if (pushNotificationList[position].pushType.equals("Voice", ignoreCase = true)) {
            holder.image!!.setImageResource(R.drawable.alerticon_audio)
        }
        holder.title!!.text = pushNotificationList[position].headTitle
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var mIntent = Intent(context, TextAlertActivity::class.java)
                if (pushNotificationList!![position].pushType.equals(
                        "",
                        ignoreCase = true
                    )
                ) {
                    mIntent!!.putExtra(IntentPassValueConstants.POSITION, position)
                    mIntent!!.putExtra("PushID", pushNotificationList!![position].id)
                    mIntent!!.putExtra("Day", pushNotificationList!![position].day)
                    mIntent!!.putExtra(
                        "Month",
                        pushNotificationList!![position].monthString
                    )
                    mIntent!!.putExtra("Year", pushNotificationList!![position].year)
                    mIntent!!.putExtra(
                        "PushDate",
                        pushNotificationList!![position].pushTime
                    )
                    context!!.startActivity(mIntent)
                }
                if (pushNotificationList!![position].pushType.equals(
                        "Image",
                        ignoreCase = true
                    ) || pushNotificationList!![position].pushType.equals(
                        "Text",
                        ignoreCase = true
                    ) || pushNotificationList!![position].pushType.equals(
                        "Text",
                        ignoreCase = true
                    ) || pushNotificationList!![position].pushType.equals(
                        "Image",
                        ignoreCase = true
                    )
                ) {
                    mIntent = Intent(context, ImageAlertActivity::class.java)
                    mIntent!!.putExtra("PushID", pushNotificationList!![position].id)
                    mIntent!!.putExtra("Day", pushNotificationList!![position].day)
                    mIntent!!.putExtra(
                        "Month",
                        pushNotificationList!![position].monthString
                    )
                    mIntent!!.putExtra("Year", pushNotificationList!![position].year)
                    mIntent!!.putExtra(
                        "PushDate",
                        pushNotificationList!![position].pushTime
                    )
                    println("pushID" + pushNotificationList!![position].id)
                    context!!.startActivity(mIntent)
                }
                if (pushNotificationList!![position].pushType.equals(
                        "Voice",
                        ignoreCase = true
                    )
                ) {
                    mIntent = Intent(context, AudioAlertActivity::class.java)
                    mIntent!!.putExtra("PushID", pushNotificationList!![position].id)
                    mIntent!!.putExtra("Day", pushNotificationList!![position].day)
                    mIntent!!.putExtra(
                        "Month",
                        pushNotificationList!![position].monthString
                    )
                    mIntent!!.putExtra("Year", pushNotificationList!![position].year)
                    mIntent!!.putExtra(
                        "PushDate",
                        pushNotificationList!![position].pushTime
                    )
                    context!!.startActivity(mIntent)
                }
                if (pushNotificationList!![position].pushType.equals(
                        "Video",
                        ignoreCase = true
                    )
                ) {
                    mIntent = Intent(context, VideoAlertActivity::class.java)
                    mIntent!!.putExtra("PushID", pushNotificationList!![position].id)
                    mIntent!!.putExtra("Day", pushNotificationList!![position].day)
                    mIntent!!.putExtra(
                        "Month",
                        pushNotificationList!![position].monthString
                    )
                    mIntent!!.putExtra("Year", pushNotificationList!![position].year)
                    mIntent!!.putExtra(
                        "PushDate",
                        pushNotificationList!![position].pushTime
                    )
                    context!!.startActivity(mIntent)
                }
            }

        })
    }

    override fun getItemCount(): Int {
        return pushNotificationList.size
    }

}