package com.example.di

import android.content.Context
import com.example.di.module.MainActivityModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        MainActivityModule::class
    ]
)

interface AppComponent {

    fun inject(app: BaseApp)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: BaseApp): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}