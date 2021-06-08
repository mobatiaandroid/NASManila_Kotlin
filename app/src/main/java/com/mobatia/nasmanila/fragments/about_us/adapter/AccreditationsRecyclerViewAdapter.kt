package com.mobatia.nasmanila.fragments.about_us.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.fragments.about_us.model.AboutUsModel

class AccreditationsRecyclerViewAdapter(context: Context,aboutUsModelArrayList: ArrayList<AboutUsModel>) : BaseAdapter() {
    private val mContext: Context? = context
    private val mAboutusLists: ArrayList<String>? = null
    private val aboutusModelArrayList: ArrayList<AboutUsModel>? = aboutUsModelArrayList
    private var view: View? = null
    private var mTitleTxt: TextView? = null
    private val mImageView: ImageView? = null
    private val mTitle: String? = null
    private val mTabId: String? = null
    override fun getCount(): Int {

        return aboutusModelArrayList!!.size
    }

    override fun getItem(position: Int): Any {


        return aboutusModelArrayList!![position]
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            val inflate = LayoutInflater.from(mContext)
            view = inflate.inflate(R.layout.custom_aboutus_list_adapter, null)
        } else {
            view = convertView
        }

            mTitleTxt = view!!.findViewById<View>(R.id.listTxtTitle) as TextView
            mTitleTxt!!.setText(aboutusModelArrayList!![position].itemTitle)

        return view!!
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}