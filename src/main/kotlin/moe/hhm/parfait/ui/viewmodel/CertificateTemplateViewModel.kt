/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moe.hhm.parfait.app.service.CertificateDataService
import moe.hhm.parfait.app.service.CertificateTemplateService
import moe.hhm.parfait.dto.CertificateContentType
import moe.hhm.parfait.dto.CertificateDataDTO
import moe.hhm.parfait.dto.CertificateTemplateDTO
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.ui.state.VMState
import moe.hhm.parfait.ui.viewmodel.common.DataViewModel
import moe.hhm.parfait.ui.viewmodel.common.VMErrorHandlerChooser
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.util.*
import javax.swing.JOptionPane

class CertificateTemplateViewModel : DataViewModel<List<CertificateTemplateDTO>>(emptyList()), KoinComponent {
    private val templateService: CertificateTemplateService by inject()
    private val dataService: CertificateDataService by inject()

    // 选中的证明模板
    private val _selectedTemplate = MutableStateFlow<CertificateTemplateDTO?>(null)
    val selectedTemplate: StateFlow<CertificateTemplateDTO?> = _selectedTemplate.asStateFlow()

    // JAR资源文件列表
    private val _jarResources = MutableStateFlow<List<String>>(emptyList())
    val jarResources: StateFlow<List<String>> = _jarResources.asStateFlow()

    init {
        // 监听数据库连接状态
        observeDatabaseConnectionState()
        // 加载JAR资源文件列表
        loadJarResources()
    }

    /**
     * 监听数据库连接状态
     */
    private fun observeDatabaseConnectionState() {
        scope.launch {
            DatabaseFactory.connectionState.collectLatest { state ->
                when (state) {
                    is DatabaseConnectionState.Connected -> {
                        logger.info("数据库已连接，准备加载证明模板数据")
                        _vmState.value = VMState.PRELOADING
                        loadData()
                    }

                    is DatabaseConnectionState.Disconnected -> {
                        logger.info("数据库已断开连接")
                        _vmState.value = VMState.DISCONNECTED
                        _selectedTemplate.value = null
                    }

                    is DatabaseConnectionState.Connecting -> _vmState.value = VMState.CONNECTING
                }
            }
        }
    }

    /**
     * 加载JAR资源文件列表
     */
    private fun loadJarResources() {
        // 扫描jar中的certificate目录
        val certificateFiles = mutableListOf<String>()

        try {
            // 默认添加内置模板
            certificateFiles.add("score_default.docx")

            // 获取资源URL
            val resourceUrl = this::class.java.classLoader.getResource("certificate")

            if (resourceUrl != null) {
                // 如果在JAR中运行
                if (resourceUrl.protocol == "jar") {
                    val jarConnection = resourceUrl.openConnection() as java.net.JarURLConnection
                    val jarFile = jarConnection.jarFile

                    // 遍历JAR文件中的所有条目
                    val entries = jarFile.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        val entryName = entry.name

                        // 检查是否是certificate目录下的文件
                        if (entryName.startsWith("certificate/") && !entry.isDirectory) {
                            // 移除前缀并添加到列表
                            val filename = entryName.substringAfter("certificate/")
                            if (filename.isNotEmpty() && !certificateFiles.contains(filename)) {
                                certificateFiles.add(filename)
                            }
                        }
                    }
                } else {
                    // 如果在文件系统中运行
                    val certificateDir = File(resourceUrl.toURI())
                    if (certificateDir.isDirectory) {
                        certificateDir.listFiles()?.forEach { file ->
                            if (file.isFile && !certificateFiles.contains(file.name)) {
                                certificateFiles.add(file.name)
                            }
                        }
                    }
                }
            }

            // 更新状态
            _jarResources.value = certificateFiles
        } catch (e: Exception) {
            // 记录错误但不中断程序
            println("加载JAR资源文件列表失败: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * 加载所有证明模板
     */
    override fun loadData() = suspendProcessWithErrorHandling(VMErrorHandlerChooser.LoadData) {
        // 设置加载状态为加载中
        _vmState.value = VMState.LOADING
        // 从服务加载所有证明模板
        val templatesList = templateService.getCertificates()
        _data.value = templatesList
        // 设置加载状态为已加载
        _vmState.value = VMState.DONE
        true
    }

    /**
     * 选择一个证明模板
     */
    fun selectTemplate(template: CertificateTemplateDTO) {
        _selectedTemplate.value = template
    }

    suspend fun updateDataState(contentPath: String, state: Boolean) {
        if (!contentPath.startsWith("db::")) return
        // 更新数据状态
        val uuidStr = contentPath.substringAfter("db::")
        val uuid = UUID.fromString(uuidStr)
        dataService.updateUsed(uuid, state)
    }

    /**
     * 添加证明模板
     */
    fun addTemplate(template: CertificateTemplateDTO) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
        // 添加新的证明模板
        _vmState.value = VMState.PROCESSING
        templateService.add(template)
        updateDataState(template.contentPath, true)
        true
    }

    /**
     * 删除证明模板
     */
    fun deleteTemplate(uuid: UUID) = suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
        _vmState.value = VMState.PROCESSING
        val result = templateService.delete(uuid)
        // 如果删除的是当前选中的模板，则清空选中
        if (result && _selectedTemplate.value?.uuid == uuid) {
            _selectedTemplate.value = null
        }
        result
    }

    /**
     * 更新证明模板
     */
    fun updateTemplate(template: CertificateTemplateDTO) =
        suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
            _vmState.value = VMState.PROCESSING
            // 更新证明模板
            templateService.update(template)
        }

    fun updateTemplateAndPath(template: CertificateTemplateDTO, beforeContentPath: String) =
        suspendProcessWithErrorHandling(VMErrorHandlerChooser.Modify) {
            _vmState.value = VMState.PROCESSING
            // 更新证明模板
            val res = templateService.update(template)
            if (res) {
                updateDataState(beforeContentPath, false)
                updateDataState(template.contentPath, true)
            }
            res
        }

    /**
     * 切换证明模板的收藏状态
     * 只有当模板处于活跃状态时才能被收藏
     */
    fun toggleLikeTemplate(template: CertificateTemplateDTO) {
        // 检查模板是否处于活跃状态
        if (!template.isActive && !template.isLike) {
            // 如果模板未激活，且要设置为收藏，则显示错误提示
            JOptionPane.showMessageDialog(
                null,
                I18nUtils.getText("certificate.error.inactive.like"),
                I18nUtils.getText("error.business.title"),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }

        // 创建一个新的模板对象，切换isLike状态
        val updatedTemplate = template.copy(isLike = !template.isLike)
        // 更新模板
        updateTemplate(updatedTemplate)
    }

    /**
     * 切换证明模板的活跃状态
     * 如果要设置为非活跃状态，则同时取消收藏
     */
    fun toggleActiveTemplate(template: CertificateTemplateDTO) {
        // 如果要将模板设置为非活跃状态，则同时取消收藏
        val newActiveState = !template.isActive
        val newLikeState = if (newActiveState) template.isLike else false

        // 创建一个新的模板对象，切换状态
        val updatedTemplate = template.copy(
            isActive = newActiveState,
            isLike = newLikeState
        )
        // 更新模板
        updateTemplate(updatedTemplate)
    }

    /**
     * 将文件上传到数据库
     * @param file 要上传的文件
     * @return 返回数据库中的UUID标识符，格式为"db::UUID"
     */
    suspend fun uploadFileToDb(file: File): String {
        _vmState.value = VMState.PROCESSING

        // 读取文件内容
        val fileData = Files.readAllBytes(file.toPath())

        // 创建CertificateDataDTO对象并保存
        val dataDTO = CertificateDataDTO(
            uuid = null,
            filename = file.name,
            data = fileData
        )

        // 添加到数据库
        val entityId = dataService.add(dataDTO)

        // 返回数据库引用路径
        _vmState.value = VMState.DONE
        return CertificateContentType.generatePath(CertificateContentType.DATABASE, entityId.value.toString())
    }

    /**
     * 从内容路径获取输入流
     * @param contentPath 内容路径
     * @return 输入流对象
     */
    suspend fun getContentStream(contentPath: String): InputStream? {
        return when {
            contentPath.startsWith("jar::") -> {
                // 从JAR资源获取
                val resourcePath = contentPath.substringAfter("jar::")
                Thread.currentThread().contextClassLoader.getResourceAsStream("certificate/$resourcePath")
            }

            contentPath.startsWith("db::") -> {
                // 从数据库获取
                val uuidStr = contentPath.substringAfter("db::")
                val uuid = UUID.fromString(uuidStr)
                val data = dataService.getByUUID(uuid)
                data?.data?.inputStream()
            }

            else -> {
                // 从本地文件获取
                try {
                    File(contentPath).inputStream()
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}