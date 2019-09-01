package com.example.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.base.viewmodel.BaseViewModel
import com.example.base.viewmodel.ViewModelCommands
import com.example.interfaces.ActivityDefaultBehavior
import com.example.interfaces.Injectable
import com.example.utils.dLog
import com.example.utils.eLog
import com.example.utils.hideKeyboard
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


abstract class BaseFragment<B : ViewDataBinding, VMF : ViewModelProvider.Factory>
    : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: VMF

    private var viewModelList: HashSet<BaseViewModel> = HashSet()

    abstract fun provideListOfViewModels(): Array<Class<*>>

    abstract fun provideActionsBinding(): (B, Set<*>) -> Unit

    abstract fun provideLayout(): Int

    abstract fun provideLifecycleOwner(): Fragment

    open fun busEvents(command: ViewModelCommands, viewModelList: Set<*>, binding: B): Boolean {
        when (command) {
            is ViewModelCommands.NavigateBack -> {
                try {
                    hideKeyboard()
                    findNavController().popBackStack()
                } catch (e: java.lang.IllegalArgumentException) {
                    e.printStackTrace()
                    "trying to navigate after closing page".eLog()
                }
            }
            is ViewModelCommands.NavigateToIntent -> {
                try {
                    hideKeyboard()
                    startActivity(command.intent)
                } catch (e: java.lang.IllegalArgumentException) {
                    e.printStackTrace()
                    "trying to navigate after closing page".eLog()
                }
            }
            is ViewModelCommands.NavigateTo -> {
                try {
                    hideKeyboard()
                    findNavController().navigate(command.directions)
                } catch (e: java.lang.IllegalArgumentException) {
                    e.printStackTrace()
                    "trying to navigate after closing page".eLog()
                }
            }
            is ViewModelCommands.ShowError ->
                (provideLifecycleOwner().requireActivity() as ActivityDefaultBehavior).showError(
                    command.text
                )
            is ViewModelCommands.HideKeyboard ->
                this@BaseFragment.hideKeyboard()
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            AndroidSupportInjection.inject(provideLifecycleOwner())
        } catch (exception: IllegalArgumentException) {
            exception.printStackTrace()
            "Add Fragment ${provideLifecycleOwner()} into FragmentBuildersModule".eLog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        "${provideLifecycleOwner().javaClass} onCreateView inflate dataBinding..".dLog()
        val binding = DataBindingUtil
            .inflate<B>(
                inflater,
                provideLayout(),
                container,
                false
            )
        this@BaseFragment.hideKeyboard()
        var firstLaunch = true
        provideListOfViewModels()
            .forEach { viewModelClass ->
                try {
                    @Suppress("UNCHECKED_CAST")
                    ViewModelProviders
                        .of(this, viewModelFactory)
                        .get(viewModelClass as Class<BaseViewModel>)
                        .apply {
                            this.viewModelCommands.observe(
                                provideLifecycleOwner().viewLifecycleOwner,
                                Observer { command ->
                                    if (!busEvents(command, viewModelList, binding)) {
                                        "Do not forget to add super.busEvents()".eLog()
                                    }
                                })
                            if (viewModelList.contains(this)) {
                                firstLaunch = false
                            }
                            viewModelList.add(this)
                        }
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    "Add ViewModel $viewModelClass into ViewModelFactory".eLog()
                } catch (e: UninitializedPropertyAccessException) {
                    e.printStackTrace()
                }
            }

        binding.lifecycleOwner = provideLifecycleOwner().viewLifecycleOwner
        binding.apply { provideActionsBinding().invoke(this, viewModelList) }
        viewModelList.forEach {
            if (firstLaunch) it.doAutoMainWork() else it.restoreViewModel()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        "${provideLifecycleOwner().javaClass} onResume".dLog()

    }

    override fun onStop() {
        "${provideLifecycleOwner().javaClass} onStop + $this".dLog()

        viewModelList.forEach { viewModel ->
            viewModel.cancelChildren()
        }
        super.onStop()
    }

    override fun onDestroy() {
        "${provideLifecycleOwner().javaClass} onDestroy".dLog()

        viewModelList.clear()
        super.onDestroy()
    }

}

