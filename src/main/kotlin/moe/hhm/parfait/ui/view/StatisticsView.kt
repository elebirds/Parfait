/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.view

import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.viewmodel.StatisticsViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JPanel

class StatisticsView(parent: DefaultCoroutineComponent? = null) : JPanel(), KoinComponent,
    CoroutineComponent by DefaultCoroutineComponent(parent) {
    // 通过Koin获取ViewModel
    private val viewModel: StatisticsViewModel by inject()
}