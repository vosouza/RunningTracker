package com.evosouza.myapplication.util

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.evosouza.myapplication.services.PolyLine
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object TrackingUtility {

    fun hasLocationPermission(context: Context) =
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }else{
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String{
        var milli = ms
        val hours = TimeUnit.MICROSECONDS.toHours(milli)
        milli -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milli)
        milli -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milli)

        if(!includeMillis){
            return "${if(hours > 10) "0" else ""}$hours:"+
                    "${if(minutes > 10) "0" else ""}$minutes:"+
                    "${if(seconds > 10) "0" else ""}$seconds"
        }

        milli -= TimeUnit.SECONDS.toMillis(seconds)
        milli /= 10
        return "${if(hours > 10) "0" else ""}$hours:"+
                "${if(minutes > 10) "0" else ""}$minutes:"+
                "${if(seconds > 10) "0" else ""}$seconds"+
                ":${if(milli > 10) "0" else ""}$milli"
    }

    fun calculatePolyLineLength(polyLine: PolyLine): Float{
        var distance = 0f
        for(i in 0..polyLine.size - 2){
            val pos1 = polyLine[i]
            val pos2 = polyLine[i + 1]

            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }
}