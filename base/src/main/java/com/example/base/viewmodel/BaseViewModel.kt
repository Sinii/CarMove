package com.example.base.viewmodel

import androidx.lifecycle.ViewModel
import com.example.utils.dLog
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel() {
    private var baseViewModelJob = SupervisorJob()
    private val baseViewModelScope = CoroutineScope(Dispatchers.Main + baseViewModelJob)

    abstract fun doAutoMainWork()

    fun <P> doWork(doBlock: suspend CoroutineScope.() -> P): Job {
        return doCoroutineWork(doBlock, baseViewModelScope, Dispatchers.IO)
    }

    fun cancelChildren() {
        baseViewModelJob.cancelChildren()
    }

    open fun restoreViewModel() {}

    override fun onCleared() {
        "onCleared".dLog()
        baseViewModelScope.cancel()
    }

    private inline fun <P> doCoroutineWork(
        crossinline doBlock: suspend CoroutineScope.() -> P,
        coroutineScope: CoroutineScope,
        context: CoroutineContext
    ): Job {
        return coroutineScope.launch {
            withContext(context) {
                try {
                    doBlock.invoke(this)
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                } catch (e: SocketTimeoutException) {
                    e.printStackTrace()
                } catch (e: ConnectException) {
                    e.printStackTrace()
                }
            }
        }
    }
}