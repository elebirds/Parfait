/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.di

import moe.hhm.parfait.ui.viewmodel.CertificateTemplateViewModel
import moe.hhm.parfait.ui.viewmodel.GpaStandardViewModel
import moe.hhm.parfait.ui.viewmodel.StudentDataViewModel
import moe.hhm.parfait.ui.viewmodel.TermViewModel
import org.koin.dsl.module

val presentationModule = module {
    // ViewModel
    single<StudentDataViewModel> { StudentDataViewModel() }
    single<GpaStandardViewModel> { GpaStandardViewModel() }
    single<CertificateTemplateViewModel> { CertificateTemplateViewModel() }
    single<TermViewModel> { TermViewModel() }
}