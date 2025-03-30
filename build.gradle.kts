plugins {
    kotlin("jvm") version "2.1.10"
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "moe.eleb.parafit"
version = "1.0-SNAPSHOT"

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
    // 连接池
    implementation("com.zaxxer:HikariCP:5.0.1")

    // 依赖注入
    implementation("io.insert-koin:koin-core")

    // 日志
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    // 工具库
    implementation("org.apache.commons:commons-lang3:3.12.0")

    // CSV处理
    implementation("com.opencsv:opencsv:5.7.1")
    // PDF处理
    implementation("com.itextpdf:itext7-core:7.2.5")

    // 测试
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("io.insert-koin:koin-test:3.4.0")
    testImplementation("io.insert-koin:koin-test-junit5:3.4.0")
    testImplementation("com.h2database:h2:2.1.214")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}