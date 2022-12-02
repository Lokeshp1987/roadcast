package com.roadcastassignment.app

import android.app.Application
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp
class BaseApplication : Application()
{

}