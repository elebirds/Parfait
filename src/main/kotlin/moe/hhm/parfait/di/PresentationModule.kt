/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.di

import moe.hhm.parfait.ui.viewmodel.StudentListViewModel
import org.koin.dsl.module

val presentationModule = module {
    // ViewModel
    single<StudentListViewModel> { StudentListViewModel(get()) }
}