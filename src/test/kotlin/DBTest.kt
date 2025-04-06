import moe.hhm.parfait.app.service.GradeCalculationService
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.app.service.impl.GradeCalculationServiceImpl
import moe.hhm.parfait.di.appModule
import moe.hhm.parfait.di.domainModule
import moe.hhm.parfait.di.infrastructureModule
import moe.hhm.parfait.di.presentationModule
import moe.hhm.parfait.dto.ScoreDTO
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.db.DatabaseFactory
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.startKoin

/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

suspend fun main() {
    DatabaseFactory.init()
    startKoin {
        modules(
            infrastructureModule,
            domainModule,
            appModule,
            presentationModule
        )
    }
    val studentService : StudentService = get().get()
    val gpaService : GradeCalculationService = get().get()

    // add a student
    var student = studentService.getStudentById("220100001")
    if(student == null) {
        studentService.addStudent(StudentDTO(
            studentId = "220100001",
            gender = StudentDTO.Gender.MALE,
            name = "张三",
            status = StudentDTO.Status.ENROLLED,
            department = "计算机学院",
            grade = 2025,
            scores = listOf(),
            major = "软件工程",
            classGroup = "1班"
        ))
        student = studentService.getStudentById("220100001")!!
    }
    val sdto = student.into()
    studentService.addScore(sdto,
        ScoreDTO(
            name = "计算机导论",
            type = "专业基础课程",
            exam = "考试",
            credit = 4,
            score = 89.9,
            gpaS = "是"
        )
    )
    studentService.addScore(sdto,
        ScoreDTO(
            name = "基础法语1",
            type = "专业基础课程",
            exam = "考试",
            credit = 6,
            score = 65.0,
            gpaS = "是"
        )
    )
    val gpa = gpaService.gpa(sdto.scores)
    val weightedMean = gpaService.weightedMean(sdto.scores)
    val simpleMean = gpaService.simpleMean(sdto.scores)
    println("GPA: $gpa")
    println("Weighted Mean: $weightedMean")
    println("Simple Mean: $simpleMean")
    println(studentService.getAllStudents())
}
