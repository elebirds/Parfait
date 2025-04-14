/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.di

import moe.hhm.parfait.domain.repository.*
import moe.hhm.parfait.infra.repository.*
import org.koin.dsl.module

val infrastructureModule = module {
    single<GpaRepository> { GpaRepositoryImpl() }
    single<StudentRepository> { StudentRepositoryImpl() }
    single<StudentSearchRepository> { StudentSearchRepositoryImpl() }
    single<TermRepository> { TermRepositoryImpl() }
    single<CertificateTemplateRepository> { CertificateTemplateRepositoryImpl() }
    single<CertificateDataRepository> { CertificateDataRepositoryImpl() }
    single<CertificateRecordRepository> { CertificateRecordRepositoryImpl() }
}