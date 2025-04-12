/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.table.model

import moe.hhm.parfait.dto.SingleGpaMapping
import moe.hhm.parfait.infra.i18n.I18nUtils
import javax.swing.table.AbstractTableModel

/**
 * GPA映射表格模型
 */
class GpaMappingTableModel : AbstractTableModel() {
    private val columnNames = arrayOf(
        I18nUtils.getText("gpa.mapping.property.level"),
        I18nUtils.getText("gpa.mapping.property.left"),
        I18nUtils.getText("gpa.mapping.property.right"),
        I18nUtils.getText("gpa.mapping.property.gpa")
    )
    private val mappingData = mutableListOf<SingleGpaMapping>()

    /**
     * 设置映射数据
     */
    fun setMappingData(data: List<SingleGpaMapping>) {
        mappingData.clear()
        mappingData.addAll(data)
        fireTableDataChanged()
    }

    /**
     * 获取映射数据
     */
    fun getMappingData(): List<SingleGpaMapping> {
        return mappingData.toList()
    }

    /**
     * 添加一行
     */
    fun addRow(mapping: SingleGpaMapping) {
        mappingData.add(mapping)
        fireTableRowsInserted(mappingData.size - 1, mappingData.size - 1)
    }

    /**
     * 删除一行
     */
    fun removeRow(rowIndex: Int) {
        if (rowIndex in 0 until mappingData.size) {
            mappingData.removeAt(rowIndex)
            fireTableRowsDeleted(rowIndex, rowIndex)
        }
    }

    /**
     * 交换两行
     */
    fun swapRows(row1: Int, row2: Int) {
        if (row1 in 0 until mappingData.size && row2 in 0 until mappingData.size) {
            val temp = mappingData[row1]
            mappingData[row1] = mappingData[row2]
            mappingData[row2] = temp
            fireTableRowsUpdated(row1.coerceAtMost(row2), row1.coerceAtLeast(row2))
        }
    }

    override fun getRowCount(): Int = mappingData.size

    override fun getColumnCount(): Int = columnNames.size

    override fun getColumnName(column: Int): String = columnNames[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val mapping = mappingData[rowIndex]
        return when (columnIndex) {
            0 -> mapping.first
            1 -> mapping.second.first
            2 -> mapping.second.last + 1
            3 -> mapping.third
            else -> ""
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

    override fun setValueAt(value: Any, rowIndex: Int, columnIndex: Int) {
        val mapping = mappingData[rowIndex]

        try {
            when (columnIndex) {
                0 -> {
                    // 更新等级 - 处理String类型
                    val grade = value.toString()
                    val newMapping = SingleGpaMapping(grade, mapping.second, mapping.third)
                    mappingData[rowIndex] = newMapping
                }

                1 -> {
                    // 更新左边界 - 处理Int类型或String类型
                    val start = when (value) {
                        is Int -> value
                        else -> value.toString().toInt()
                    }
                    val end = mapping.second.last
                    if (start <= end) {
                        val newMapping = SingleGpaMapping(mapping.first, start..end, mapping.third)
                        mappingData[rowIndex] = newMapping
                    }
                }

                2 -> {
                    // 更新右边界 - 处理Int类型或String类型
                    // UI显示为不包含上界,但在存储时使用包含上界,所以需要-1
                    val end = when (value) {
                        is Int -> value - 1
                        else -> value.toString().toInt() - 1
                    }
                    val start = mapping.second.first
                    if (start <= end) {
                        val newMapping = SingleGpaMapping(mapping.first, start..end, mapping.third)
                        mappingData[rowIndex] = newMapping
                    }
                }

                3 -> {
                    // 更新绩点 - 处理Double类型或String类型
                    val gpa = when (value) {
                        is Double -> value
                        else -> value.toString().toDouble()
                    }
                    if (gpa >= 0) {
                        val newMapping = SingleGpaMapping(mapping.first, mapping.second, gpa)
                        mappingData[rowIndex] = newMapping
                    }
                }
            }
        } catch (e: Exception) {
            // 记录异常但不中断操作
            println("编辑单元格时出错: ${e.message}")
        }

        fireTableCellUpdated(rowIndex, columnIndex)
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return when (columnIndex) {
            0 -> String::class.java
            1, 2 -> Int::class.java
            3 -> Double::class.java
            else -> String::class.java
        }
    }
} 