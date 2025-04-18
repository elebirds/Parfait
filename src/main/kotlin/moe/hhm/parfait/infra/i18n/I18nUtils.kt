/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.infra.i18n

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import java.awt.Component
import java.awt.Dialog
import java.awt.Frame
import java.awt.Window
import java.lang.ref.WeakReference
import javax.swing.*

/**
 * 国际化工具类
 *
 * 提供将 Swing 组件国际化的工具方法
 */
object I18nUtils {
    // 应用级UI线程协程作用域 - 应用程序生命周期内有效
    private val appUiScope = CoroutineScope(Dispatchers.Swing)

    // 全局语言更新Job
    private var languageUpdateJob: Job? = null

    /**
     * 组件更新器接口
     *
     * 定义任何可以随语言更新的项目
     */
    interface I18nUpdater {
        /**
         * 当语言变化时更新
         */
        fun update()

        /**
         * 检查是否仍有效
         */
        fun isValid(): Boolean
    }

    /**
     * 组件属性更新器
     *
     * @param component 组件弱引用
     * @param key 国际化资源键
     * @param updater 更新函数，接收组件和文本
     */
    private class ComponentSinglePropertyUpdater<T : Component>(
        private val component: WeakReference<T>,
        private val key: String,
        private val updater: (T, String) -> Unit
    ) : I18nUpdater {
        override fun update() {
            component.get()?.let { comp ->
                updater(comp, I18nManager.getMessage(key))
            }
        }

        override fun isValid(): Boolean = component.get() != null
    }

    /**
     * 组件列表更新器
     *
     * 用于更新多个组件的属性
     */
    private class ComponentMultiplePropertyUpdater<T : Component>(
        private val components: WeakReference<T>,
        private val keys: List<String>,
        private val updater: (T, List<String>) -> Unit
    ) : I18nUpdater {
        override fun update() {
            components.get()?.let { comp ->
                updater(comp, I18nManager.getMessages(keys))
            }
        }

        override fun isValid(): Boolean = components.get() != null
    }

    // 所有注册的更新器
    private val updaters = mutableListOf<I18nUpdater>()

    fun addUpdater(updater: I18nUpdater) {
        updaters.add(updater)
    }

    /**
     * 初始化国际化支持
     */
    fun init() {
        I18nManager.init()
        setupLanguageMonitor()
    }

    /**
     * 设置语言监视器
     */
    private fun setupLanguageMonitor() {
        languageUpdateJob?.cancel()

        languageUpdateJob = appUiScope.launch {
            I18nManager.currentLanguage.onEach {
                updateAllComponents()
            }.collect()
        }
    }

    /**
     * 更新所有组件
     */
    private fun updateAllComponents() {
        // 过滤掉无效的更新器
        updaters.removeAll { !it.isValid() }

        // 更新所有有效组件
        updaters.forEach { it.update() }
    }

    /**
     * 绑定组件属性到国际化键
     *
     * 通用方法，可以绑定任何组件的任何属性
     *
     * @param component 要绑定的组件
     * @param key 国际化资源键
     * @param updater 更新函数，指定如何将文本应用到组件
     */
    fun <T : Component> bindProperty(component: T, key: String, updater: (T, String) -> Unit) {
        // 先应用初始值
        updater(component, I18nManager.getMessage(key))

        // 创建更新器并添加到集合
        val propertyUpdater = ComponentSinglePropertyUpdater(WeakReference(component), key, updater)
        updaters.add(propertyUpdater)
    }

    /**
     * 绑定组件属性到国际化键
     *
     * 通用方法，可以绑定任何组件的任何属性
     *
     * @param component 要绑定的组件
     * @param keys 国际化资源键列表
     * @param updater 更新函数，指定如何将文本应用到组件
     */
    fun <T : Component> bindProperty(component: T, keys: List<String>, updater: (T, List<String>) -> Unit) {
        // 先应用初始值
        updater(component, I18nManager.getMessages(keys))

        // 创建更新器并添加到集合
        val propertyUpdater = ComponentMultiplePropertyUpdater(WeakReference(component), keys, updater)
        updaters.add(propertyUpdater)
    }

    // 常用绑定函数的便捷方法

    /**
     * 绑定组件文本属性
     */
    fun bindText(component: JComponent, key: String) {
        when (component) {
            is JLabel -> bindProperty(component, key) { c, text -> c.text = text }
            is AbstractButton -> bindProperty(component, key) { c, text -> c.text = text }
            is JTextField -> bindProperty(component, key) { c, text -> c.text = text }
            is JTextArea -> bindProperty(component, key) { c, text -> c.text = text }
            else -> throw IllegalArgumentException("Unsupported component type for text binding: ${component.javaClass.simpleName}")
        }
    }

    /**
     * 绑定工具提示文本
     */
    fun bindToolTipText(component: JComponent, key: String) {
        bindProperty(component, key) { c, text -> c.toolTipText = text }
    }

    /**
     * 绑定窗口标题
     */
    fun bindTitle(window: Window, key: String) {
        bindProperty(window, key) { w, text ->
            when (w) {
                is JFrame -> w.title = text
                is JDialog -> w.title = text
                is Frame -> w.title = text
                is Dialog -> w.title = text
            }
        }
    }

    // 创建常用组件的便捷方法

    /**
     * 创建带有国际化文本的标签
     */
    fun createLabel(key: String, horizontalAlignment: Int = JLabel.LEADING): JLabel {
        val label = JLabel("", null, horizontalAlignment)
        bindText(label, key)
        return label
    }

    /**
     * 创建带有国际化文本的按钮
     */
    fun createButton(key: String, icon: Icon? = null): JButton {
        val button = JButton(icon)
        bindText(button, key)
        return button
    }

    /**
     * 创建带有国际化文本的菜单
     */
    fun createMenu(key: String): JMenu {
        val menu = JMenu()
        bindText(menu, key)
        return menu
    }

    /**
     * 创建带有国际化文本的菜单项
     */
    fun createMenuItem(key: String): JMenuItem {
        val menuItem = JMenuItem()
        bindText(menuItem, key)
        return menuItem
    }

    /**
     * 创建带有国际化文本的复选框
     */
    fun createCheckBox(key: String): JCheckBox {
        val checkBox = JCheckBox()
        bindText(checkBox, key)
        return checkBox
    }

    /**
     * 获取国际化文本
     */
    fun getText(key: String): String = I18nManager.getMessage(key)

    /**
     * 获取带格式的国际化文本
     */
    fun getFormattedText(key: String, vararg args: Any): String =
        I18nManager.getMessageFormatted(key, *args)
}