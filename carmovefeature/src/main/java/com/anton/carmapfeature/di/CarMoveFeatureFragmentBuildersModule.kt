package com.anton.carmapfeature.di


import com.anton.carmapfeature.ui.CarMapFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CarMoveFeatureFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeCarMoveFragment(): CarMapFragment

    //Add more Fragments here

}
