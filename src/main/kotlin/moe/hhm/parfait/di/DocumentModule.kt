/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.di

import moe.hhm.parfait.app.certificate.CertificateGenerator
import moe.hhm.parfait.app.certificate.TemplateModelBuilder
import moe.hhm.parfait.app.term.TermProcessor
import org.koin.dsl.module

/**
 * 文档生成模块依赖注入
 */
val documentModule = module {
    // 证书生成相关
    single { CertificateGenerator() }
    factory { TemplateModelBuilder() }
    factory { TermProcessor(get(), get(), get()) }
} 