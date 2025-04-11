/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.view

import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JPanel
import kotlin.getValue

class CertificateTemplateView(parent: DefaultCoroutineComponent? = null) : JPanel(), KoinComponent,
    CoroutineComponent by DefaultCoroutineComponent(parent) {
    // 通过Koin获取ViewModel
    private val viewModel: CertificateTemplateView by inject()
}