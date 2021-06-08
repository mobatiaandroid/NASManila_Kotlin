package com.mobatia.nasmanila.activities.staff_directory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.staff_directory.model.StaffModel
import java.util.*

class CustomStaffDirectoryAdapter(
    mContext: Context?,
    mStaffDirectoryListArray: ArrayList<StaffModel>
) : RecyclerView.Adapter<CustomStaffDirectoryAdapter.MyViewHolder>(){
    private val mContext: Context? = null
    private val mStaffList: ArrayList<StaffModel>? = null
    var dept: String? = null
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var mTitleTxt: TextView? = null
        init {
            mTitleTxt = view.findViewById<View>(R.id.listTxtTitle) as TextView
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomStaffDirectoryAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_aboutus_list_adapter, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CustomStaffDirectoryAdapter.MyViewHolder, position: Int) {
        holder.mTitleTxt!!.text = mStaffList!![position].staffCategoryName
    }

    override fun getItemCount(): Int {
        return mStaffList!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}