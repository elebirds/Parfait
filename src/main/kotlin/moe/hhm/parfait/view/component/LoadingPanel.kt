package moe.hhm.parfait.view.component

import com.formdev.flatlaf.FlatClientProperties
import com.formdev.flatlaf.extras.FlatSVGIcon
import net.miginfocom.swing.MigLayout
import javax.swing.JLabel
import javax.swing.JPanel

class LoadingPanel : JPanel() {
    private val loadingIcon = JLabel(FlatSVGIcon("ui/nwicons/loading.svg", 0.5f)).apply {
        putClientProperty(FlatClientProperties.STYLE, "foreground:\$Component.accentColor;")
    }
    private val loadingText = JLabel("正在连接数据库...").apply {
        putClientProperty(FlatClientProperties.STYLE, "font:bold +2;")
    }

    init {
        layout = MigLayout("wrap,al center center", "[center]")
        add(loadingIcon, "gapy 20 10")
        add(loadingText)
    }
} 