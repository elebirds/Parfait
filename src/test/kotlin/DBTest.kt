import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.di.appModule
import moe.hhm.parfait.di.domainModule
import moe.hhm.parfait.di.infrastructureModule
import moe.hhm.parfait.di.presentationModule
import moe.hhm.parfait.dto.StudentDTO
import moe.hhm.parfait.infra.db.DatabaseFactory
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.startKoin

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

    // add a student
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
    println(studentService.getAllStudents())
}
