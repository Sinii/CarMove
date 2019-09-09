package com.anton.carmapfeature.ui

import android.annotation.SuppressLint
import android.view.MotionEvent.ACTION_DOWN
import androidx.lifecycle.Observer
import com.anton.carmapfeature.R
import com.anton.carmapfeature.databinding.FragmentCarMapBinding
import com.example.base.di.ViewModelFactory
import com.example.base.ui.BaseFragment
import com.example.utils.dLog


class CarMapFragment : BaseFragment<FragmentCarMapBinding, ViewModelFactory>() {

    override fun provideListOfViewModels(): Array<Class<*>> = arrayOf(
        CarViewModel::class.java
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun provideActionsBinding(): (FragmentCarMapBinding, Set<*>) -> Unit =
        { binding, viewModelList ->
            viewModelList.forEach { viewModel ->
                when (viewModel) {
                    is CarViewModel -> {
                        "provideActionsBinding CarViewModel".dLog()
                        viewModel.carCoordinatesXYAngle.observe(this, Observer {
                            "CarViewModel addAllCoordinates".dLog()
                            binding.carMap.addAllCoordinates(it)
                        })
                        binding.carMap.setOnTouchListener { _, motionEvent ->
                            if (motionEvent.action == ACTION_DOWN) {
                                viewModel.destinationCoordinates(
                                    motionEvent.x, motionEvent.y, binding.carMap.getAngle(),
                                    binding.carMap.getCarX(), binding.carMap.getCarY()
                                )
                            }
                            return@setOnTouchListener true
                        }
                    }
                }
            }
        }

    override fun provideLayout() = R.layout.fragment_car_map

    override fun provideLifecycleOwner() = this

}