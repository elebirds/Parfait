/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.di

import moe.hhm.parfait.domain.repository.GpaRepository
import moe.hhm.parfait.domain.repository.StudentRepository
import moe.hhm.parfait.domain.repository.StudentSearchRepository
import moe.hhm.parfait.infra.repository.GpaRepositoryImpl
import moe.hhm.parfait.infra.repository.StudentRepositoryImpl
import moe.hhm.parfait.infra.repository.StudentSearchRepositoryImpl
import org.koin.dsl.module

val infrastructureModule = module {
    single<GpaRepository> { GpaRepositoryImpl() }
    single<StudentRepository> { StudentRepositoryImpl() }
    single<StudentSearchRepository> { StudentSearchRepositoryImpl() }
}