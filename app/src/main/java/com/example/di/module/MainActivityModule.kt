package com.example.di.module

import com.anton.carmapfeature.di.CarMoveFeatureFragmentBuildersModule
import com.anton.carmapfeature.di.CarMoveFeatureViewModelModule
import com.example.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(
    includes = [
        CarMoveFeatureViewModelModule::class
    ]
)
abstract class MainActivityModule {

    @ContributesAndroidInjector(
        modules = [CarMoveFeatureFragmentBuildersModule::class]
    )

    abstract fun contributeMainActivity(): MainActivity

}