/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.action

import moe.hhm.parfait.infra.db.DatabaseConnectionConfig
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.infra.db.STANDALONE_DB_SUFFIX
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

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
        DatabaseFactory.connect(DatabaseConnectionConfig.standalone(path))
    }
}