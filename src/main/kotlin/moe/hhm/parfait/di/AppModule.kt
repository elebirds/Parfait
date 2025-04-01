package moe.hhm.parfait.di

import moe.hhm.parfait.app.service.GradeCalculationService
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.app.service.impl.GradeCalculationServiceImpl
import moe.hhm.parfait.app.service.impl.StudentServiceImpl
import org.koin.dsl.module

val appModule = module {
    single<GradeCalculationService> { GradeCalculationServiceImpl(get()) }
    single<StudentService> { StudentServiceImpl(get()) }
}