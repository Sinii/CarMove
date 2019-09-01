package com.anton.carmapfeature.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anton.carmapfeature.ui.CarViewModel
import com.example.base.di.ViewModelFactory
import com.example.base.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class CarMoveFeatureViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CarViewModel::class)
    internal abstract fun bindCarMoveViewModel(viewModel: CarViewModel): ViewModel

    //Add more ViewModels here
}