/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.viewmodel

import moe.hhm.parfait.app.service.GpaStandardService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

class GpaStandardViewModel : BaseViewModel(), KoinComponent {
    // 日志
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val service : GpaStandardService by inject()
}