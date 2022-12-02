package com.roadcastassignment.ui.entries

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.roadcastassignment.deviceinfo.AlarmReceiver
import com.roadcastassignment.model.Entry
import com.roadcastassignment.ui.map.MapActivity
import com.roadcastassignment.ui.theme.RoadCastAssignmentTheme
import com.roadcastassignment.utils.ApiState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EntriesActivity : ComponentActivity()
{

    private val entriesViewModel : EntriesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoadCastAssignmentTheme {
                Surface(color = MaterialTheme.colors.background) {

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title =
                                {
                                    Text(text = "Roadcast")
                                },
                                navigationIcon =
                                {
                                    IconButton(onClick = { Toast.makeText(this,"Work under progress",Toast.LENGTH_SHORT).show() })
                                    {
                                        Icon(Icons.Filled.Menu, contentDescription = "menu")
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { openLiveMap()}) {
                                        Icon(Icons.Filled.LocationOn, contentDescription = "noti")
                                    }
                                  /*  IconButton(onClick = { *//*TODO*//* })
                                    {
                                        Icon(Icons.Filled.Info, contentDescription = "search")
                                    }
                                    showSwitch()*/
                                }
                            )
                        },

                    )
                    {

                        GetData(entriesViewModel = entriesViewModel)
                    }


                }
            }
        }
        createAlarmReceiver(this)
    }

    @Composable
    fun EachRow(entry: Entry) {
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(), elevation = 2.dp, shape = RoundedCornerShape(4.dp)
        )
        {
            Column() {
                Text(text = "Title : "+entry.API, modifier = Modifier.padding(10.dp))
                Text(text = "Des : "+entry.Description, modifier = Modifier.padding(10.dp))
                ClickableText(text = AnnotatedString("Get More"), modifier = Modifier.padding(10.dp), onClick ={openBrowser(entry.Link)} )

            }

        }
    }

    private fun openBrowser(link : String)
    {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }
    @Composable
    fun GetData(entriesViewModel: EntriesViewModel)
    {

        when(val result  = entriesViewModel.response.value)
        {
            is ApiState.Success ->
            {
                LazyColumn{
                    items(result.data.entries)
                    { response ->
                        EachRow(entry = response)
                    }
                }

            }
            is ApiState.Failure ->{
                Text(text = "${result.message}")
            }
            is ApiState.Loading ->{
                ProgressBar()
            }
            is ApiState.Empty->{

            }
        }
    }

    @Composable
    fun ProgressBar() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }


    @Composable
    fun showSwitch() {
        val isChecked = remember {
            mutableStateOf(true)
        }

        Switch(checked = isChecked.value, colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            uncheckedThumbColor = Color.Gray,
            checkedTrackColor = Color.White,
            uncheckedTrackColor = Color.Gray
        ), onCheckedChange = {
            isChecked.value = it
        },
            modifier = Modifier.run {
                size(20.dp)
                padding(5.dp)
            }
        )
    }

    private fun openLiveMap()
    {
        startActivity(Intent(this, MapActivity::class.java))

    }
    //Register Alarm to launch forground service for show Bettery percentage and Temperature
    fun createAlarmReceiver(ct: Context) {
        val alarmManager = ct.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(ct, AlarmReceiver::class.java)
        intent.action = "android.alarm.receiver"


        //PendingIntent pendingIntent = PendingIntent.getBroadcast(ct, 100, intent, FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val pendingIntent =
                PendingIntent.getBroadcast(ct, 100, intent, PendingIntent.FLAG_MUTABLE)
            alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1 * 1000] =
                pendingIntent
        } else {
            val pendingIntent =
                PendingIntent.getBroadcast(ct, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1 * 1000] =
                pendingIntent
        }
    }
}