/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.table

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import moe.hhm.parfait.dto.TermDTO
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import moe.hhm.parfait.ui.viewmodel.TermViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel

/**
 * 术语表格组件
 */
class TermTable(parent: CoroutineComponent? = null) : JTable(), KoinComponent,
    CoroutineComponent by DefaultCoroutineComponent(parent) {
    private val viewModel: TermViewModel by inject()

    // 自定义表格模型，可编辑单元格
    private val tableModel = object : DefaultTableModel() {
        override fun isCellEditable(row: Int, column: Int): Boolean {
            // 只允许编辑字段名、上下文、语言和术语值列
            return column in 1..4
        }
    }

    // 保存术语数据的列表，用于获取选中的术语信息
    private var termList: List<TermDTO> = emptyList()

    // 表格列名称
    private val columnKeys = listOf("UUID", "字段", "上下文", "语言", "术语值")

    init {
        // 设置表格模型和基本属性
        model = tableModel
        preferredViewportSize = Dimension(900, 600)
        autoResizeMode = AUTO_RESIZE_SUBSEQUENT_COLUMNS
        selectionModel.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        rowHeight = 25

        // 设置UUID列不可见，但保留以便进行数据操作
        // 初始化表格列
        initColumns()

        // 表格行选择监听器
        this.selectionModel.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                viewModel.setSelectedTerms(getSelectedTerms())
            }
        }

        // 添加双击编辑功能
        this.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    val row = rowAtPoint(e.point)
                    val col = columnAtPoint(e.point)
                    if (row >= 0 && col in 1..4) {
                        // 开始编辑单元格
                        editCellAt(row, col)
                    }
                }
            }
        })

        // 添加单元格编辑完成监听器
        this.putClientProperty("terminateEditOnFocusLost", true)
        this.tableModel.addTableModelListener { event ->
            if (event.type == javax.swing.event.TableModelEvent.UPDATE) {
                val row = event.firstRow
                val col = event.column

                if (row >= 0 && col in 1..4 && row < termList.size) {
                    val term = termList[row]
                    val newValue = tableModel.getValueAt(row, col).toString()

                    // 更新术语数据
                    val updatedTerm = when (col) {
                        1 -> term.copy(field = newValue)
                        2 -> term.copy(context = if (newValue.isBlank()) null else newValue)
                        3 -> term.copy(language = if (newValue.isBlank()) null else newValue)
                        4 -> term.copy(term = newValue)
                        else -> term
                    }

                    if (updatedTerm != term) {
                        viewModel.updateTerm(updatedTerm)
                    }
                }
            }
        }
    }

    override fun observer() {
        // 监听表格数据更新
        scope.launch {
            viewModel.data.collectLatest { terms ->
                // 更新表格数据
                this@TermTable.updateData(terms)
            }
        }
    }

    // 初始化表格列
    private fun initColumns() {
        tableModel.setColumnIdentifiers(columnKeys.toTypedArray())
        
        // 设置列宽
        columnModel.getColumn(0).preferredWidth = 0  // UUID列（隐藏）
        columnModel.getColumn(1).preferredWidth = 150 // 字段名
        columnModel.getColumn(2).preferredWidth = 150 // 上下文
        columnModel.getColumn(3).preferredWidth = 80  // 语言
        columnModel.getColumn(4).preferredWidth = 300 // 术语值
        
        // 隐藏UUID列
        columnModel.getColumn(0).minWidth = 0
        columnModel.getColumn(0).maxWidth = 0
        columnModel.getColumn(0).preferredWidth = 0
    }

    // 将术语DTO对象转换为表格行数据
    private fun termToRow(term: TermDTO): Array<Any?> {
        return arrayOf(
            term.uuid,
            term.field,
            term.context ?: "",
            term.language ?: "",
            term.term
        )
    }

    // 更新表格数据
    fun updateData(terms: List<TermDTO>) {
        // 保存术语列表引用
        termList = terms

        // 清空现有数据
        tableModel.rowCount = 0

        // 添加新数据
        terms.forEach { term ->
            tableModel.addRow(termToRow(term))
        }
    }

    // 获取当前选中的所有术语
    fun getSelectedTerms(): List<TermDTO> {
        return selectedRows.map { row ->
            if (row >= 0 && row < termList.size) {
                termList[row]
            } else {
                null
            }
        }.filterNotNull()
    }

    // 获取当前选中的术语
    fun getSelectedTerm(): TermDTO? {
        val selectedTerms = getSelectedTerms()
        return if (selectedTerms.isNotEmpty()) selectedTerms[0] else null
    }

    // 获取当前选中的术语UUID列表
    fun getSelectedTermUUIDs(): List<UUID> {
        return getSelectedTerms().mapNotNull { it.uuid }
    }

    // 选择指定术语的行
    fun selectTerm(term: TermDTO?) {
        if (term == null) {
            clearSelection()
            return
        }

        // 查找术语在列表中的索引
        val index = termList.indexOfFirst { it.uuid == term.uuid }
        if (index >= 0) {
            // 选择找到的行
            setRowSelectionInterval(index, index)
            // 确保选中的行可见
            scrollRectToVisible(getCellRect(index, 0, true))
        } else {
            // 未找到匹配的术语，清除选择
            clearSelection()
        }
    }

    // 选择多个术语的行
    fun selectTerms(terms: List<TermDTO>) {
        clearSelection()

        if (terms.isEmpty()) return

        terms.forEach { term ->
            // 查找术语在列表中的索引
            val index = termList.indexOfFirst { it.uuid == term.uuid }
            if (index >= 0) {
                // 添加到选择
                addRowSelectionInterval(index, index)
                // 确保第一个选中的行可见
                if (terms.first() == term) {
                    scrollRectToVisible(getCellRect(index, 0, true))
                }
            }
        }
    }
} 