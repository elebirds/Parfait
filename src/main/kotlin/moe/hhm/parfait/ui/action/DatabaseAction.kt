/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.action

import moe.hhm.parfait.exception.BusinessException
import moe.hhm.parfait.infra.db.DatabaseConnectionConfig
import moe.hhm.parfait.infra.db.DatabaseExportImport
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.infra.db.ImportExportOption
import moe.hhm.parfait.infra.db.STANDALONE_DB_SUFFIX
import moe.hhm.parfait.infra.i18n.I18nUtils
import java.io.File
import javax.swing.JCheckBox
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.filechooser.FileFilter
import net.miginfocom.swing.MigLayout

object DatabaseAction {
    fun openStandaloneChooser() {
        val fc = JFileChooser().apply {
            fileFilter = object : FileFilter() {
                override fun accept(f: File): Boolean {
                    return f.isDirectory || f.name.endsWith(".pardb")
                }

                override fun getDescription(): String = "Parfait Standalone Data File (*.pardb)"
            }
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
        }
        if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return

        // 确保文件名以 .pardb 结尾
        var path = fc.selectedFile.absolutePath
        if (!fc.selectedFile.absolutePath.endsWith(STANDALONE_DB_SUFFIX)) path += STANDALONE_DB_SUFFIX
        val file = File(path)
        if (!file.canWrite() || !file.canRead()) {
            throw BusinessException("database.connect.error.permissionDenied", path)
        }
        DatabaseFactory.connect(DatabaseConnectionConfig.standalone(path))
    }

    fun validateOnlineParams(
        address: String,
        user: String,
        password: String,
        databaseName: String
    ) {
        // 检测地址是否合法
        val a = address.split(":")
        if (a.size != 2) {
            throw IllegalArgumentException(I18nUtils.getFormattedText("database.connect.error.invalidAddress", a))
        }
        // IP 地址是否合法，需要支持域名,localhost,127.0.0.1等
        val ip = a[0]
        if (!ip.matches(Regex("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) && !ip.matches(Regex("[a-zA-Z0-9.-]+"))) {
            throw IllegalArgumentException(I18nUtils.getFormattedText("database.connect.error.illegalIp", ip))
        }
        // 端口是否合法
        val port = a[1].toInt()
        if (port < 0 || port > 65535) {
            throw IllegalArgumentException(I18nUtils.getFormattedText("database.connect.error.invalidPort", port))
        }
        // 数据库名是否合法
        if (databaseName.isEmpty()) {
            throw IllegalArgumentException(
                I18nUtils.getFormattedText(
                    "database.connect.error.invalidDatabaseName",
                    databaseName
                )
            )
        }

        // 用户名是否合法
        if (user.isEmpty()) {
            throw IllegalArgumentException(I18nUtils.getText("database.connect.error.invalidUserName"))
        }
        // 密码允许为空
    }

    /**
     * 连接到在线数据库
     *
     * @param address 服务器地址，格式为 "host:port"
     * @param user 用户名
     * @param password 密码
     * @param databaseName 数据库名称
     * @return 连接是否成功
     */
    fun connectOnline(
        address: String,
        user: String,
        password: String,
        databaseName: String
    ): Boolean {
        try {
            validateOnlineParams(address, user, password, databaseName)
            val a = address.split(":")
            val config = DatabaseConnectionConfig.online(
                host = a[0],
                port = a[1].toInt(),
                user = user,
                password = password,
                databaseName = databaseName
            )
            DatabaseFactory.connect(config)
            return true  // 连接成功
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                null,
                I18nUtils.getFormattedText("database.connect.error.msg", e.localizedMessage),
                I18nUtils.getText("database.connect.error.title"),
                JOptionPane.ERROR_MESSAGE
            )
            return false  // 连接失败
        }
    }
    
    /**
     * 导出数据库到文件
     */
    fun exportDatabase() {
        // 检查是否支持导出操作
        val (canExport, reason) = DatabaseExportImport.canExport()
        if (!canExport) {
            JOptionPane.showMessageDialog(
                null,
                reason ?: I18nUtils.getText("database.export.error.unknown"),
                I18nUtils.getText("database.export.error.title"),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        
        val fc = JFileChooser().apply {
            fileFilter = object : FileFilter() {
                override fun accept(f: File): Boolean {
                    return f.isDirectory || f.name.endsWith(".pardb")
                }

                override fun getDescription(): String = "Parfait Standalone Data File (*.pardb)"
            }
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
            dialogTitle = I18nUtils.getText("database.export.title")
        }
        
        if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return
        
        // 确保文件名以 .pardb 结尾
        var path = fc.selectedFile.absolutePath
        if (!path.endsWith(STANDALONE_DB_SUFFIX)) path += STANDALONE_DB_SUFFIX
        val file = File(path)
        
        // 如果文件已存在，确认是否覆盖
        if (file.exists()) {
            val result = JOptionPane.showConfirmDialog(
                null,
                I18nUtils.getFormattedText("database.export.overwrite.msg", file.name),
                I18nUtils.getText("database.export.overwrite.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            )
            if (result != JOptionPane.YES_OPTION) return
        }
        
        // 执行导出
        val success = DatabaseExportImport.exportDatabase(file)
        
        // 显示结果
        if (success) {
            JOptionPane.showMessageDialog(
                null,
                I18nUtils.getFormattedText("database.export.success.msg", file.absolutePath),
                I18nUtils.getText("database.export.success.title"),
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }
    
    /**
     * 从文件导入数据库
     */
    fun importDatabase() {
        // 检查是否支持导入操作
        val (canImport, reason) = DatabaseExportImport.canImport()
        if (!canImport) {
            JOptionPane.showMessageDialog(
                null,
                reason ?: I18nUtils.getText("database.import.error.unknown"),
                I18nUtils.getText("database.import.error.title"),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        
        val fc = JFileChooser().apply {
            fileFilter = object : FileFilter() {
                override fun accept(f: File): Boolean {
                    return f.isDirectory || f.name.endsWith(".pardb")
                }

                override fun getDescription(): String = "Parfait Standalone Data File (*.pardb)"
            }
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
            dialogTitle = I18nUtils.getText("database.import.title")
        }
        
        if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return
        
        val file = File(fc.selectedFile.absolutePath)
        
        // 确认文件格式
        if (!file.name.endsWith(STANDALONE_DB_SUFFIX)) {
            JOptionPane.showMessageDialog(
                null,
                I18nUtils.getText("database.import.error.invalidFile"),
                I18nUtils.getText("database.import.error.title"),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        
        // 选择导入选项
        val optionsPanel = createImportOptionsPanel()
        
        val result = JOptionPane.showConfirmDialog(
            null,
            optionsPanel,
            I18nUtils.getText("database.import.options.title"),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        )
        
        if (result != JOptionPane.OK_OPTION) return
        
        // 获取选中的选项
        val selectedOptions = getSelectedOptions(optionsPanel)
        
        if (selectedOptions.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                I18nUtils.getText("database.import.error.noOptions"),
                I18nUtils.getText("database.import.error.title"),
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        
        // 确认是否继续
        val confirmResult = JOptionPane.showConfirmDialog(
            null,
            I18nUtils.getText("database.import.confirm.msg"),
            I18nUtils.getText("database.import.confirm.title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        )
        
        if (confirmResult != JOptionPane.YES_OPTION) return
        
        // 执行导入
        val success = DatabaseExportImport.importDatabase(file, selectedOptions)
        
        // 显示结果
        if (success) {
            JOptionPane.showMessageDialog(
                null,
                I18nUtils.getFormattedText("database.import.success.msg", file.absolutePath),
                I18nUtils.getText("database.import.success.title"),
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }
    
    /**
     * 创建导入选项面板
     */
    private fun createImportOptionsPanel(): JPanel {
        val panel = JPanel(MigLayout("wrap 1", "[fill]"))
        
        // 添加说明文本
        panel.add(javax.swing.JLabel(I18nUtils.getText("database.import.options.description")))
        
        // 添加"全部导入"选项
        val allCheckBox = JCheckBox(I18nUtils.getText(ImportExportOption.ALL.i18nKey))
        panel.add(allCheckBox)
        
        // 添加单独选项
        val optionCheckBoxes = ImportExportOption.values()
            .filter { it != ImportExportOption.ALL }
            .map { option ->
                JCheckBox(I18nUtils.getText(option.i18nKey)).apply {
                    isEnabled = true
                    panel.add(this)
                }
            }
        
        // 设置"全部导入"的动作
        allCheckBox.addActionListener {
            val selected = allCheckBox.isSelected
            optionCheckBoxes.forEach { it.isSelected = selected }
            optionCheckBoxes.forEach { it.isEnabled = !selected }
        }
        
        return panel
    }
    
    /**
     * 获取选中的选项
     */
    private fun getSelectedOptions(panel: JPanel): List<ImportExportOption> {
        val checkBoxes = panel.components.filterIsInstance<JCheckBox>()
        
        // 如果选择了"全部导入"，则返回ALL
        if (checkBoxes.first().isSelected) {
            return listOf(ImportExportOption.ALL)
        }
        
        // 否则返回选中的选项
        return checkBoxes.drop(1).mapIndexedNotNull { index, checkBox ->
            if (checkBox.isSelected) {
                ImportExportOption.values().filter { it != ImportExportOption.ALL }[index]
            } else {
                null
            }
        }
    }
}