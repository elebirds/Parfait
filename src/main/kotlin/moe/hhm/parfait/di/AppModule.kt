/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.di

import moe.hhm.parfait.app.certificate.CertificateGenerator
import moe.hhm.parfait.app.certificate.TemplateModelBuilder
import moe.hhm.parfait.app.service.*
import moe.hhm.parfait.app.service.impl.*
import moe.hhm.parfait.app.term.MapBasedContextProvider
import moe.hhm.parfait.app.term.StudentContextProvider
import moe.hhm.parfait.app.term.TermParser
import moe.hhm.parfait.app.term.TermProcessor
import org.koin.dsl.module

/**
 * 应用依赖注入模块
 */
val appModule = module {
    /// 基础服务
    single<GpaStandardService> { GpaStandardServiceImpl(get()) }
    single<StudentService> { StudentServiceImpl(get()) }
    single<StudentSearchService> { StudentSearchServiceImpl() }
    single<TermService> { TermServiceImpl(get()) }
    single<TermSearchService> { TermSearchServiceImpl() }
    single<CertificateTemplateService> { CertificateTemplateServiceImpl(get()) }
    single<CertificateDataService> { CertificateDataServiceImpl(get()) }
    single<CertificateRecordService> { CertificateRecordServiceImpl() }

    ///业务逻辑
    // 术语处理相关
    single { TermParser() }
    single { MapBasedContextProvider() }
    single { StudentContextProvider() }
    single { TermProcessor(get(), get<StudentContextProvider>()) }

    // 模板生成相关
    single { TemplateModelBuilder() }
    single { CertificateGenerator() }
}