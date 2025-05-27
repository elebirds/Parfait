import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import moe.hhm.parfait.app.service.GpaStandardService
import moe.hhm.parfait.app.service.StudentService
import moe.hhm.parfait.app.service.TermService
import moe.hhm.parfait.di.appModule
import moe.hhm.parfait.di.domainModule
import moe.hhm.parfait.di.infrastructureModule
import moe.hhm.parfait.di.presentationModule
import moe.hhm.parfait.dto.*
import moe.hhm.parfait.infra.db.DatabaseConnectionConfig
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import org.jetbrains.exposed.dao.id.EntityID
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
import org.koin.core.context.GlobalContext.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
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
        val studentService: StudentService = get().get()
        studentService.addStudent(data)
        // 验证插入的数据
        val student = studentService.getStudentByStudentId("2023001")
        assertNotNull(student, "应该能找到插入的学生信息")
        assertEquals(data, student.toDTO(), "插入的学生信息应该与查询结果一致")
    }

    @Test
    fun `学生分数添加`() = runBlocking {
        // 先插入一个学生
        val studentData = StudentDTO(
            studentId = "2023002",
            name = "李四",
            gender = StudentDTO.Gender.MALE,
            status = StudentDTO.Status.ENROLLED,
            department = "计算机学院",
            major = "计算机科学与技术",
            grade = 2023,
            classGroup = "2班",
            scores = arrayListOf()
        )

        val studentService: StudentService = get().get()
        studentService.addStudent(studentData)

        // 获取插入后的学生对象（包含UUID）
        val insertedStudent = studentService.getStudentByStudentId("2023002")
        assertNotNull(insertedStudent, "应该能找到刚插入的学生")
        val studentWithUuid = insertedStudent.toDTO()

        // 创建多个分数记录
        val scores = listOf(
            ScoreDTO(
                name = "高等数学",
                type = CourseType.PROFESSIONAL_BASIC,
                exam = "考试",
                credit = 4,
                score = 85.5,
                gpa = true
            ),
            ScoreDTO(
                name = "大学英语",
                type = CourseType.GENERAL_REQUIRED,
                exam = "考试",
                credit = 3,
                score = 92.0,
                gpa = true
            ),
            ScoreDTO(
                name = "数据结构",
                type = CourseType.PROFESSIONAL_CORE,
                exam = "考试",
                credit = 4,
                score = 88.0,
                gpa = true
            )
        )

        // 添加分数到学生
        scores.forEach { score ->
            studentService.addScore(studentWithUuid, score)
        }

        // 验证分数是否正确添加
        val updatedStudent = studentService.getStudentByStudentId("2023002")
        assertNotNull(updatedStudent, "应该能找到学生信息")
        assertEquals(3, updatedStudent.toDTO().scores.size, "应该有3条成绩记录")

        // 验证每个分数的详细信息
        val studentScores = updatedStudent.toDTO().scores
        assertTrue(studentScores.any { it.name == "高等数学" && it.score == 85.5 }, "应包含高等数学成绩")
        assertTrue(studentScores.any { it.name == "大学英语" && it.score == 92.0 }, "应包含大学英语成绩")
        assertTrue(studentScores.any { it.name == "数据结构" && it.score == 88.0 }, "应包含数据结构成绩")

        // 测试加权平均分和简单平均分
        val weightedAvg = studentScores.weightedMean()
        val simpleAvg = studentScores.simpleMean()
        logger.info("加权平均分: $weightedAvg, 简单平均分: $simpleAvg")
        assertTrue(weightedAvg > 0, "加权平均分应该大于0")
        assertTrue(simpleAvg > 0, "简单平均分应该大于0")
    }

    @Test
    fun `GPA标准插入`() = runBlocking {
        val gpaService: GpaStandardService = get().get()

        // 创建一个新的GPA标准
        val mappingData = listOf(
            SingleGpaMapping("A+", 95..100, 4.3),
            SingleGpaMapping("A", 90..94, 4.0),
            SingleGpaMapping("A-", 85..89, 3.7),
            SingleGpaMapping("B+", 80..84, 3.3),
            SingleGpaMapping("B", 75..79, 3.0),
            SingleGpaMapping("B-", 70..74, 2.7),
            SingleGpaMapping("C+", 65..69, 2.3),
            SingleGpaMapping("C", 60..64, 2.0),
            SingleGpaMapping("D", 50..59, 1.0),
            SingleGpaMapping("F", 0..49, 0.0)
        )

        val gpaStandard = GpaStandardDTO(
            name = "测试GPA标准",
            description = "用于单元测试的GPA标准",
            category = "测试",
            purpose = "单元测试",
            isDefault = false,
            isLike = false,
            mapping = GpaMappingDTO(mappingData)
        )

        // 插入GPA标准
        val insertedId = gpaService.addGpaStandard(gpaStandard)
        assertNotNull(insertedId, "应该返回插入的ID")

        // 验证插入的数据
        val retrievedStandard = gpaService.getGpaStandardByName("测试GPA标准")
        assertNotNull(retrievedStandard, "应该能找到插入的GPA标准")
        assertEquals("测试GPA标准", retrievedStandard.name, "名称应该一致")
        assertEquals("测试", retrievedStandard.category, "类别应该一致")

        // 测试GPA计算
        val testScores = listOf(
            ScoreDTO(
                name = "测试课程1",
                type = CourseType.DEFAULT,
                exam = "考试",
                credit = 3,
                score = 95.0,
                gpa = true
            ),
            ScoreDTO(
                name = "测试课程2",
                type = CourseType.DEFAULT,
                exam = "考试",
                credit = 4,
                score = 85.0,
                gpa = true
            ),
            ScoreDTO(name = "测试课程3", type = CourseType.DEFAULT, exam = "考试", credit = 2, score = 70.0, gpa = true)
        )

        val calculatedGpa = gpaStandard.mapping.getGpa(testScores)
        logger.info("计算的GPA: $calculatedGpa")
        assertTrue(calculatedGpa > 0, "GPA应该大于0")

        // 验证GPA映射表的有效性
        val validationResult = gpaStandard.mapping.validCode()
        assertEquals(GpaMappingValidState.VALID, validationResult, "GPA映射表应该是有效的")

        // 测试获取所有GPA标准
        val allStandards = gpaService.getAllGpaStandards()
        assertTrue(allStandards.isNotEmpty(), "应该至少有一个GPA标准")
        assertTrue(allStandards.any { it.name == "测试GPA标准" }, "应包含刚插入的测试标准")
    }

    @Test
    fun `术语添加测试`() = runBlocking {
        val termService: TermService = get().get()

        // 创建多个术语测试数据
        val terms = listOf(
            // 基础术语（无上下文，无语言）
            TermDTO(
                field = "university",
                context = null,
                language = null,
                term = "大学"
            ),
            // 带上下文的术语
            TermDTO(
                field = "department",
                context = "science",
                language = null,
                term = "理学院"
            ),
            // 带语言的术语
            TermDTO(
                field = "degree",
                context = null,
                language = "zh",
                term = "学士学位"
            ),
            // 完整的术语（带上下文和语言）
            TermDTO(
                field = "major",
                context = "computer_science",
                language = "zh",
                term = "计算机科学与技术"
            ),
            // 英文术语
            TermDTO(
                field = "major",
                context = "computer_science",
                language = "en",
                term = "Computer Science and Technology"
            )
        )

        // 插入所有术语
        val insertedIds = mutableListOf<EntityID<UUID>>()
        terms.forEach { term ->
            val id = termService.add(term)
            assertNotNull(id, "应该返回插入的ID")
            insertedIds.add(id)
        }

        // 验证术语数量
        val totalCount = termService.count()
        assertTrue(totalCount >= terms.size, "术语总数应该至少包含刚插入的术语")

        // 测试查询功能
        // 1. 查询基础术语
        val universityTerm = termService.getTerm("university")
        assertNotNull(universityTerm, "应该能找到university术语")
        assertEquals("大学", universityTerm.term, "术语值应该正确")

        // 2. 查询带上下文的术语
        val departmentTerm = termService.getTerm("department", "science")
        assertNotNull(departmentTerm, "应该能找到department术语")
        assertEquals("理学院", departmentTerm.term, "术语值应该正确")

        // 3. 查询带语言的术语
        val degreeTerm = termService.getTerm("degree", null, "zh")
        assertNotNull(degreeTerm, "应该能找到degree术语")
        assertEquals("学士学位", degreeTerm.term, "术语值应该正确")

        // 4. 查询完整术语（中文）
        val majorTermZh = termService.getTerm("major", "computer_science", "zh")
        assertNotNull(majorTermZh, "应该能找到中文的major术语")
        assertEquals("计算机科学与技术", majorTermZh.term, "中文术语值应该正确")

        // 5. 查询完整术语（英文）
        val majorTermEn = termService.getTerm("major", "computer_science", "en")
        assertNotNull(majorTermEn, "应该能找到英文的major术语")
        assertEquals("Computer Science and Technology", majorTermEn.term, "英文术语值应该正确")

        // 测试批量查询
        val fields = listOf("university", "department", "degree")
        val termsMap = termService.getByFields(fields)
        assertEquals(3, termsMap.size, "应该返回3个术语")
        assertTrue(termsMap.containsKey("university"), "应包含university术语")
        assertTrue(termsMap.containsKey("department"), "应包含department术语")
        assertTrue(termsMap.containsKey("degree"), "应包含degree术语")

        // 测试按语言查询
        val zhTerms = termService.getByLanguage("zh")
        assertTrue(zhTerms.isNotEmpty(), "应该有中文术语")
        assertTrue(zhTerms.any { it.field == "degree" }, "中文术语应包含degree")
        assertTrue(zhTerms.any { it.field == "major" }, "中文术语应包含major")

        // 测试分页查询
        val page1 = termService.getPage(1, 3)
        assertTrue(page1.isNotEmpty(), "第一页应该有数据")
        assertTrue(page1.size <= 3, "每页最多3条数据")

        // 测试更新术语
        val termToUpdate = universityTerm.toDTO().copy(
            term = "高等学府"
        )
        val updateResult = termService.update(termToUpdate)
        assertTrue(updateResult, "更新应该成功")

        // 验证更新结果
        val updatedTerm = termService.getTerm("university")
        assertNotNull(updatedTerm, "应该能找到更新后的术语")
        assertEquals("高等学府", updatedTerm.term, "术语值应该已更新")

        logger.info("术语测试完成，共插入${terms.size}个术语")
    }
}
