package com.example.nasmanila.customviews.custombutton

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

class CustomButtonFontSansButton : androidx.appcompat.widget.AppCompatButton {
    var FONT_NAME: Typeface? = null
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        if (FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.assets, "fonts/SourceSansPro-Regular.otf")
        this.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        this.typeface = FONT_NAME
    }
}