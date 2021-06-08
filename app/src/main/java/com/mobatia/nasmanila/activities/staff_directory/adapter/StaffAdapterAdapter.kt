package com.mobatia.nasmanila.activities.staff_directory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.staff_directory.model.StaffModel
import java.util.*

class StaffAdapterAdapter(
    context: Context?,
    deptArrayList: ArrayList<String>,
    hashmap: HashMap<String, ArrayList<StaffModel>>
): RecyclerView.Adapter<StaffAdapterAdapter.MyViewHolder>(){
    var mStaffModels: ArrayList<StaffModel>? = null
    var deptArrayList: ArrayList<String>? = null
    var hashmap: HashMap<String, ArrayList<StaffModel>>? = null
    var mContext: Context? = null
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var deptLayout: LinearLayout? = null
        var separator: View? = null
        var deptName: TextView? = null
        var mStaffDeptListView: RecyclerView? = null
        init {
            deptLayout = view.findViewById<View>(R.id.deptLayout) as LinearLayout
            mStaffDeptListView = view.findViewById<View>(R.id.mStaffDepListView) as RecyclerView
            deptName = view.findViewById<View>(R.id.departmentName) as TextView
            separator = view.findViewById(R.id.separator)
            mStaffDeptListView!!.setHasFixedSize(true)

            val llm : LinearLayoutManager = LinearLayoutManager(mContext)
            llm.orientation = LinearLayoutManager.VERTICAL
            mStaffDeptListView!!.layoutManager = llm
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.customadapter_staffdept_listitem, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (hashmap!![deptArrayList!![position]]!!.size > 0) {
            holder.deptName!!.text = deptArrayList!![position]
        }  else {
            holder.deptLayout!!.visibility = View.GONE
        }

        holder.separator!!.visibility = View.GONE
        val customStaffDeptRecyclerAdapter = CustomStaffDeptRecyclerAdapter(
            mContext,
            hashmap!![deptArrayList!![position]]!!, "list"
        )
        holder.mStaffDeptListView!!.adapter = customStaffDeptRecyclerAdapter
    }

    override fun getItemCount(): Int {
        println("Adapter---size" + deptArrayList!!.size)
        return deptArrayList!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}