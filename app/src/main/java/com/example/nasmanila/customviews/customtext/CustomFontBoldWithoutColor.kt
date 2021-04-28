package com.example.nasmanila.customviews.customtext

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

class CustomFontBoldWithoutColor : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {
        val type = Typeface.createFromAsset(context.assets, "fonts/SourceSansPro-Bold.otf")
        this.typeface = type
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val type = Typeface.createFromAsset(context.assets, "fonts/SourceSansPro-Bold.otf")
        this.typeface = type
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val type = Typeface.createFromAsset(context.assets, "fonts/SourceSansPro-Bold.otf")
        this.typeface = type
    }
}

