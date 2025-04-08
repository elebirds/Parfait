/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.di

import moe.hhm.parfait.app.service.GradeCalculationService
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.app.service.impl.GradeCalculationServiceImpl
import moe.hhm.parfait.app.service.impl.StudentServiceImpl
import org.koin.dsl.module

/**
 * 应用依赖注入模块
 */
val appModule = module {
    // 领域服务
    single<GradeCalculationService> { GradeCalculationServiceImpl(get()) }

    // 仓储实现
    single<StudentService> { StudentServiceImpl(get()) }

}