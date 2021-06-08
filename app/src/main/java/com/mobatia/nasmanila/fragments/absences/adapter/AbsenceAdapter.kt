package com.mobatia.nasmanila.fragments.absences.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.fragments.absences.model.LeavesModel
import com.mobatia.nasmanila.manager.AppUtils
import com.mobatia.nasmanila.manager.PreferenceManager


class AbsenceAdapter(
    mContext: Context,
    mLeavesList: ArrayList<LeavesModel>
): RecyclerView.Adapter<AbsenceAdapter.ViewHolder>(){
    lateinit var preferenceManager: PreferenceManager
    lateinit var appUtils: AppUtils
    private val context: Context = mContext
    private val leavesList: ArrayList<LeavesModel> = mLeavesList
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var listDate: TextView? = null
        var listStatus:TextView? = null
        var imgIcon: ImageView? = null
        var v: View? = null
        var listBackGround: RelativeLayout? = null
        init {
            listDate = view.findViewById<android.view.View?>(R.id.listDate) as TextView?
            listStatus = view.findViewById<android.view.View?>(R.id.listStatus) as TextView?
            listBackGround = view.findViewById<android.view.View?>(R.id.listBackGround) as RelativeLayout?
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsenceAdapter.ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.absence_recycler_list_item, parent, false)

        return ViewHolder(
            itemView
        )
    }

    override fun onBindViewHolder(holder: AbsenceAdapter.ViewHolder, position: Int) {
        if (leavesList[position].toDate
                .equals(leavesList[position].fromDate)
        ) {
            holder.listDate!!.text = (appUtils.dateParsingToDdMmYyyy(leavesList[position].fromDate!!)
            )
        } else {
            holder.listDate!!.text =
                Html.fromHtml(
                    appUtils.dateParsingToDdMmYyyy(leavesList[position].fromDate!!)
                        .toString() + " to " +
                            appUtils.dateParsingToDdMmYyyy(leavesList[position].toDate!!)
                )
        }

        if (leavesList[position].status.equals("1")) {
            holder.listStatus!!.text = "Approved"
            holder.listStatus!!.setTextColor(context.resources.getColor(R.color.nas_green))
        } else if (leavesList[position].status.equals("2")) {
            holder.listStatus!!.text = "Pending"
            holder.listStatus!!.setTextColor(context.resources.getColor(R.color.rel_six))
        } else if (leavesList[position].status.equals("3")) {
            holder.listStatus!!.text = "Rejected"
            holder.listStatus!!.setTextColor(context.resources.getColor(R.color.rel_nine))
        }
    }

    override fun getItemCount(): Int {
        return leavesList.size
    }
}