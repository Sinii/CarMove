package com.example.base.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.example.utils.dLog
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel() {

    val viewModelCommands = SingleLiveEvent<ViewModelCommands>()
    private var baseViewModelJob = SupervisorJob()
    private val baseViewModelScope = CoroutineScope(Dispatchers.Main + baseViewModelJob)

    abstract fun doAutoMainWork()

    fun <P> doInfiniteRepeatWork(
        doBlock: suspend CoroutineScope.() -> P
    ): Job {
        return baseViewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (isActive) {
                    try {
                        doBlock.invoke(this)
                    } catch (e: UnknownHostException) {
                        e.printStackTrace()
                        showError("Server is unreachable")
                    } catch (e: SocketTimeoutException) {
                        e.printStackTrace()
                        showError("No internet connection")
                    } catch (e: ConnectException) {
                        e.printStackTrace()
                        showError("Connection error")
                    }
                }
            }
        }
    }

    fun <P> doWork(doBlock: suspend CoroutineScope.() -> P): Job {
       return doCoroutineWork(doBlock, baseViewModelScope, Dispatchers.IO)
    }

    fun <P> doWorkInMainThread(doBlock: suspend CoroutineScope.() -> P) {
        doCoroutineWork(doBlock, baseViewModelScope, Dispatchers.Main)
    }

    fun cancelChildren() {
        baseViewModelJob.cancelChildren()
    }

    open fun restoreViewModel() {}

    override fun onCleared() {
        "onCleared".dLog()
        baseViewModelScope.cancel()
    }

    fun navigateBack() {
        doWorkInMainThread {
            viewModelCommands.postValue(ViewModelCommands.NavigateBack)
        }
    }

    fun navigate(intent: Intent) {
        doWorkInMainThread {
            viewModelCommands.postValue(ViewModelCommands.NavigateToIntent(intent))
        }
    }

    fun navigate(directions: NavDirections) {
        doWorkInMainThread {
            viewModelCommands.postValue(ViewModelCommands.NavigateTo(directions))
        }
    }

    fun showError(text: String?) {
        if (text != null) {
            doWorkInMainThread {
                viewModelCommands.postValue(ViewModelCommands.ShowError(text))
            }
        }
    }

    fun hideKeyboard() {
        doWorkInMainThread {
            viewModelCommands.postValue(ViewModelCommands.HideKeyboard)
        }
    }

    private inline fun <P> doCoroutineWork(
        crossinline doBlock: suspend CoroutineScope.() -> P,
        coroutineScope: CoroutineScope,
        context: CoroutineContext
    ) : Job {
        return coroutineScope.launch {
            withContext(context) {
                try {
                    doBlock.invoke(this)
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                    showError("Server is unreachable")
                } catch (e: SocketTimeoutException) {
                    e.printStackTrace()
                    showError("No internet connection")
                } catch (e: ConnectException) {
                    e.printStackTrace()
                    showError("Connection error")
                }
            }
        }
    }
}