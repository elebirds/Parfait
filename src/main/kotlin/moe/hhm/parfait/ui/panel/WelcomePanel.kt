/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.panel

import com.formdev.flatlaf.FlatClientProperties
import com.formdev.flatlaf.extras.FlatSVGIcon
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.bindText
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.infra.i18n.I18nUtils.createCheckBox
import moe.hhm.parfait.infra.i18n.I18nUtils.createLabel
import moe.hhm.parfait.infra.persist.LoginPrefsManager
import moe.hhm.parfait.ui.action.DatabaseAction
import net.miginfocom.swing.MigLayout
import javax.swing.*

class WelcomePanel : JPanel() {
    private val standaloneButton =
        createButton("button.standalone", FlatSVGIcon("ui/nwicons/standalone.svg", 0.063f)).apply {
            this.putClientProperty(
                FlatClientProperties.STYLE,
                "focusWidth:0;" + "font:+1;"
            )
            this.addActionListener {
                DatabaseAction.openStandaloneChooser()
            }
        }
    private val cmdSignIn: JButton = object : JButton() {
        override fun isDefaultButton(): Boolean {
            return true
        }

        init {
            icon = FlatSVGIcon("ui/nwicons/next.svg")
        }
    }.apply {
        bindText(this, "button.signin")
        this.putClientProperty(
            FlatClientProperties.STYLE,
            "foreground:#FFFFFF;" + "iconTextGap:10;"
        )
        this.setHorizontalTextPosition(JButton.LEADING)
        this.addActionListener {
            // 连接数据库
            val success = DatabaseAction.connectOnline(
                address = txtAddress.text,
                user = txtUser.text,
                password = String(txtPassword.password),
                databaseName = txtDatabaseName.text
            )

            // 如果连接成功，保存登录信息
            if (success) {
                LoginPrefsManager.saveLoginInfo(
                    address = txtAddress.text,
                    database = txtDatabaseName.text,
                    username = txtUser.text,
                    password = String(txtPassword.password),
                    remember = rememberCheckBox.isSelected
                )
            }
        }
    }
    private val txtAddress = JTextField().apply {
        this.putClientProperty(
            FlatClientProperties.STYLE,
            "iconTextGap:10;"
        )
        I18nUtils.bindProperty(this, "form.placeholder.address") { c, v ->
            this.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, v)
        }
        this.putClientProperty(
            FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            FlatSVGIcon("ui/nwicons/host.svg", 0.068f)
        )
    }
    private val txtUser = JTextField().apply {
        this.putClientProperty(
            FlatClientProperties.STYLE,
            "iconTextGap:10;"
        )
        I18nUtils.bindProperty(this, "form.placeholder.user") { c, v ->
            this.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, v)
        }
        this.putClientProperty(
            FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            FlatSVGIcon("ui/nwicons/user.svg", 0.068f)
        )
    }

    private val txtPassword = JPasswordField().apply {
        this.putClientProperty(
            FlatClientProperties.STYLE,
            "iconTextGap:10;" + "showRevealButton:true;"
        )
        I18nUtils.bindProperty(this, "form.placeholder.password") { c, v ->
            this.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, v)
        }
        this.putClientProperty(
            FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            FlatSVGIcon("ui/nwicons/password.svg", 0.35f)
        )
    }

    private val txtDatabaseName = JTextField().apply {
        this.putClientProperty(
            FlatClientProperties.STYLE,
            "iconTextGap:10;"
        )
        I18nUtils.bindProperty(this, "form.placeholder.database") { c, v ->
            this.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, v)
        }
        this.putClientProperty(
            FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            FlatSVGIcon("ui/nwicons/database.svg", 0.068f)
        )
    }

    // 记住密码复选框
    private val rememberCheckBox = createCheckBox("welcome.remember").apply {
        addActionListener {
            // 如果取消勾选，可以选择清除已保存的凭据
            if (!isSelected) {
                // 可选：添加确认对话框，询问是否要清除已保存的凭据
                // 此处简化处理，不添加确认步骤
            }
        }
    }

    init {
        init()
        // 尝试加载保存的登录信息
        loadSavedLoginInfo()
    }

    private fun init() {
        setLayout(MigLayout("wrap,gapy 3", "[fill,300]"))

        add(JLabel(FlatSVGIcon("ui/nwicons/logo.svg", 1.5f)))

        val titleLabel = createLabel("welcome.title", JLabel.CENTER).apply {
            putClientProperty(FlatClientProperties.STYLE, "font:bold +15;")
        }
        add(titleLabel, "gapy 8 8")

        add(createLabel("welcome.message", JLabel.CENTER))

        add(standaloneButton, "gapy 15 10")

        val lbSeparator = createLabel("welcome.separator").apply {
            putClientProperty(
                FlatClientProperties.STYLE,
                "foreground:\$Label.disabledForeground;" + "font:-1;"
            )
        }

        // sizegroup to make group component are same size
        add(createSeparator(), "split 3,sizegroup g1")
        add(lbSeparator, "sizegroup g1")
        add(createSeparator(), "sizegroup g1")

        val lbAddress = createLabel("form.address").apply {
            putClientProperty(FlatClientProperties.STYLE, "font:bold;")
        }
        add(lbAddress, "gapy 10 5")
        add(txtAddress)

        val lbDatabaseName = createLabel("form.databaseName").apply {
            putClientProperty(FlatClientProperties.STYLE, "font:bold;")
        }
        add(lbDatabaseName, "gapy 10 5")
        add(txtDatabaseName)

        val lbUser = createLabel("form.user").apply {
            putClientProperty(FlatClientProperties.STYLE, "font:bold;")
        }
        add(lbUser, "gapy 10 5")
        add(txtUser)


        val lbPassword = createLabel("form.password").apply {
            putClientProperty(FlatClientProperties.STYLE, "font:bold;")
        }
        add(lbPassword, "gapy 10 5")
        //val cmdForgotPassword = createNoBorderButton("button.forgot")
        //add(cmdForgotPassword, "grow 0,gapy 10 5")

        add(txtPassword)

        // 使用记住密码复选框
        add(rememberCheckBox, "gapy 10 10")
        add(cmdSignIn, "gapy n 10")

        val lbNoAccount = createLabel("welcome.needAdvice").apply {
            putClientProperty(FlatClientProperties.STYLE, "foreground:\$Label.disabledForeground;")
        }
        add(lbNoAccount, "split 2,gapx push n")

        val cmdCreateAccount = createNoBorderButton("welcome.advice").apply {
            addActionListener {
                JOptionPane.showMessageDialog(
                    null,
                    I18nUtils.getText("database.mode.tip"),
                    I18nUtils.getText("welcome.advice"),
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        }

        add(cmdCreateAccount, "gapx n push")
    }

    /**
     * 从保存的首选项中加载登录信息
     */
    private fun loadSavedLoginInfo() {
        val loginInfo = LoginPrefsManager.getLoginInfo()

        // 如果有保存的登录信息，则填充表单
        if (loginInfo != null) {
            txtAddress.text = loginInfo.address
            txtDatabaseName.text = loginInfo.database
            txtUser.text = loginInfo.username
            txtPassword.text = loginInfo.password
            rememberCheckBox.isSelected = loginInfo.remember
        }
    }

    private fun createSeparator(): JSeparator = JSeparator().apply {
        putClientProperty(FlatClientProperties.STYLE, "stripeIndent:8;")
    }

    private fun createNoBorderButton(key: String): JButton = createButton(key).apply {
        putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "foreground:\$Component.accentColor;" +
                    "margin:1,5,1,5;" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;" +
                    "background:null;"
        )
    }

}