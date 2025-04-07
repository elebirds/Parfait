import kotlinx.coroutines.flow.MutableStateFlow


/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.hhm.parfait.ui.state.mutableStateOf

val _stateFlow = mutableStateOf<Int>(0) // 初始值为 0
val stateFlow by _stateFlow

fun main(): Unit = runBlocking {

    // 启动数据更新
    launch {
        for (i in 1..3) {
            delay(1000)
            _stateFlow.value = i // 只会存储最新值
            println("更新数据: $i")
        }
    }

    launch {
        delay(1500) // 延迟一会，让数据先更新
        println("订阅者开始收集")
        _stateFlow.addListener { println("订阅者收到: $it") }
    }
}