package com.capstone.healthyeat.ui.customview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import androidx.appcompat.widget.AppCompatEditText
import java.util.regex.Pattern

class EmailEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }
    private val emailRegex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$"

    init {
        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                checkEmailFormat()
            }
        }
    }

    private fun checkEmailFormat() {
        val email = text.toString()
        if (!isEmailValid(email)) {
            setError("Invalid Email Format", null)
            setTextColor(Color.RED)
        } else {
            error = null
            setTextColor(Color.LTGRAY)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = Pattern.compile(emailRegex)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}