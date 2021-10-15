package com.evosouza.myapplication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.evosouza.myapplication.R
import com.evosouza.myapplication.services.PolyLine
import com.evosouza.myapplication.services.PolyLines
import com.evosouza.myapplication.services.TrackingServices
import com.evosouza.myapplication.ui.viewmodel.MainViewModel
import com.evosouza.myapplication.util.Constants
import com.evosouza.myapplication.util.Constants.ACTION_PAUSE_SERVICE
import com.evosouza.myapplication.util.Constants.ACTION_START_RESUME_SERVICE
import com.evosouza.myapplication.util.Constants.MAP_ZOOM
import com.evosouza.myapplication.util.Constants.POLYLINE_COLOR
import com.evosouza.myapplication.util.Constants.POLYLINE_WIDTH
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathingPoints = mutableListOf<PolyLine>()

    private var map: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.onCreate(savedInstanceState)
        btnToggleRun?.setOnClickListener {
            toggleRun()
        }

        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        subscribeToObserver()
    }

    private fun updateTracking(istracking: Boolean){
        this.isTracking = isTracking
        if(!istracking){
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        }else{
            btnToggleRun.text = "Stop"
            btnFinishRun.visibility = View.GONE
        }
    }

    private fun subscribeToObserver(){
        TrackingServices.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingServices.pathPoints.observe(viewLifecycleOwner, Observer {
            pathingPoints = it
            addAllPolylines()
            moveCameraToUser()
        })
    }

    private fun toggleRun(){
        if(isTracking){
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_RESUME_SERVICE)
        }
    }

    private fun moveCameraToUser(){
        if(pathingPoints.isNotEmpty() && pathingPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathingPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines(){
        for(poli in pathingPoints){
            val polylineOption = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(poli)
            map?.addPolyline(polylineOption)
        }
    }

    private fun addLatestPolylines(){
        if(pathingPoints.isNotEmpty() && pathingPoints.last().size > 1){
            val presLastLatLng = pathingPoints.last()[pathingPoints.last().size -2]
            val lastLatLong = pathingPoints.last().last()
            val polylineOption = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(presLastLatLng)
                .add(lastLatLong)
            map?.addPolyline(polylineOption)
        }
    }

    private fun sendCommandToService(command: String) =
        Intent(requireContext(), TrackingServices::class.java).also {
            it.action = command
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

}