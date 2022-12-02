package com.roadcastassignment.deviceinfo

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Build



class AlarmReceiver : BroadcastReceiver() {
    var ct: Context? = null

    override fun onReceive(context: Context, intent: Intent) {
        try {
            ct = context
            checkService()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun checkService() {
        try {
            if(!isServiceRunning(ct!!,BatteryLevel::class.java))
            {
                val intent1 = Intent(ct, BatteryLevel::class.java)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ct!!.startForegroundService(intent1)
                    } else {
                        ct!!.startService(intent1)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

       /*     if (!ct?.let { isServiceRunning(it, BatteryLevel::class.java)
            }!!)
            {
                val intent1 = Intent(ct, BatteryLevel::class.java)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ct!!.startForegroundService(intent1)
                    } else {
                        ct!!.startService(intent1)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }*/
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun isServiceRunning(mContext: Context, serviceClass: Class<*>): Boolean {
        val manager = mContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className && service.pid != 0) {
                //ShowLog("", "ser name "+serviceClass.getName());
                return true
            }
        }
        return false
    }
}