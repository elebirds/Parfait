/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.di

import moe.hhm.parfait.domain.repository.CertificateDataRepository
import moe.hhm.parfait.domain.repository.CertificateTemplateRepository
import moe.hhm.parfait.domain.repository.GpaRepository
import moe.hhm.parfait.domain.repository.StudentRepository
import moe.hhm.parfait.domain.repository.StudentSearchRepository
import moe.hhm.parfait.domain.repository.TermRepository
import moe.hhm.parfait.infra.repository.CertificateDataRepositoryImpl
import moe.hhm.parfait.infra.repository.CertificateTemplateRepositoryImpl
import moe.hhm.parfait.infra.repository.GpaRepositoryImpl
import moe.hhm.parfait.infra.repository.StudentRepositoryImpl
import moe.hhm.parfait.infra.repository.StudentSearchRepositoryImpl
import moe.hhm.parfait.infra.repository.TermRepositoryImpl
import org.koin.dsl.module

val infrastructureModule = module {
    single<GpaRepository> { GpaRepositoryImpl() }
    single<StudentRepository> { StudentRepositoryImpl() }
    single<StudentSearchRepository> { StudentSearchRepositoryImpl() }
    single<TermRepository> { TermRepositoryImpl() }
    single<CertificateTemplateRepository> { CertificateTemplateRepositoryImpl() }
    single<CertificateDataRepository> { CertificateDataRepositoryImpl() }
}