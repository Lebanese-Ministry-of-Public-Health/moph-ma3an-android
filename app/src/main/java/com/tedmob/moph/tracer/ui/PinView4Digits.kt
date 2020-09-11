package com.tedmob.moph.tracer.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.tedmob.moph.tracer.R
import kotlinx.android.synthetic.main.layout_pin_view_4_digits.view.*

class PinView4Digits
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    LinearLayout(context, attrs, defStyle), TextWatcher, TextView.OnEditorActionListener {

    var onCodeChanged: ((v: PinView4Digits, code: String, complete: Boolean) -> Unit)? = null

    var pin: String
        get() = pinViewEditText.text.toString()
        set(pin) = pinViewEditText.setText(pin)

    var onTextChanged: ((s: CharSequence, start: Int, before: Int, count: Int) -> Unit)? = null
    var onEditorActionListener: ((v: TextView?, actionId: Int, event: KeyEvent?) -> Boolean)? = null

    private val digits = mutableListOf<TextView>()
    private var digitNbr = 1


    init {
        isClickable = true

        View.inflate(getContext(), R.layout.layout_pin_view_4_digits, this)

        digits.add(pinDigit1)
        digits.add(pinDigit2)
        digits.add(pinDigit3)
        digits.add(pinDigit4)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.PinView, defStyle, 0)

            val d = a.getDrawable(R.styleable.PinView_digitBackground)
            d?.let { setDigitBackground(it) }

            setDigitTextColor(a.getColor(R.styleable.PinView_digitTextColor, Color.BLACK))

            val textSize = a.getDimension(R.styleable.PinView_digitTextSize, -1f)

            digitNbr = a.getInteger(R.styleable.PinView_digitNbr, 1)
            if (textSize != -1f) {
                setDigitTextSize(textSize)
            }

            val isAlphaNumerical = a.getBoolean(R.styleable.PinView_isAlphaNumerical, false)
            if (isAlphaNumerical) {
                pinViewEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            }

            pinViewEditText.filters = arrayOf(InputFilter.LengthFilter(digits.size * digitNbr))
            pinViewEditText.addTextChangedListener(this)
            pinViewEditText.setOnEditorActionListener(this)

            a.recycle()
        }

        setOnClickListener {
            pinViewEditText.requestFocus()
            showKeyboard(pinViewEditText)
        }
    }

    private fun showKeyboard(v: View) {
        val manager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.showSoftInput(v, 0)
    }

    fun setDigitBackground(resId: Int) {
        val d = ContextCompat.getDrawable(context, resId)
        d?.let { setDigitBackground(it) }
    }

    fun setDigitBackground(d: Drawable) {
        digits.forEach { it.background = d }
    }

    fun setDigitTextColor(color: Int) {
        digits.forEach { it.setTextColor(color) }
    }

    fun setDigitTextSize(size: Float) {
        digits.forEach { it.setTextSize(TypedValue.COMPLEX_UNIT_PX, size) }
    }


    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val number = StringBuilder(s.toString())

        onCodeChanged?.let {
            val complete = number.length == (digits.size * digitNbr)
            it.invoke(this, number.toString(), complete)
        }

        for (i in number.length until digits.size) {
            number.append(" ")
        }

        digits.forEach { it.text = "" }
        var digitIndex = 0
        for (i in number.indices step digitNbr) {
            val endTxtIndex = i + digitNbr
            val maskedChar =
                if (endTxtIndex < number.length)
                    number.substring(i, endTxtIndex)
                else
                    number.substring(i, number.length)

            digits[digitIndex].text = maskedChar
            digitIndex += 1
        }

        onTextChanged?.invoke(s, start, before, count)
    }

    override fun afterTextChanged(s: Editable) {
    }


    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        return onEditorActionListener?.invoke(v, actionId, event) ?: false
    }
}
