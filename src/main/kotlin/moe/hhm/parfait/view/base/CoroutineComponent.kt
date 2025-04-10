/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

interface CoroutineComponent {
    val job: Job
    val scope: CoroutineScope
    fun observer()
}

class DefaultCoroutineComponent(parent: Job? = null, dispatcher: CoroutineDispatcher = Dispatchers.Main) : CoroutineComponent {
    constructor(parent: CoroutineComponent?, dispatcher: CoroutineDispatcher = Dispatchers.Main) : this(parent?.job, dispatcher)
    override val job = SupervisorJob(parent)
    override val scope = CoroutineScope(dispatcher + job)

    override fun observer() {
        // No-op
    }
}
