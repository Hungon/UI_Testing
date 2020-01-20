/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.statistics

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView

import com.example.android.architecture.blueprints.todoapp.R

import com.google.common.base.Preconditions.checkNotNull

/**
 * Main UI for the statistics screen.
 */
class StatisticsFragment : Fragment(), StatisticsContract.View {

    private var mStatisticsTV: TextView? = null
    private var seekBarTextView: TextView? = null
    private var seekBar: SeekBar? = null
    private var progressText: String? = null

    private var mPresenter: StatisticsContract.Presenter? = null

    override val isActive: Boolean
        get() = isAdded

    override fun setPresenter(presenter: StatisticsContract.Presenter) {
        mPresenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.statistics_frag, container, false)
        mStatisticsTV = root.findViewById<View>(R.id.statistics) as TextView
        seekBarTextView = root.findViewById<View>(R.id.seekBarTextView) as TextView
        seekBar = root.findViewById<View>(R.id.simpleSeekBar) as SeekBar
        progressText = getString(R.string.statisticsProgress)
        seekBar!!.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}

                    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                                   fromUser: Boolean) {
                        seekBarTextView!!.setText(String.format(progressText!! + " %d", seekBar.progress))

                    }
                }
        )

        seekBar!!.max = 50
        seekBar!!.min = 0
        seekBar!!.progress = 0


        seekBarTextView!!.setText(String.format(progressText!! + " %d", 0))
        showDialogWithDelay()
        return root
    }

    override fun onResume() {
        super.onResume()
        mPresenter!!.start()
    }

    override fun setProgressIndicator(active: Boolean) {
        if (active) {
            mStatisticsTV!!.text = getString(R.string.loading)
        } else {
            mStatisticsTV!!.text = ""
        }
    }

    override fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int) {
        if (numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0) {
            mStatisticsTV!!.text = resources.getString(R.string.statistics_no_tasks)
        } else {
            val displayString = (resources.getString(R.string.statistics_active_tasks) + " "
                    + numberOfIncompleteTasks + "\n" + resources.getString(
                    R.string.statistics_completed_tasks) + " " + numberOfCompletedTasks)
            mStatisticsTV!!.text = displayString
        }
    }

    override fun showLoadingStatisticsError() {
        mStatisticsTV!!.text = resources.getString(R.string.statistics_error)
    }

    private fun showDialogWithDelay() {
        val builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(activity)
        } else {
            builder = AlertDialog.Builder(activity)
        }
        builder.setTitle("Alert dialog")
                .setMessage("Dismiss me silently.")
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    // do nothing - test sample
                }
                .setNegativeButton(android.R.string.no) { dialog, which ->
                    // do nothing - test sample
                }
                .show()
    }

    companion object {

        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}
