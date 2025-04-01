package moe.hhm.parfait.presentation.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

open class BaseViewModel {
    protected val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    protected val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception> = _error

    fun onCleared() {
        viewModelScope.cancel()
    }
}