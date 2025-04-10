import moe.hhm.parfait.app.service.GradeCalculationService
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.app.service.impl.GradeCalculationServiceImpl
import moe.hhm.parfait.di.appModule
import moe.hhm.parfait.di.domainModule
import moe.hhm.parfait.di.infrastructureModule
import moe.hhm.parfait.di.presentationModule
import moe.hhm.parfait.dto.ScoreDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.db.DatabaseConnectionConfig
import moe.hhm.parfait.infra.db.DatabaseFactory
import org.apache.commons.io.FileUtils
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.startKoin
import java.io.File

/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

suspend fun main() {
    DatabaseFactory.connect(DatabaseConnectionConfig.standalone("data.pardb"))
    startKoin {
        modules(
            infrastructureModule,
            domainModule,
            appModule,
            presentationModule
        )
    }
    val studentService : StudentService = get().get()

    FileUtils.readLines(File("students.csv")).forEach {
        if(it.isBlank()) return@forEach
        val b = it.trim().split(",")
        if(b.size != 6) return@forEach
        studentService.addStudent(StudentDTO(
            studentId = b[0],
            name = b[1],
            gender = StudentDTO.Gender.UNKNOWN,
            status = StudentDTO.Status.ENROLLED,
            department = b[3],
            major = b[4],
            grade = b[2].toInt(),
            classGroup = b[5],
            scores = arrayListOf()
        ))
    }
}
