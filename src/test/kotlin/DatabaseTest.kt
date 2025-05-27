import kotlinx.coroutines.runBlocking
import moe.hhm.parfait.di.appModule
import moe.hhm.parfait.di.domainModule
import moe.hhm.parfait.di.infrastructureModule
import moe.hhm.parfait.di.presentationModule
import moe.hhm.parfait.infra.db.DatabaseConnectionConfig
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.delay
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.dto.StudentDTO
import org.koin.core.context.GlobalContext.get
import org.slf4j.LoggerFactory
import kotlin.test.assertNotNull

// 测试用表
object TestTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseTest : KoinTest {
    // 数据库文件和目录
    private lateinit var tempDir: Path
    private lateinit var dbFile: File
    private lateinit var dbPath: String
    private val logger = LoggerFactory.getLogger(this::class.java)

    @BeforeAll
    fun koinSetup() {
        stopKoin() // 确保没有其他Koin实例在运行
        startKoin {
            modules(
                listOf(
                    appModule,
                    domainModule,
                    infrastructureModule,
                    presentationModule
                )
            )
        }
        logger.info("Koin已启动，模块已加载")
    }

    @BeforeAll
    fun databaseSetup() = runBlocking {
        // 手动创建临时目录
        tempDir = Files.createTempDirectory("parfait-test-")
        
        // 创建临时数据库文件路径
        dbFile = File(tempDir.toFile(), "test.db")
        dbPath = dbFile.absolutePath

        // 连接到临时数据库
        DatabaseFactory.connect(DatabaseConnectionConfig.standalone(dbPath))
        
        // 等待数据库连接完成
        withTimeout(10000) { // 10秒超时
            // 等待连接状态变为Connected
            while (DatabaseFactory.connectionState.value !is DatabaseConnectionState.Connected) {
                delay(100) // 轮询间隔
            }
        }
        
        // 确保数据库文件存在
        assertTrue(dbFile.exists(), "数据库文件应该已被创建")
        
        // 验证数据库连接状态
        val state = DatabaseFactory.connectionState.value
        assertTrue(state is DatabaseConnectionState.Connected, "数据库应该处于已连接状态")
        logger.info("数据库已连接，状态: $state")
    }
    
    @AfterAll
    fun cleanup() {
        // 断开数据库连接
        DatabaseFactory.disconnect()
        
        // 清理临时目录
        try {
            // 删除数据库文件
            if (dbFile.exists()) {
                dbFile.delete()
            }
            
            // 删除临时目录
            Files.deleteIfExists(tempDir)
        } catch (e: Exception) {
            println("清理临时文件时出错: ${e.message}")
        }
    }

    @Test
    fun `基础读写`() = runBlocking {
        transaction {
            // 创建测试表
            SchemaUtils.create(TestTable)
        }
        transaction {
            // 插入一条测试数据
            TestTable.insert {
                it[name] = "测试数据"
            }

            // 查询并验证数据
            val result = TestTable.selectAll().firstOrNull()
            assertTrue(result != null, "应该有一条测试记录")
            assertEquals("测试数据 1", result!![TestTable.name], "插入的数据应该正确")
        }
        transaction {
            // 创建测试表
            SchemaUtils.drop(TestTable)
        }
    }
    
    @Test
    fun `测试多条记录插入和查询`() = runBlocking {
        transaction {
            // 创建测试表
            SchemaUtils.create(TestTable)
        }
        transaction {
            // 插入多条测试数据
            for (i in 1..5) {
                TestTable.insert {
                    it[name] = "测试数据 $i"
                }
            }
        }
        transaction {
            // 验证记录数量
            val count = TestTable.selectAll().count()
            assertEquals(5, count, "应该有5条测试记录")

            // 验证数据内容
            val records = TestTable.selectAll().map { it[TestTable.name] }
            assertTrue(records.contains("测试数据 1"), "应包含'测试数据 1'")
            assertTrue(records.contains("测试数据 5"), "应包含'测试数据 5'")
        }
    }

    @Test
    fun `学生信息插入`() = runBlocking {
        val data = StudentDTO(
            studentId = "2023001",
            name = "张三",
            gender = StudentDTO.Gender.UNKNOWN,
            status = StudentDTO.Status.ENROLLED,
            department = "计算机学院",
            major = "软件工程",
            grade = 2023,
            classGroup = "1班",
            scores = arrayListOf()
        )
        val studentService : StudentService = get().get()
        studentService.addStudent(data)
        // 验证插入的数据
        val student = studentService.getStudentByStudentId("2023001")
        assertNotNull(student, "应该能找到插入的学生信息")
        assertEquals(data, student.toDTO(), "插入的学生信息应该与查询结果一致")
    }
} 