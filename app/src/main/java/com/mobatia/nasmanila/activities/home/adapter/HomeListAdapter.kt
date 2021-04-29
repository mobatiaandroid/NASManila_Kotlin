package com.mobatia.nasmanila.activities.home.adapter

import android.content.Context
import android.content.res.TypedArray
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class HomeListAdapter: BaseAdapter {
    private val mContext: Context? = null
    private lateinit var  mListItemArray: Array<String>
    private val mListImgArray: TypedArray? = null
    private val customLayout = 0
    private val mDisplayListImage = false
    constructor(context: Context, listItemArray: Array<String>, listImgArray: TypedArray, customLayout: Int, displayListImage: Boolean)
    override fun getCount(): Int {
        return mListItemArray.size
    }

    override fun getItem(position: Int): Any {
        return mListItemArray[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        return convertView!!
    }
}