package com.evosouza.myapplication.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.evosouza.myapplication.R
import com.evosouza.myapplication.ui.MainActivity
import com.evosouza.myapplication.util.Constants.ACTION_PAUSE_SERVICE
import com.evosouza.myapplication.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.evosouza.myapplication.util.Constants.ACTION_START_RESUME_SERVICE
import com.evosouza.myapplication.util.Constants.ACTION_STOP_SERVICE
import com.evosouza.myapplication.util.Constants.NOTIFICATION_CHANNEL_ID
import com.evosouza.myapplication.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.evosouza.myapplication.util.Constants.NOTIFICATION_ID
import timber.log.Timber

class TrackingServices: LifecycleService() {

    var isFristRun = true

    //quanto um intent chama com uma ação
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when(it.action){
                ACTION_START_RESUME_SERVICE ->{
                    if(isFristRun){
                        startForegroundService()
                        isFristRun = false
                    }
                }
                ACTION_PAUSE_SERVICE ->{
                    Timber.d("pause service")
                }
                ACTION_STOP_SERVICE ->{
                    Timber.d("stop service")
                }
                else -> Timber.d("nothing")
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
            .setContentTitle("Running APP")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}