package com.example.base.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.utils.eLog
import java.util.concurrent.atomic.AtomicBoolean

open class SingleLiveEvent<T> : LiveData<T>() {

    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            "Multiple observers registered but only one will be notified of changes.".eLog()
        }

        super.observe(owner, Observer { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    public override fun postValue(t: T) {
        super.postValue(t)
    }

    @MainThread
    public override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }
}
