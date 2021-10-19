package com.evosouza.myapplication.ui.customView

import android.content.Context
import com.evosouza.myapplication.db.Run
import com.evosouza.myapplication.util.TrackingUtility
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerVIew(
    val runs: List<Run>,
    c : Context,
    layoutid: Int
): MarkerView(c, layoutid) {


    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e == null){
            return
        }

        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val dateFormate = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        tvDate.text = dateFormate.format(calendar.time)

        val avgSpeed = "${run.avgSpeedKMH}km/h"
        tvAvgSpeed.text = avgSpeed

        val distanceInKm = "${run.distanceInMeters/1000f}km"
        tvDistance.text = distanceInKm

        tvDuration.text = TrackingUtility.getFormattedStopWatchTime(run.timeMillis)

        val caloriesBurned = "${run.caloriesBurned}kcal"
        tvCaloriesBurned.text = caloriesBurned

    }

}