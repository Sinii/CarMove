package com.example.ui

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.example.R
import com.example.base.di.interfaces.ActivityDefaultBehavior
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity(), ActivityDefaultBehavior {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun showError(errorText: String) {
        //todo return universe error showing solution if needed. For now toast is enough
        Toast.makeText(this, errorText, Toast.LENGTH_LONG).show()
    }
}