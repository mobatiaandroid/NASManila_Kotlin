package com.mobatia.nasmanila.activities.parent_essentials.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.fragments.parent_essentials.model.ParentEssentialsModel
import java.util.*

class ParentEssentialActivityListAdapter(
    mContext: Context,
    list: ArrayList<ParentEssentialsModel>?
) : RecyclerView.Adapter<ParentEssentialActivityListAdapter.MyViewHolder>() {
    private val mContext: Context? = null
    private val mnNewsLetterModelArrayList: ArrayList<ParentEssentialsModel>? = null
    var dept: String? = null
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var imageIcon: ImageView? = null
        var pdfTitle: TextView? = null
        init {
            imageIcon = view.findViewById<View>(R.id.imageIcon) as ImageView
            pdfTitle = view.findViewById<View>(R.id.pdfTitle) as TextView

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_pdf_adapter_row, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.pdfTitle!!.text = mnNewsLetterModelArrayList!![position].submenu
        if (mnNewsLetterModelArrayList[position].filename!!.endsWith(".pdf")) {
            holder.imageIcon!!.setBackgroundResource(R.drawable.pdfdownloadbutton)
        } else {
            holder.imageIcon!!.setBackgroundResource(R.drawable.webcontentviewbutton)
        }
    }

    override fun getItemCount(): Int {
        return mnNewsLetterModelArrayList!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}