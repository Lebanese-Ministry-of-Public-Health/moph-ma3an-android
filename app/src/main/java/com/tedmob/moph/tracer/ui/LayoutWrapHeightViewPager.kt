package com.tedmob.moph.tracer.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.tedmob.moph.tracer.R
import me.relex.circleindicator.CircleIndicator

class LayoutWrapHeightViewPager : LinearLayout {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        orientation = VERTICAL

        View.inflate(context, R.layout.layout_view_pager_picker, this)
        View.inflate(context, R.layout.layout_view_pager_wrap_height_pager, this)

        initLanguagePicker()
    }


    private var layouts: Array<Int> = emptyArray()

    private val adapter: PagerAdapter
        get() = object : PagerAdapter() {
            override fun getCount(): Int = layouts.size

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val v = LayoutInflater.from(container.context).inflate(layouts[position], container, false)
                onPageCreated(v, position)
                container.addView(v)
                return v
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean = `object` == view

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                (`object` as? View)?.let { container.removeView(it) }
            }
        }

    private fun initLanguagePicker() {
        val options = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(context, R.layout.layout_view_pager_picker_option, options)
            .apply { setDropDownViewResource(R.layout.layout_view_pager_picker_option_dropdown) }

        findViewById<Spinner>(R.id.languagesSpinner)?.let {
            it.adapter = adapter
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    findViewById<ViewPager>(R.id.pager)?.currentItem = position
                    onLanguageSelected(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }


    class WrapHeightViewPager : NonSwipeableViewPager {
        constructor(context: Context) : super(context)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var heightMeasureSpec = heightMeasureSpec
            val mode = MeasureSpec.getMode(heightMeasureSpec)

            if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
                // super has to be called in the beginning so the child views can be initialized.
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                var height = 0
                for (i in 0 until childCount) {
                    val child = getChildAt(i)
                    child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                    val h = child.measuredHeight
                    if (h > height) height = h
                }
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }


    fun setPages(@LayoutRes arabicPage: Int, @LayoutRes englishPage: Int) {
        //fixme Assuming this will be called once for now, and only for 2 languages in that order
        this.layouts = arrayOf(arabicPage, englishPage)

        val viewPager = findViewById<ViewPager>(R.id.pager)
        viewPager?.adapter = adapter
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                findViewById<Spinner>(R.id.languagesSpinner)?.setSelection(position, false)
                onLanguageSelected(position)
            }
        })
        findViewById<CircleIndicator>(R.id.indicator)?.setViewPager(viewPager)
    }

    fun setPickerSelection(position: Int) {
        findViewById<ViewPager>(R.id.pager)?.currentItem = position
        findViewById<Spinner>(R.id.languagesSpinner)?.setSelection(position)
    }

    fun showPicker(show: Boolean) {
        findViewById<View>(R.id.pickerLayout)?.isVisible = show
    }

    fun showPageIndicator(show: Boolean) {
        findViewById<View>(R.id.indicator)?.isVisible = show
    }

    var onLanguageSelected: (position: Int) -> Unit = {}

    var onPageCreated: (view: View, position: Int) -> Unit = { _, _ -> }
}