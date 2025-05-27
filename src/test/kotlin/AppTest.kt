import moe.hhm.parfait.app.service.GpaStandardService
import moe.hhm.parfait.di.appModule
import moe.hhm.parfait.di.domainModule
import moe.hhm.parfait.di.infrastructureModule
import moe.hhm.parfait.di.presentationModule
import moe.hhm.parfait.utils.VersionUtils
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppTest : KoinTest {

    @BeforeAll
    fun setup() {
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
    }

    @Test
    fun `测试版本信息可以正确获取`() {
        val versionInfo = VersionUtils.getFullInfo()
        assertNotNull(versionInfo)
    }

    @Test
    fun `测试Koin依赖注入正常工作`() {
        // 简单测试确保Koin依赖注入正常工作
        val allDefinitions = getKoin().getAll<GpaStandardService>()
        assertNotNull(allDefinitions)
        assert(allDefinitions.isNotEmpty())
    }
} 