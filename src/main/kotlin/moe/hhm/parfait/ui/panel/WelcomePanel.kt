/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.ui.panel

import com.formdev.flatlaf.FlatClientProperties
import com.formdev.flatlaf.extras.FlatSVGIcon
import moe.hhm.parfait.infra.db.DatabaseConnectionConfig
import moe.hhm.parfait.infra.db.DatabaseFactory
import moe.hhm.parfait.infra.i18n.I18nUtils
import moe.hhm.parfait.infra.i18n.I18nUtils.bindText
import moe.hhm.parfait.infra.i18n.I18nUtils.createButton
import moe.hhm.parfait.infra.i18n.I18nUtils.createCheckBox
import moe.hhm.parfait.infra.i18n.I18nUtils.createLabel
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
            val address = txtAddress.text
            val port = 3306 // 可以添加一个端口输入框或从地址中解析
            val user = txtUser.text
            val password = String(txtPassword.password)
            val databaseName = txtDatabaseName.text

            try {
                // 创建在线模式连接配置
                val config = DatabaseConnectionConfig.online(
                    host = address,
                    port = port, 
                    user = user,
                    password = password,
                    databaseName = databaseName
                )
                
                // 尝试连接数据库
                DatabaseFactory.connect(config)
                
                // 连接成功后可以切换到主界面
                // ...
                
            } catch (e: Exception) {
                // 显示连接错误
                JOptionPane.showMessageDialog(
                    this,
                    "连接失败: ${e.message}",
                    "连接错误",
                    JOptionPane.ERROR_MESSAGE
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

    init {
        init()
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
        add(lbPassword, "gapy 10 5,split 2")
        val cmdForgotPassword = createNoBorderButton("button.forgot")
        add(cmdForgotPassword, "grow 0,gapy 10 5")

        add(txtPassword)
        

        add(createCheckBox("welcome.remember"), "gapy 10 10")
        add(cmdSignIn, "gapy n 10")

        val lbNoAccount = createLabel("welcome.noaccount").apply {
            putClientProperty(FlatClientProperties.STYLE, "foreground:\$Label.disabledForeground;")
        }
        add(lbNoAccount, "split 2,gapx push n")

        val cmdCreateAccount = createNoBorderButton("button.create")

        add(cmdCreateAccount, "gapx n push")
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