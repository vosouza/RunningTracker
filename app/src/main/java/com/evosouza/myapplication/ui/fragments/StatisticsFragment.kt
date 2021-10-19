package com.evosouza.myapplication.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.evosouza.myapplication.R
import com.evosouza.myapplication.ui.customView.CustomMarkerVIew
import com.evosouza.myapplication.ui.viewmodel.MainViewModel
import com.evosouza.myapplication.ui.viewmodel.StatisticsViewModel
import com.evosouza.myapplication.util.TrackingUtility
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObserver()
        barChartSetup()

    }

    private fun barChartSetup(){
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            setDrawGridLines(false)
        }

        barChart.axisLeft.apply {
            axisLineColor = Color.BLACK
            setDrawGridLines(false)
        }
        barChart.axisRight.apply {
            axisLineColor = Color.BLACK
            setDrawGridLines(false)
        }
        barChart.apply {
            description.text = "Avg Speed over time"
            legend.isEnabled = false
        }
    }

    private fun subscribeToObserver(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner,{
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                tvTotalTime.text = totalTimeRun

            }
        })

        viewModel.totalDistance.observe(viewLifecycleOwner,{
            it?.let {
                val km = it/ 1000f
                val totalDistance = round(km * 10f) / 10f
                val totalDistanceString = "${totalDistance}km"
                tvTotalDistance.text = totalDistanceString
            }
        })
        viewModel.totalCalories.observe(viewLifecycleOwner,{
            it?.let {
                val caloriesString = "${it}kcal"
                tvTotalCalories.text = caloriesString
            }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner,{
            it?.let {
                val avgSpeedString = "${round(it * 10f)/10f}km/h"
                tvAverageSpeed.text = avgSpeedString
            }
        })

        viewModel.runsSortedByDate.observe(viewLifecycleOwner,{
            it?.let {
                val allAvgSpeed = it.indices.map { index ->
                    BarEntry(index.toFloat(), it[index].avgSpeedKMH)
                }
                val barDataSet = BarDataSet(allAvgSpeed,"Avg speed over time").apply {
                    valueTextColor = Color.BLACK
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }
                barChart.data = BarData(barDataSet)
                barChart.marker = CustomMarkerVIew(it.reversed(), requireContext(), R.layout.marker_view)
                barChart.invalidate()
            }
        })
    }



}