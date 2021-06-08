package com.mobatia.nasmanila.fragments.about_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.fragments.about_us.adapter.CustomAboutUsAdapter.MyViewHolder
import com.mobatia.nasmanila.fragments.about_us.model.AboutUsModel
import java.util.*

class CustomAboutUsAdapter(activity: FragmentActivity?, mAboutUsListArray: ArrayList<AboutUsModel>?): RecyclerView.Adapter<CustomAboutUsAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val mTitleTxt = itemView!!.findViewById<View>(R.id.listTxtTitle) as TextView

    }

    private val staffList = mAboutUsListArray
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_aboutus_list_adapter, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.mTitleTxt.text = staffList!![position].TabType
    }

    override fun getItemCount(): Int {
        return staffList!!.size
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
//    var context: Context = FragmentActivity
//    var staffList: ArrayList<AboutUsModel> = mAboutUsListArray

}
