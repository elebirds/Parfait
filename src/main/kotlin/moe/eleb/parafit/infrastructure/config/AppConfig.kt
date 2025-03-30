package moe.eleb.parafit.infrastructure.config

data class AppConfig(
    val serverMode: Boolean = false,
    val dbHost: String = "localhost",
    val dbPort: Int = 3306,
    val dbName: String = "student_system",
    val dbUser: String = "root",
    val dbPassword: String = "",
    val dbFilePath: String = "data/student_system.db",
    val logLevel: String = "INFO",
    val schoolName: String = "示例大学",
    val schoolNameEnglish: String = "Example University",
    val academicYear: String = "",
    val semester: String = ""
)