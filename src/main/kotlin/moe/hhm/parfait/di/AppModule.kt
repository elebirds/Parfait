/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.di

import moe.hhm.parfait.app.service.GpaStandardService
import moe.hhm.parfait.app.service.StudentSearchService
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.app.service.TermService
import moe.hhm.parfait.app.service.impl.GpaStandardServiceImpl
import moe.hhm.parfait.app.service.impl.StudentSearchServiceImpl
import moe.hhm.parfait.app.service.impl.StudentServiceImpl
import moe.hhm.parfait.app.service.impl.TermServiceImpl
import org.koin.dsl.module

/**
 * 应用依赖注入模块
 */
val appModule = module {
    single<GpaStandardService> { GpaStandardServiceImpl(get()) }
    single<StudentService> { StudentServiceImpl(get()) }
    single<StudentSearchService> { StudentSearchServiceImpl() }
    single<TermService> { TermServiceImpl(get()) }
}