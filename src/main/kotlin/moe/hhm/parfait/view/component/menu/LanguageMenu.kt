/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component.menu

import moe.hhm.parfait.utils.i18n.I18nManager
import moe.hhm.parfait.utils.i18n.I18nUtils.bindText
import javax.swing.ButtonGroup
import javax.swing.JMenu
import javax.swing.JRadioButtonMenuItem

/**
 * 语言菜单
 *
 * 允许用户在不同语言之间切换
 */
class LanguageMenu : JMenu() {
    private val buttonGroup = ButtonGroup()

    init {
        // 设置菜单名称
        bindText(this, "menu.language")

        // 添加语言选项
        addLanguageItem(I18nManager.Language.ENGLISH)
        addLanguageItem(I18nManager.Language.CHINESE)

        // 设置当前语言选中
        updateSelectedLanguage(I18nManager.currentLanguage.value)
    }

    /**
     * 添加语言选项
     *
     * @param language 语言
     */
    private fun addLanguageItem(language: I18nManager.Language) {
        val key = when (language) {
            I18nManager.Language.ENGLISH -> "menu.language.english"
            I18nManager.Language.CHINESE -> "menu.language.chinese"
        }

        val item = JRadioButtonMenuItem()
        bindText(item, key)

        item.addActionListener {
            I18nManager.switchLanguage(language)
            updateSelectedLanguage(language)
        }

        buttonGroup.add(item)
        add(item)
    }

    /**
     * 更新选中的语言
     *
     * @param language 语言
     */
    private fun updateSelectedLanguage(language: I18nManager.Language) {
        // 遍历菜单项，设置当前语言选中
        for (i in 0 until itemCount) {
            val item = getItem(i) as JRadioButtonMenuItem
            item.isSelected = when (i) {
                0 -> language == I18nManager.Language.ENGLISH
                1 -> language == I18nManager.Language.CHINESE
                else -> false
            }
        }
    }
} 