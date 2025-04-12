/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.viewmodel

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moe.hhm.parfait.domain.model.certificate.CertificateTerm
import moe.hhm.parfait.dto.CertificateTermDTO
import moe.hhm.parfait.infra.db.DatabaseConnectionState
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.ui.state.TermLoadState
import moe.hhm.parfait.ui.state.TermPaginationState
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.math.ceil

/**
 * 术语视图模型
 */
class TermViewModel : BaseViewModel() {
    // 日志
    private val logger = LoggerFactory.getLogger(this::class.java)

    // 术语列表数据状态
    private val _terms = MutableStateFlow<List<CertificateTermDTO>>(emptyList())
    val terms: StateFlow<List<CertificateTermDTO>> = _terms.asStateFlow()

    // 分页状态
    private val _paginationState = MutableStateFlow(TermPaginationState())
    val paginationState: StateFlow<TermPaginationState> = _paginationState.asStateFlow()

    // 加载状态
    private val _loadState = MutableStateFlow(TermLoadState.DISCONNECTED)
    val loadState: StateFlow<TermLoadState> = _loadState.asStateFlow()

    // 当前选中的多个术语
    private val _selectedTerms = MutableStateFlow<List<CertificateTermDTO>>(emptyList())
    val selectedTerms: StateFlow<List<CertificateTermDTO>> = _selectedTerms.asStateFlow()

    init {
        // 监听数据库连接状态
        observeDatabaseConnectionState()
    }

    /**
     * 监听数据库连接状态
     */
    private fun observeDatabaseConnectionState() {
        scope.launch {
            DatabaseFactory.connectionState.collectLatest { state ->
                when (state) {
                    is DatabaseConnectionState.Connected -> {
                        logger.info("数据库已连接，准备加载术语数据")
                        _loadState.value = TermLoadState.PRELOADING
                        loadData()
                    }

                    is DatabaseConnectionState.Disconnected -> {
                        logger.info("数据库已断开连接")
                        _loadState.value = TermLoadState.DISCONNECTED
                        _terms.value = emptyList()
                        _paginationState.value = TermPaginationState()
                        _selectedTerms.value = emptyList()
                    }

                    is DatabaseConnectionState.Connecting -> _loadState.value = TermLoadState.CONNECTING
                }
            }
        }
    }

    /**
     * 加载术语数据
     */
    fun loadData() {
        // 检查数据库是否已连接
        if (TermLoadState.PRELOADING != _loadState.value) {
            logger.error("非法加载数据，当前状态：${_loadState.value.name}")
            return
        }

        scope.launch {
            try {
                // 设置加载状态
                _loadState.value = TermLoadState.LOADING

                // 保存当前选中的术语ID，用于后续恢复选中状态
                val selectedTermIds = _selectedTerms.value.mapNotNull { it.uuid }

                // 在事务中获取数据
                val (terms, totalCount) = transaction {
                    // 获取总术语数量
                    val count = CertificateTerm.count()
                    
                    // 计算分页参数
                    val pageSize = _paginationState.value.pageSize
                    val offset = (_paginationState.value.currentPage - 1) * pageSize
                    
                    // 查询当前页的术语
                    val termsData = CertificateTerm.all()
                        .offset(offset.toLong())
                        .limit(pageSize)
                        .map { term ->
                            CertificateTermDTO(
                                uuid = term.id.value,
                                key = term.key,
                                term = term.term
                            )
                        }
                    
                    Pair(termsData, count)
                }

                // 计算总页数
                val totalPages = calculateTotalPages(totalCount, _paginationState.value.pageSize)

                // 更新分页状态
                _paginationState.update {
                    it.copy(
                        // 如果当前页数大于总页数，重置为第一页
                        currentPage = if (_paginationState.value.currentPage > totalPages) 1 else _paginationState.value.currentPage,
                        totalTerms = totalCount,
                        totalPages = totalPages
                    )
                }

                // 更新术语列表
                _terms.value = terms

                // 尝试在新数据中找回之前选中的术语
                if (selectedTermIds.isNotEmpty()) {
                    val selectedTerms = terms.filter { it.uuid in selectedTermIds }
                    _selectedTerms.value = selectedTerms
                }

                _loadState.value = TermLoadState.DONE
            } catch (e: Exception) {
                _loadState.value = TermLoadState.ERROR
                logger.error("加载术语数据失败", e)
                e.printStackTrace()
            }
        }
    }

    /**
     * 计算总页数
     */
    private fun calculateTotalPages(totalItems: Long, pageSize: Int): Int {
        return ceil(totalItems.toDouble() / pageSize).toInt().coerceAtLeast(1)
    }

    /**
     * 设置当前页码
     */
    fun setCurrentPage(page: Int) {
        if (page == _paginationState.value.currentPage) return
        _paginationState.update { it.copy(currentPage = page) }
        _loadState.value = TermLoadState.PRELOADING
        loadData()
    }

    /**
     * 设置页大小
     */
    fun setPageSize(size: Int) {
        if (size == _paginationState.value.pageSize) return
        _paginationState.update { it.copy(pageSize = size, currentPage = 1) }
        _loadState.value = TermLoadState.PRELOADING
        loadData()
    }

    /**
     * 转到第一页
     */
    fun firstPage() {
        setCurrentPage(1)
    }

    /**
     * 转到上一页
     */
    fun previousPage() {
        if (_paginationState.value.currentPage > 1) {
            setCurrentPage(_paginationState.value.currentPage - 1)
        }
    }

    /**
     * 转到下一页
     */
    fun nextPage() {
        if (_paginationState.value.currentPage < _paginationState.value.totalPages) {
            setCurrentPage(_paginationState.value.currentPage + 1)
        }
    }

    /**
     * 转到最后一页
     */
    fun lastPage() {
        setCurrentPage(_paginationState.value.totalPages)
    }

    /**
     * 设置选中的术语
     */
    fun setSelectedTerm(term: CertificateTermDTO?) {
        _selectedTerms.value = if (term == null) emptyList() else listOf(term)
    }

    /**
     * 设置选中的多个术语
     */
    fun setSelectedTerms(terms: List<CertificateTermDTO>) {
        _selectedTerms.value = terms
    }

    /**
     * 添加术语
     */
    fun addTerm(term: CertificateTermDTO) {
        if (_loadState.value != TermLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试添加术语")
            return
        }

        scope.launch {
            try {
                _loadState.value = TermLoadState.PROCESSING
                
                transaction {
                    CertificateTerm.new {
                        this.key = term.key
                        this.term = term.term
                    }
                }
                
                _loadState.value = TermLoadState.PRELOADING
                loadData()
            } catch (e: Exception) {
                _loadState.value = TermLoadState.ERROR
                logger.error("添加术语失败", e)
                e.printStackTrace()
            }
        }
    }

    /**
     * 更新术语
     */
    fun updateTerm(term: CertificateTermDTO) {
        if (_loadState.value != TermLoadState.DONE || term.uuid == null) {
            logger.warn("在未初始化完毕时尝试更新术语或UUID为空")
            return
        }

        scope.launch {
            try {
                _loadState.value = TermLoadState.PROCESSING
                
                transaction {
                    val termEntity = CertificateTerm.findById(term.uuid)
                    termEntity?.apply {
                        this.key = term.key
                        this.term = term.term
                    }
                }
                
                _loadState.value = TermLoadState.PRELOADING
                loadData()
            } catch (e: Exception) {
                _loadState.value = TermLoadState.ERROR
                logger.error("更新术语失败", e)
                e.printStackTrace()
            }
        }
    }

    /**
     * 删除术语
     */
    fun deleteTerm(uuid: UUID) {
        deleteTerms(listOf(uuid))
    }

    /**
     * 批量删除术语
     */
    fun deleteTerms(uuids: List<UUID>) {
        if (_loadState.value != TermLoadState.DONE) {
            logger.warn("在未初始化完毕时尝试删除术语")
            return
        }

        scope.launch {
            try {
                _loadState.value = TermLoadState.PROCESSING
                
                // 先从选中集合中移除
                uuids.forEach { uuid ->
                    val selectedUUIDs = _selectedTerms.value.mapNotNull { it.uuid }
                    if (uuid in selectedUUIDs) {
                        _selectedTerms.value = _selectedTerms.value.filter { it.uuid != uuid }
                    }
                }
                
                // 从数据库中删除
                transaction {
                    uuids.forEach { uuid ->
                        CertificateTerm.findById(uuid)?.delete()
                    }
                }
                
                _loadState.value = TermLoadState.PRELOADING
                loadData()
            } catch (e: Exception) {
                _loadState.value = TermLoadState.ERROR
                logger.error("删除术语失败", e)
                e.printStackTrace()
            }
        }
    }
} 