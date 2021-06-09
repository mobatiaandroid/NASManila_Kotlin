package com.mobatia.nasmanila.manager

import com.mobatia.nasmanila.R
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import java.io.Serializable

class HeaderManager: Serializable {
    private var context: Activity? = null

    /** The inflator.  */
    private var inflator: LayoutInflater? = null

    /** The header view.  */
    var headerView: View? = null

    /** The m heading1.  */
    private var mHeading1: TextView? = null

    /** The relative params.  */
    private var relativeParams: RelativeLayout.LayoutParams? = null

    /** The heading1.  */
    private var heading1: String? = null

    /** The is cancel.  */
    private val isCancel = false

    private var edtText: EditText? = null
    /* FOR HOME SCREEN */
    /* FOR HOME SCREEN */
    /** The is home.  */
    private var isHome = false

    /** The m right text.  */
    private var mLeftText: TextView? = null
    /** The m right text.  */
    private  var mRightText:TextView? = null

    /** The m left.  */
    private var mLeftImage: ImageView? = null
    /** The m left.  */
    private  var mRightImage:android.widget.ImageView? = null
    /** The m left.  */
    private  var mRight:android.widget.ImageView? = null
    /** The m left.  */
    private  var mLeft:android.widget.ImageView? = null
    /** The m left.  */
    private  var logoClickImgView:android.widget.ImageView? = null
    var btn_info: ImageView? = null

    /**
     * Instantiates a new headermanager.
     *
     * @param context
     * the context
     * @param heading1
     * the heading1
     */
    constructor(context: Activity?, heading1: String?) {
        setContext(context)
        inflator = LayoutInflater.from(context)
        this.heading1 = heading1
    }

    /**
     * Instantiates a new headermanager.
     *
     * @param home
     * the home
     * @param context
     * the context
     */
    fun HeaderManager(home: Boolean, context: Activity?) {
        setContext(context)
        inflator = LayoutInflater.from(context)
        isHome = home
    }

    /**
     * Gets the left text.
     *
     * @return the left text
     */
    fun getLeftText(): TextView? {
        return mLeftText
    }

    /**
     * Sets the left text.
     *
     * @param mLeftText
     * the new left text
     */
    fun setLeftText(mLeftText: TextView?) {
        this.mLeftText = mLeftText
    }

    /**
     * Gets the right text.
     *
     * @return the right text
     */
    fun getRightText(): TextView? {
        return mRightText
    }

    /**
     * Sets the right text.
     *
     * @param mLeftText
     * the new right text
     */
    fun setRightText(mLeftText: TextView) {
        this.mRightText = mLeftText
    }

    // image view
    // image view
    /**
     * Gets the left image.
     *
     * @return the left image
     */
    fun getLeftImage(): ImageView? {
        return mLeftImage
    }

    /**
     * Sets the left image.
     *
     * @param mLeftImage
     * the new left image
     */
    fun setLeftImage(mLeftImage: ImageView?) {
        this.mLeftImage = mLeftImage
    }

    /**
     * Gets the right image.
     *
     * @return the right image
     */
    fun getRightImage(): ImageView? {
        return mLeftImage
    }

    fun getRightInfoImage(): ImageView? {
        return btn_info
    }

    /**
     * Sets the right image.
     *
     * @param mLeftImage
     * the new right image
     */
    fun setRightImage(mLeftImage: ImageView) {
        this.mRightImage = mLeftImage
    }

    /**
     * Sets the visible.
     *
     * @param v
     * the new visible
     */
    fun setVisible(v: View?) {
        v!!.visibility = View.VISIBLE
    }

    /**
     * Sets the invisible.
     */
    fun setInvisible() {
        headerView!!.visibility = View.INVISIBLE
    }

    /**
     * Sets the invisible.
     *
     * @param v
     * the new invisible
     */
    fun setInvisible(v: View) {
        v.visibility = View.INVISIBLE
    }

    /**
     * Gets the header.
     *
     * @param headerHolder
     * the header holder
     * @return the header
     */
    fun getHeader(headerHolder: LinearLayout, type: Int): Int {
        initializeUI(type)
        relativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        relativeParams!!.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        headerHolder.addView(headerView, relativeParams)
        return headerView!!.id
    }

    /**
     * Gets the header.
     *
     * @param headerHolder
     * the header holder
     * @return the header
     */
    fun getHeader(
        headerHolder: RelativeLayout, getHeading: Boolean,
        type: Int
    ): Int {
        initializeUI(getHeading, type)
        relativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        relativeParams!!.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        headerHolder.addView(headerView, relativeParams)
        return headerView!!.id
    }

    /**
     * Initialize ui.
     */
    private fun initializeUI(type: Int) {
        inflator = LayoutInflater.from(getContext())
        headerView = inflator!!.inflate(R.layout.common_header_single, null)
        val logoHeader = headerView!!
            .findViewById<View>(R.id.relative_logo_header) as RelativeLayout
        btn_info = headerView!!
            .findViewById<View>(R.id.btn_info) as ImageView
        if (type == 0) {
            logoHeader.setBackgroundResource(R.drawable.titlebar) // two
            btn_info!!.visibility = View.GONE
            // buttons
        } else if (type == 1) {
            logoHeader.setBackgroundResource(R.drawable.titlebar) // left
            btn_info!!.visibility = View.GONE
            // button
        } else if (type == 3) {
            logoHeader.setBackgroundResource(R.drawable.titlebar) // left
            btn_info!!.visibility = View.VISIBLE
            // button
        }
        mHeading1 = headerView!!.findViewById<View>(R.id.heading) as TextView
        mHeading1!!.text = heading1
        mHeading1!!.setBackgroundColor(context!!.resources.getColor(R.color.split_bg))
        mRight = headerView!!.findViewById<View>(R.id.btn_right) as ImageView
        mLeft = headerView!!.findViewById<View>(R.id.btn_left) as ImageView
        logoClickImgView = headerView!!.findViewById<View>(R.id.logoClickImgView) as ImageView
    }

    /**
     * Initialize ui.
     */
    private fun initializeUI(getHeading: Boolean, type: Int) {
        inflator = LayoutInflater.from(getContext())
        headerView = inflator!!.inflate(R.layout.common_header_single, null)
        val logoHeader = headerView!!
            .findViewById<View>(R.id.relative_logo_header) as RelativeLayout
        btn_info = headerView!!
            .findViewById<View>(R.id.btn_info) as ImageView
        if (type == 0) {
            logoHeader.setBackgroundResource(R.drawable.titlebar)
            btn_info!!.visibility = View.GONE
        } else if (type == 1) {
            logoHeader.setBackgroundResource(R.drawable.titlebar)
            mHeading1 = headerView!!.findViewById<View>(R.id.heading) as TextView
            mHeading1!!.visibility = View.GONE
            btn_info!!.visibility = View.GONE
        } else if (type == 3) {
            logoHeader.setBackgroundResource(R.drawable.titlebar) // left
            btn_info!!.visibility = View.VISIBLE
            // button
        } /*
		 * else if(type==2) {
		 * logoHeader.setBackgroundResource(R.drawable.titlebars); }
		 */


//		edtText = (EditText) headerView.findViewById(R.id.edtTxt_Header);
        mHeading1 = headerView!!.findViewById<View>(R.id.heading) as TextView
        mHeading1!!.text = heading1
        mHeading1!!.setBackgroundColor(context!!.resources.getColor(R.color.split_bg))
        mRight = headerView!!.findViewById<View>(R.id.btn_right) as ImageView
        mLeft = headerView!!.findViewById<View>(R.id.btn_left) as ImageView
        logoClickImgView = headerView!!.findViewById<View>(R.id.logoClickImgView) as ImageView
    }

    /**
     * Sets the title bar.
     *
     * @param titleBar
     * the new title bar
     */
    fun setTitleBar(titleBar: Int) {
        headerView!!.setBackgroundResource(titleBar)
    }

    fun setTitle(title: String?) {
        mHeading1!!.text = title
    }

    /**
     * Gets the left button.
     *
     * @return the left button
     */
    fun getLeftButton(): ImageView? {
        return mLeft
    }

    fun getLogoButton(): ImageView? {
        return logoClickImgView
    }

    /**
     * Sets the left button.
     *
     * @param right
     * the new left button
     */
    fun setLeftButton(right: ImageView) {
        this.mLeft = right
    }

    /**
     * Gets the right button.
     *
     * @return the right button
     */
    fun getRightButton(): ImageView? {
        return mRight
    }

    fun getEditText(): EditText? {
        mHeading1!!.visibility = View.GONE
        edtText!!.visibility = View.VISIBLE
        return edtText
    }

    /**
     * Sets the edits the text.
     *
     * @param editText
     * the new edits the text
     */
    fun setEditText(editText: EditText?) {
        edtText = editText
        setVisible(edtText)
    }

    /**
     * Sets the right button.
     *
     * @param right
     * the new right button
     */
    fun setRightButton(right: ImageView) {
        this.mRight = right
    }

    /**
     * Sets the button right selector.
     *
     * @param normalStateResID
     * the normal state res id
     * @param pressedStateResID
     * the pressed state res id
     */
    fun setButtonRightSelector(
        normalStateResID: Int,
        pressedStateResID: Int
    ) {
        if (normalStateResID == R.drawable.settings) {
            mRight!!.setBackgroundResource(R.drawable.settings)
        }
        //		else if (normalStateResID==R.drawable.help)
//		{
//			mRight.setBackgroundResource(R.drawable.help);
//		}
//		else if (normalStateResID==R.drawable.share)
//		{
//			mRight.setBackgroundResource(R.drawable.share);
//		}
//		else
//		{
//			mRight.setBackgroundResource(R.drawable.settings);
//
//		}
        mRight!!.setImageDrawable(
            getButtonDrawableByScreenCathegory(
                normalStateResID, pressedStateResID
            )
        )
        setVisible(mRight)
    }

    /**
     * Sets the button left selector.
     *
     * @param normalStateResID
     * the normal state res id
     * @param pressedStateResID
     * the pressed state res id
     */
    fun setButtonLeftSelector(
        normalStateResID: Int,
        pressedStateResID: Int
    ) {
        mLeft!!.setImageDrawable(
            getButtonDrawableByScreenCathegory(
                normalStateResID, pressedStateResID
            )
        )
        setVisible(mLeft)
    }

    /**
     * Gets the button drawable by screen cathegory.
     *
     * @param normalStateResID
     * the normal state res id
     * @param pressedStateResID
     * the pressed state res id
     * @return the button drawable by screen cathegory
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getButtonDrawableByScreenCathegory(
        normalStateResID: Int,
        pressedStateResID: Int
    ): Drawable? {
        val stateNormal = context!!.resources
            .getDrawable(normalStateResID).mutate()
        val statePressed = context!!.resources
            .getDrawable(pressedStateResID).mutate()
        val drawable = StateListDrawable()
        drawable.addState(
            intArrayOf(R.attr.state_pressed),
            statePressed
        )
        drawable.addState(
            intArrayOf(R.attr.state_enabled),
            stateNormal
        )
        return drawable
    }

    /**
     * Sets the context.
     *
     * @param context
     * the new context
     */
    private fun setContext(context: Activity?) {
        this.context = context
    }

    /**
     * Gets the context.
     *
     * @return the context
     */
    private fun getContext(): Activity? {
        return context
    }
}