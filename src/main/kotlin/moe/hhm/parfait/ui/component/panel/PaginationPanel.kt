/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.component.panel

import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.infra.i18n.I18nUtils.createLabel
import moe.hhm.parfait.ui.base.CoroutineComponent
import moe.hhm.parfait.ui.base.DefaultCoroutineComponent
import net.miginfocom.swing.MigLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

/**
 * 通用分页面板
 * 
 * @param parent 父协程组件
 * @param pageSizeOptions 页大小选项
 * @param defaultPageSize 默认页大小
 * @param onFirstPage 点击首页按钮回调
 * @param onPrevPage 点击上一页按钮回调
 * @param onNextPage 点击下一页按钮回调
 * @param onLastPage 点击末页按钮回调
 * @param onPageSizeChange 页大小改变回调
 */
open class PaginationPanel(
    parent: CoroutineComponent? = null,
    pageSizeOptions: List<Int> = listOf(10, 20, 50, 100),
    defaultPageSize: Int = 20,
    private var onFirstPage: () -> Unit = {},
    private var onPrevPage: () -> Unit = {},
    private var onNextPage: () -> Unit = {},
    private var onLastPage: () -> Unit = {},
    private var onPageSizeChange: (Int) -> Unit = {}
) : JPanel(), CoroutineComponent by DefaultCoroutineComponent(parent) {

    private val buttonFirstPage: JButton = createButton("page.first").apply {
        addActionListener { onFirstPage() }
    }
    private val buttonPrevPage: JButton = createButton("page.previous").apply {
        addActionListener { onPrevPage() }
    }
    private val buttonNextPage: JButton = createButton("page.next").apply {
        addActionListener { onNextPage() }
    }
    private val buttonLastPage: JButton = createButton("page.last").apply {
        addActionListener { onLastPage() }
    }
    private val labelCurrentPage: JLabel = JLabel("1")
    private val labelTotalPages: JLabel = JLabel("/ 1")
    private val labelPageSize: JLabel = createLabel("page.eachPage")
    private val comboPageSize: JComboBox<Int> = JComboBox<Int>().apply {
        pageSizeOptions.forEach { addItem(it) }
        selectedItem = defaultPageSize
        addActionListener {
            val size = selectedItem as Int
            onPageSizeChange(size)
        }
    }

    init {
        this.layout = MigLayout("insets 0", "[][][][][][][][]", "[]")
        this.add(labelPageSize, "cell 0 0")
        this.add(comboPageSize, "cell 1 0")
        this.add(buttonFirstPage, "cell 2 0")
        this.add(buttonPrevPage, "cell 3 0")
        this.add(labelCurrentPage, "cell 4 0,alignx center")
        this.add(labelTotalPages, "cell 5 0")
        this.add(buttonNextPage, "cell 6 0")
        this.add(buttonLastPage, "cell 7 0")
        this.border = EmptyBorder(5, 0, 5, 0)
    }

    /**
     * 设置回调函数
     */
    fun setCallbacks(
        onFirstPage: () -> Unit = {},
        onPrevPage: () -> Unit = {},
        onNextPage: () -> Unit = {},
        onLastPage: () -> Unit = {},
        onPageSizeChange: (Int) -> Unit = {}
    ) {
        this.onFirstPage = onFirstPage
        this.onPrevPage = onPrevPage
        this.onNextPage = onNextPage
        this.onLastPage = onLastPage
        this.onPageSizeChange = onPageSizeChange
        
        // 更新按钮事件监听器
        buttonFirstPage.actionListeners.forEach { buttonFirstPage.removeActionListener(it) }
        buttonPrevPage.actionListeners.forEach { buttonPrevPage.removeActionListener(it) }
        buttonNextPage.actionListeners.forEach { buttonNextPage.removeActionListener(it) }
        buttonLastPage.actionListeners.forEach { buttonLastPage.removeActionListener(it) }
        comboPageSize.actionListeners.forEach { comboPageSize.removeActionListener(it) }
        
        buttonFirstPage.addActionListener { onFirstPage() }
        buttonPrevPage.addActionListener { onPrevPage() }
        buttonNextPage.addActionListener { onNextPage() }
        buttonLastPage.addActionListener { onLastPage() }
        comboPageSize.addActionListener { 
            val size = comboPageSize.selectedItem as Int
            onPageSizeChange(size)
        }
    }

    /**
     * 更新UI状态
     *
     * @param connected 是否已连接
     * @param currentPage 当前页码
     * @param totalPages 总页数
     */
    fun updateState(connected: Boolean, currentPage: Int, totalPages: Int) {
        // 设置分页按钮状态
        buttonFirstPage.isEnabled = connected && currentPage > 1
        buttonPrevPage.isEnabled = connected && currentPage > 1
        buttonNextPage.isEnabled = connected && currentPage < totalPages
        buttonLastPage.isEnabled = connected && currentPage < totalPages
        comboPageSize.isEnabled = connected

        // 更新当前页码和总页码
        labelCurrentPage.text = currentPage.toString()
        labelTotalPages.text = "/ $totalPages"
    }
    
    /**
     * 设置页大小
     */
    fun setPageSize(pageSize: Int) {
        comboPageSize.selectedItem = pageSize
    }
    
    /**
     * 观察者方法，用于子类覆盖
     */
    override fun observer() {
        // 默认空实现，由子类覆盖
    }
} 