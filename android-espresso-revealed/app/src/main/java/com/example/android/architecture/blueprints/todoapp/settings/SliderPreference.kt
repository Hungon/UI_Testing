/*
 * Copyright 2012 Jay Weisskopf
 *
 * Licensed under the MIT License (see LICENSE.txt)
 */

package com.example.android.architecture.blueprints.todoapp.settings

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar

import com.example.android.architecture.blueprints.todoapp.R

/**
 * @author Jay Weisskopf
 */
class SliderPreference : DialogPreference {

    protected var mValue: Float = 0.toFloat()
    protected var mSeekBarValue: Int = 0
    protected var mSummaries: Array<CharSequence>? = null

    // clamp to [MINIMUM, MAXIMUM]
    var value: Float
        get() = mValue
        set(value) {
            var value = value
            value = Math.max(MINIMUM, Math.min(value, MAXIMUM))
            if (shouldPersist()) {
                persistFloat(value)
            }
            if (value != mValue) {
                mValue = value
                notifyChanged()
            }
        }

    /**
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup(context, attrs)
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setup(context, attrs)
    }

    private fun setup(context: Context, attrs: AttributeSet) {
        dialogLayoutResource = R.layout.slider_preference_dialog
        val a = context.obtainStyledAttributes(attrs, R.styleable.SliderPreference)
        try {
            setSummaryArray(a.getTextArray(R.styleable.SliderPreference_android_summary))
        } catch (e: Exception) {
            // Do nothing
        }

        a.recycle()
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getFloat(index, MINIMUM)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any) {
        value = if (restoreValue) getPersistedFloat(mValue) else defaultValue as Float
    }

    override fun getSummary(): CharSequence {
        if (mSummaries != null && mSummaries!!.size > 0) {
            var index = (mValue * mSummaries!!.size).toInt()
            index = Math.min(index, mSummaries!!.size - 1)
            return mSummaries!![index]
        } else {
            return super.getSummary()
        }
    }

    private fun setSummaryArray(summaries: Array<CharSequence>) {
        mSummaries = summaries
    }

    override fun setSummary(summary: CharSequence) {
        super.setSummary(summary)
        mSummaries = null
    }

    override fun setSummary(summaryResId: Int) {
        try {
            setSummaryArray(context.resources.getTextArray((R.array.slider_summaries)))
        } catch (e: Exception) {
            super.setSummary(summaryResId)
        }

    }

    override fun onCreateDialogView(): View {
        mSeekBarValue = (mValue * SEEKBAR_RESOLUTION).toInt()
        val view = super.onCreateDialogView()
        val seekbar = view.findViewById<View>(R.id.slider_preference_seekbar) as SeekBar
        seekbar.max = SEEKBAR_RESOLUTION
        seekbar.progress = mSeekBarValue
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onStopTrackingTouch(seekBar: SeekBar) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    this@SliderPreference.mSeekBarValue = progress
                }
            }
        })
        return view
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        val newValue = mSeekBarValue.toFloat() / SEEKBAR_RESOLUTION
        if (positiveResult && callChangeListener(newValue)) {
            value = newValue
        }
        super.onDialogClosed(positiveResult)
    }

    companion object {

        val MAXIMUM = 1.0f
        val MINIMUM = 0.0f
        protected val SEEKBAR_RESOLUTION = 10000
    }
}
