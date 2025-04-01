package moe.hhm.parfait.presentation.common

import java.util.concurrent.CopyOnWriteArrayList

open class LiveData<T> {
    private var value: T? = null
    private val observers = CopyOnWriteArrayList<(T) -> Unit>()

    fun observe(observer: (T) -> Unit) {
        observers.add(observer)
        value?.let { observer(it) }
    }

    fun removeObserver(observer: (T) -> Unit) {
        observers.remove(observer)
    }

    protected open fun setValue(newValue: T) {
        value = newValue
        observers.forEach { it(newValue) }
    }

    fun getValue(): T? = value
}

class MutableLiveData<T> : LiveData<T>() {
    public override fun setValue(value: T) {
        super.setValue(value)
    }
}