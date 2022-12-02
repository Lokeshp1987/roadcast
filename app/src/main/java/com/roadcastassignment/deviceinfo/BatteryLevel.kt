package com.roadcastassignment.deviceinfo

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.SensorManager
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import com.roadcastassignment.R
import com.roadcastassignment.ui.entries.EntriesActivity


class BatteryLevel : Service() {
    var context: Context? = null
    var batLevel = 100
    var temp  : Int = 5
    var preLevel = 0
    var mSensorManager : SensorManager?= null
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        val bm = applicationContext.getSystemService(BATTERY_SERVICE) as BatteryManager
        // Get the battery percentage and store it in a INT variable
        batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        // timeInterval_handler.postDelayed(timeInterval_runnable, 1000)
        this.registerReceiver(this.mBatInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        // Display the variable using a Toast


        ShowNotification()
    }

    private val mBatInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(ctxt: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0)
            if(temp>0)
                temp = temp/10
            val batteryPct = level * 100 / scale.toFloat()
            batLevel = batteryPct.toInt()
            if (preLevel != batLevel)
                ShowNotification()

            preLevel = batLevel
            //Toast.makeText(applicationContext,"Battery is ${batteryPct.toString()}", Toast.LENGTH_LONG).show()
            Log.d("sdsfghg", batteryPct.toString())
            // batteryTxt.setText("$batteryPct%")


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ShowNotification() {
        try {
            val NOTIFICATION_ID = 1111
            val contentIntent: PendingIntent
            val notificationIntent = Intent(this, EntriesActivity::class.java)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            var channelId = ""
            // contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            //Changed by Sachin
            contentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
            }

            var inf = "Battery level : $batLevel %" + "\nDevice Temp : $temp °C"
            val builder: Notification.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channelId =
                    createNotificationChannel("my_service_attendance", "My Background Service")
                builder = Notification.Builder(baseContext, channelId)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(inf)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(contentIntent)
            } else {
                builder = Notification.Builder(baseContext)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(inf)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(contentIntent)
            }
            val build = builder.build()
            startForeground(NOTIFICATION_ID, build)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        val restartServiceTask = Intent(this, EntriesActivity::class.java)
        restartServiceTask.setPackage(packageName)
        val restartPendingIntent = PendingIntent.getService(
            applicationContext,
            111,
            restartServiceTask,
            PendingIntent.FLAG_ONE_SHOT
        )
        val myAlarmService = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        myAlarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000] =
            restartPendingIntent
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        timeInterval_handler.removeCallbacks(timeInterval_runnable)
        unregisterReceiver(mBatInfoReceiver)
    }

    var timeInterval_handler = Handler()
    var timeInterval_runnable: Runnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            timeInterval_handler.removeCallbacks(this)
            val bm = applicationContext.getSystemService(BATTERY_SERVICE) as BatteryManager
            // Get the battery percentage and store it in a INT variable
            ShowNotification()
            batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            timeInterval_handler.postDelayed(this, 1000)
        }
    }

}