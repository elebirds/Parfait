import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.20"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.runtime") version "1.13.1"
    application
}

group = "moe.hhm.parfait"
version = "1.0.0"

val exposedVersion = "0.60.0"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    // Exposed (Kotlin SQL框架)
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    // 数据库驱动
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
    implementation("com.mysql:mysql-connector-j:8.2.0")
    // 协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.1")
    // Koin 依赖注入框架
    implementation("io.insert-koin:koin-core:4.0.3")
    // 日志
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    // 文档生成
    implementation("com.deepoove:poi-tl:1.12.2")
    // 拼音英文转换
    implementation("com.belerweb:pinyin4j:2.5.1")
    // 测试
    testImplementation(kotlin("test"))
}

application {
    mainClass = "moe.hhm.parfait.ParfaitAppKt"
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

runtime {
    imageZip.set(project.file("${project.buildDir}/image-zip/parfait-image.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    modules.set(listOf("java.desktop", "java.naming", "java.sql"))

    jpackage {
        imageName = "Parfait"
        imageOptions = listOf()
        skipInstaller = false
        installerName = "Parfait"
        installerType = "msi"
        installerOptions = listOf("--win-menu", "--win-shortcut")
    }
}