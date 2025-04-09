/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component

import com.formdev.flatlaf.FlatClientProperties
import com.formdev.flatlaf.extras.FlatSVGIcon
import moe.hhm.parfait.view.action.DatabaseAction
import net.miginfocom.swing.MigLayout
import javax.swing.*

class WelcomePanel : JPanel() {
    private val standaloneButton = JButton(
        "Continue in standalone mode",
        FlatSVGIcon("ui/nwicons/standalone.svg", 0.063f)
    ).apply {
        this.putClientProperty(
            FlatClientProperties.STYLE,
            "focusWidth:0;" + "font:+1;"
        )
        this.addActionListener {
            DatabaseAction.openStandaloneChooser()
        }
    }
    private val cmdSignIn: JButton = object : JButton("Sign in", FlatSVGIcon("ui/nwicons/next.svg")) {
        override fun isDefaultButton(): Boolean {
            return true
        }
    }.apply {
        this.putClientProperty(
            FlatClientProperties.STYLE,
            "foreground:#FFFFFF;" + "iconTextGap:10;"
        )
        this.setHorizontalTextPosition(JButton.LEADING)
        this.addActionListener {
            JOptionPane.showMessageDialog(this, "别急，联机版本还没写完:)", "Parfait", JOptionPane.INFORMATION_MESSAGE)
        }
    }
    private val txtAddress = JTextField().apply {
        this.putClientProperty(
            FlatClientProperties.STYLE,
            "iconTextGap:10;"
        )
        this.putClientProperty(
            FlatClientProperties.PLACEHOLDER_TEXT,
            "Enter your database address (e.g. localhost:3306)"
        )
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
        this.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username")
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
        this.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password")
        this.putClientProperty(
            FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            FlatSVGIcon("ui/nwicons/password.svg", 0.35f)
        )
    }


    init {
        init()
    }

    private fun init() {
        setLayout(MigLayout("wrap,gapy 3", "[fill,300]"))

        add(JLabel(FlatSVGIcon("ui/nwicons/logo.svg", 1.5f)))

        val titleLabel = JLabel("Welcome back, un peu de Parfait?", JLabel.CENTER).apply {
            putClientProperty(FlatClientProperties.STYLE, "font:bold +15;")
        }
        add(titleLabel, "gapy 8 8")

        add(JLabel("Sign in to access to your dashboard!", JLabel.CENTER))

        add(standaloneButton, "gapy 15 10")

        val lbSeparator = JLabel("Or connect to a MySQL database in online mode").apply {
            putClientProperty(
                FlatClientProperties.STYLE,
                "foreground:\$Label.disabledForeground;" + "font:-1;"
            )
        }

        // sizegroup to make group component are same size
        add(createSeparator(), "split 3,sizegroup g1")
        add(lbSeparator, "sizegroup g1")
        add(createSeparator(), "sizegroup g1")

        val lbAddress = JLabel("Address").apply {
            putClientProperty(FlatClientProperties.STYLE, "font:bold;")
        }
        add(lbAddress, "gapy 10 5")
        add(txtAddress)

        val lbUser = JLabel("User").apply {
            putClientProperty(FlatClientProperties.STYLE, "font:bold;")
        }
        add(lbUser, "gapy 10 5")
        add(txtUser)

        val lbPassword = JLabel("Password").apply {
            putClientProperty(FlatClientProperties.STYLE, "font:bold;")
        }
        add(lbPassword, "gapy 10 5,split 2")
        val cmdForgotPassword = createNoBorderButton("Forgot Password ?")
        add(cmdForgotPassword, "grow 0,gapy 10 5")

        add(txtPassword)

        add(JCheckBox("Remember for 30 days"), "gapy 10 10")
        add(cmdSignIn, "gapy n 10")

        val lbNoAccount = JLabel("No account ?")
        lbNoAccount.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "foreground:\$Label.disabledForeground;"
        )
        add(lbNoAccount, "split 2,gapx push n")

        val cmdCreateAccount = createNoBorderButton("Create an account")

        add(cmdCreateAccount, "gapx n push")
    }

    private fun createSeparator(): JSeparator {
        val separator = JSeparator()
        separator.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "stripeIndent:8;"
        )
        return separator
    }

    private fun createNoBorderButton(text: String): JButton {
        val button = JButton(text)
        button.putClientProperty(
            FlatClientProperties.STYLE, "" +
                    "foreground:\$Component.accentColor;" +
                    "margin:1,5,1,5;" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;" +
                    "background:null;"
        )
        return button
    }
}