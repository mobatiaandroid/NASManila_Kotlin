package com.mobatia.nasmanila.activities.tutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.mobatia.nasmanila.R
import com.mobatia.nasmanila.activities.login.LoginActivity
import com.mobatia.nasmanila.activities.tutorial.adapter.TutorialViewPagerAdapter
import com.mobatia.nasmanila.constants.IntentPassValueConstants.TYPE

class TutorialActivity : AppCompatActivity() {
//    lateinit var imageCircle: Array<ImageView?>
    private var imageCircle: Array<ImageView?>? = null
    lateinit var linearLayout: LinearLayout
    lateinit var tutorialViewPager: ViewPager
    lateinit var context: Context
    lateinit var imageSkip: ImageView
    lateinit var tutorialViewPagerAdapter: TutorialViewPagerAdapter
    var dataType = 0

    var tPhotoList = ArrayList(
            listOf(
                    R.drawable.tut_1,
                    R.drawable.tut_2,
                    R.drawable.tut_4,
                    R.drawable.tut_6
            )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial_acitivity)
        supportActionBar?.hide()              /** Remove and add theme to manifest **/
        context = this
        val bundle = intent.extras
        if (bundle != null) {
            dataType = bundle.getInt(TYPE, 0)
        }
        initialiseViewPagerUI()
    }


    private fun initialiseViewPagerUI() {
        tutorialViewPager = findViewById(R.id.tutorialViewPager)
        imageSkip = findViewById(R.id.imageSkip)
        tutorialViewPagerAdapter = TutorialViewPagerAdapter(context, tPhotoList)
        tutorialViewPager.currentItem = 0
        imageCircle = arrayOfNulls<ImageView>(tPhotoList.size)
        tutorialViewPager.adapter = tutorialViewPagerAdapter
        linearLayout = findViewById(R.id.linear)
        imageSkip.setOnClickListener {
            if (dataType == 0)
                finish()
            else {
                val loginIntent = Intent(context, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
            }
        }
        tutorialViewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
//                for (i in tPhotoList.indices) {
//                    imageCircle!![i]!!.setBackgroundDrawable(resources
//                            .getDrawable(R.drawable.blackround))
//                }
                if (position < tPhotoList.size) {
//                    imageCircle!![position]!!.setBackgroundDrawable(resources
//                            .getDrawable(R.drawable.redround))
                    linearLayout.removeAllViews()
//                    addShowCountView(position)
                } else {
                    linearLayout.removeAllViews()
                    if (dataType == 0) {
                        finish()
                    } else {
                        val loginIntent = Intent(context,
                                LoginActivity::class.java)
                        startActivity(loginIntent)
                        finish()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }


        })
    }

//    private fun initialiseViewPagerUI() {
//        tutorialViewPager = findViewById<View>(R.id.tutorialViewPager) as ViewPager
//        imageSkip = findViewById<View>(R.id.imageSkip) as ImageView
//        linearLayout = findViewById<View>(R.id.linear) as LinearLayout
//        imageCircle = arrayOfNulls (tPhotoList.size)
//        tutorialViewPagerAdapter = TutorialViewPagerAdapter(context, tPhotoList)
//        tutorialViewPager.currentItem = 0
//        tutorialViewPager.adapter = tutorialViewPagerAdapter
//
////        addShowCountView(0)
//        imageSkip.setOnClickListener {
//            if (dataType == 0)
//                finish()
//            else {
//                val loginIntent = Intent(context, LoginActivity::class.java)
//                startActivity(loginIntent)
//                finish()
//            }
//        }
//        tutorialViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//            }
//
//            override fun onPageSelected(position: Int) {
//                var i = 0
//                for (i in 0..tPhotoList.size)
//                    imageCircle[i]?.background = resources.getDrawable(R.drawable.blackround)
//
//                if (position < tPhotoList.size) {
//                    imageCircle[position]?.background = resources.getDrawable(R.drawable.redround)
//                    linearLayout.removeAllViews()
//                    addShowCountView(position)
//                } else {
//                    linearLayout.removeAllViews()
//                    if (dataType == 0)
//                        finish()
//                    else {
//                        val loginIntent = Intent(context, LoginActivity::class.java)
//                        startActivity(loginIntent)
//                        finish()
//                    }
//                }
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//            }
//        })
//        (tutorialViewPager.adapter as TutorialViewPagerAdapter).notifyDataSetChanged()
//    }

    private fun addShowCountView(count: Int) {
        var size = tPhotoList.size
        var i = 0
        for (i in 0..size) {
            imageCircle?.set(i, ImageView(this))
            val layoutParams = LinearLayout.LayoutParams(
                    resources.getDimension(R.dimen.home_circle_width).toInt(),
                    resources.getDimension(R.dimen.home_circle_height).toInt()
            )
            imageCircle?.get(i)?.layoutParams = layoutParams
            if (i == count) {
                imageCircle?.get(i)?.setBackgroundResource(R.drawable.redround)
            } else {
                imageCircle?.get(i)?.setBackgroundResource(R.drawable.blackround)
            }
            linearLayout.addView(imageCircle?.get(i))
        }
    }
}
