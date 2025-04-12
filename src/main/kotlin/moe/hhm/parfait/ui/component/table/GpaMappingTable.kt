/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.table

import moe.hhm.parfait.dto.SingleGpaMapping
import moe.hhm.parfait.ui.component.table.model.GpaMappingTableModel
import java.text.DecimalFormat
import java.text.NumberFormat
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.text.DefaultFormatterFactory
import javax.swing.text.NumberFormatter

/**
 * GPA绩点映射表格
 * 处理绩点映射的编辑功能
 */
class GpaMappingTable : JTable() {
    private val tableModel = GpaMappingTableModel()

    init {
        model = tableModel
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        autoResizeMode = AUTO_RESIZE_ALL_COLUMNS

        setupRenderers()
        setupEditors()
        setupColumnWidths()
    }

    /**
     * 设置表格单元格渲染器
     */
    private fun setupRenderers() {
        // 设置居中对齐渲染器
        val centerRenderer = DefaultTableCellRenderer()
        centerRenderer.horizontalAlignment = SwingConstants.CENTER
        for (i in 0 until columnCount) {
            columnModel.getColumn(i).cellRenderer = centerRenderer
        }
    }

    /**
     * 设置表格单元格编辑器
     */
    private fun setupEditors() {
        // Integer编辑器 - 用于分数范围
        val intFormat = NumberFormat.getIntegerInstance().apply {
            isGroupingUsed = false
        }
        val intFormatter = NumberFormatter(intFormat).apply {
            valueClass = Integer::class.java
            minimum = 0
            maximum = 101
        }
        val intEditorFactory = DefaultFormatterFactory(intFormatter)

        // Double编辑器 - 用于绩点
        val doubleFormat = DecimalFormat("0.0#").apply {
            isGroupingUsed = false
        }
        val doubleFormatter = NumberFormatter(doubleFormat).apply {
            valueClass = java.lang.Double::class.java
        }
        val doubleEditorFactory = DefaultFormatterFactory(doubleFormatter)

        // 设置各列的编辑器
        val intEditor = JFormattedTextField(intEditorFactory)
        val doubleEditor = JFormattedTextField(doubleEditorFactory)

        // 左边界列
        getColumnModel().getColumn(1).cellEditor = DefaultCellEditor(intEditor)
        // 右边界列
        getColumnModel().getColumn(2).cellEditor = DefaultCellEditor(intEditor)
        // 绩点列
        getColumnModel().getColumn(3).cellEditor = DefaultCellEditor(doubleEditor)

        // 让表格更好地处理默认的JTextField（等级列）编辑器
        getColumnModel().getColumn(0).cellEditor = DefaultCellEditor(JTextField())
    }

    /**
     * 设置列宽
     */
    private fun setupColumnWidths() {
        getColumnModel().getColumn(0).preferredWidth = 100 // 等级
        getColumnModel().getColumn(1).preferredWidth = 120 // 左边界
        getColumnModel().getColumn(2).preferredWidth = 120 // 右边界
        getColumnModel().getColumn(3).preferredWidth = 100 // 绩点
    }

    /**
     * 设置表格数据
     */
    fun setMappingData(data: List<SingleGpaMapping>) {
        tableModel.setMappingData(data)
    }

    /**
     * 获取表格数据
     */
    fun getMappingData(): List<SingleGpaMapping> {
        return tableModel.getMappingData()
    }

    /**
     * 添加一行
     */
    fun addRow(mapping: SingleGpaMapping = SingleGpaMapping("", 0..0, 0.0)) {
        tableModel.addRow(mapping)
        changeSelection(tableModel.rowCount - 1, 0, false, false)
    }

    /**
     * 删除当前选中行
     */
    fun removeSelectedRow(): Boolean {
        val selectedRow = selectedRow
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow)

            // 移除后调整选择
            if (tableModel.rowCount > 0) {
                val newSelection = if (selectedRow < tableModel.rowCount) selectedRow else tableModel.rowCount - 1
                changeSelection(newSelection, 0, false, false)
            }
            return true
        }
        return false
    }

    /**
     * 将当前选中行上移
     */
    fun moveSelectedRowUp(): Boolean {
        val selectedRow = selectedRow
        if (selectedRow > 0) {
            tableModel.swapRows(selectedRow, selectedRow - 1)
            changeSelection(selectedRow - 1, 0, false, false)
            return true
        }
        return false
    }

    /**
     * 将当前选中行下移
     */
    fun moveSelectedRowDown(): Boolean {
        val selectedRow = selectedRow
        if (selectedRow != -1 && selectedRow < tableModel.rowCount - 1) {
            tableModel.swapRows(selectedRow, selectedRow + 1)
            changeSelection(selectedRow + 1, 0, false, false)
            return true
        }
        return false
    }

    /**
     * 判断是否可以上移当前选中行
     */
    fun canMoveUp(): Boolean {
        return selectedRow > 0
    }

    /**
     * 判断是否可以下移当前选中行
     */
    fun canMoveDown(): Boolean {
        return selectedRow != -1 && selectedRow < tableModel.rowCount - 1
    }

    /**
     * 判断是否有选中行
     */
    fun hasSelection(): Boolean {
        return selectedRow != -1
    }
} 