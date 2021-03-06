package com.mobatia.nasmanila.customviews.customtext

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.mobatia.nasmanila.R

class CustomFontDJ5TextWhite : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {
        val type = Typeface.createFromAsset(context.assets, "fonts/DJ5CTRIAL.otf")
        this.typeface = type
        this.setTextColor(context.resources.getColor(R.color.white))
        this.textSize = 20f
    }
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val type = Typeface.createFromAsset(context.assets, "fonts/DJ5CTRIAL.otf")
        this.typeface = type
        this.setTextColor(context.resources.getColor(R.color.white))
        this.textSize = 20f
    }
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        val type = Typeface.createFromAsset(context.assets, "fonts/DJ5CTRIAL.otf")
        this.typeface = type
        this.setTextColor(context.resources.getColor(R.color.white))
        this.textSize = 20f
    }
}