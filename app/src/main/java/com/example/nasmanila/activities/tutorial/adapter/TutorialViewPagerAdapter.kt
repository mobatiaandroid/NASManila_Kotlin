package com.example.nasmanila.activities.tutorial.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.nasmanila.R

class TutorialViewPagerAdapter(var context: Context, var imagesArrayList: ArrayList<Int>): PagerAdapter() {
    lateinit var tContext: Context
//    private lateinit var tImagesArrayList: ArrayList<Int>
    lateinit var inflater: LayoutInflater

    override fun getCount(): Int {
        return imagesArrayList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = LayoutInflater.from(context)
        var pageView: View = inflater.inflate(R.layout.layout_imagepager_adapter, container, false)
        val imageView = pageView.findViewById<View>(R.id.adImg) as ImageView
//        if (position < imagesArrayList.size) {
////            imageView.setBackgroundResource(imagesArrayList.get(position))
//            Glide.with(context).load(imagesArrayList[position]).into(imageView)
//            (container as ViewPager).addView(pageView, 0)
//        }
//        return pageView
        imageView.setImageResource(imagesArrayList[position])
        container.addView(pageView,0)
        return pageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }
}