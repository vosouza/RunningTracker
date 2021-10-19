package com.evosouza.myapplication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.evosouza.myapplication.R
import com.evosouza.myapplication.db.Run
import com.evosouza.myapplication.services.PolyLine
import com.evosouza.myapplication.services.TrackingServices
import com.evosouza.myapplication.ui.customView.CancelTrackingDialog
import com.evosouza.myapplication.ui.viewmodel.MainViewModel
import com.evosouza.myapplication.util.Constants.ACTION_PAUSE_SERVICE
import com.evosouza.myapplication.util.Constants.ACTION_START_RESUME_SERVICE
import com.evosouza.myapplication.util.Constants.ACTION_STOP_SERVICE
import com.evosouza.myapplication.util.Constants.MAP_ZOOM
import com.evosouza.myapplication.util.Constants.POLYLINE_COLOR
import com.evosouza.myapplication.util.Constants.POLYLINE_WIDTH
import com.evosouza.myapplication.util.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round

const val CANCEL_TRACKING_DIALOG = "CANCEL_TRACKING_DIALOG"

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathingPoints = mutableListOf<PolyLine>()

    private var map: GoogleMap? = null
    private var _isFirstRun : Boolean = true

    private var currentTimeMillis = 0L

    private var menu: Menu? = null

    @set:Inject
    var weigtht = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.onCreate(savedInstanceState)
        btnToggleRun?.setOnClickListener {
            toggleRun()
        }
        btnFinishRun?.setOnClickListener{
            zoomToSeeWholeTrack()
            endRunAndSaveToDB()
        }

        if(savedInstanceState != null){
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG
            ) as CancelTrackingDialog?
            cancelTrackingDialog?.setYesListener { stopRun() }
        }

        mapView.getMapAsync {
            map = it
            if(!_isFirstRun){
                addAllPolylines()
            }
        }
        subscribeToObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(currentTimeMillis>0){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.miCancelTraking ->{
               showCancelDialog()
           }
       }
       return super.onOptionsItemSelected(item)
    }

    private fun showCancelDialog(){
        CancelTrackingDialog().apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG)
    }

    private fun stopRun() {
        tvTimer.text = "00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun updateTracking(track: Boolean){
        this.isTracking = track
        if(!track && currentTimeMillis > 0L){
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        }else if(isTracking){
            menu?.getItem(0)?.isVisible = true
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
        TrackingServices.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeMillis, true)
            tvTimer.text = formattedTime
        })
        TrackingServices._isFirstRun.observe(viewLifecycleOwner, Observer {
            _isFirstRun = it
        })
    }

    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
            isTracking = !isTracking
        }else{
            sendCommandToService(ACTION_START_RESUME_SERVICE)
            isTracking = !isTracking
        }
    }

    private fun zoomToSeeWholeTrack(){
        val bounds = LatLngBounds.Builder()
        for(polylines in pathingPoints){
            for (pos in polylines){
                bounds.include(pos )
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
    }


    private fun endRunAndSaveToDB(){
        map?.snapshot {
            var distanceInMetter = 0
            for (poly in pathingPoints){
                distanceInMetter += TrackingUtility.calculatePolyLineLength(poly).toInt()
            }
            val avgSpeed = round((distanceInMetter/1000f) /(currentTimeMillis/1000f/60/60) *10) / 10f
            val dateTime = Calendar.getInstance().timeInMillis
            val calories = ((distanceInMetter/1000f) * weigtht).toInt()
            val run = Run(it, dateTime,avgSpeed,distanceInMetter,currentTimeMillis,calories)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run saved",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
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