package com.example.di

import android.app.Activity
import android.app.Application
import com.example.di.utils.AppInjector
import com.example.utils.dLog
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class BaseApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        "BaseApp onCreate".dLog()
    }

    override fun activityInjector() = dispatchingAndroidInjector
}