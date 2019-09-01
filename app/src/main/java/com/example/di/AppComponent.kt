package com.example.di

import android.app.Application
import android.content.Context
import com.example.di.module.MainActivityModule
import com.example.network.di.CustomRequesterModule
import com.example.network.di.RetrofitModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        MainActivityModule::class,
        CustomRequesterModule::class,
        RetrofitModule::class
    ]
)

interface AppComponent {

    fun inject(app: BaseApp)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}