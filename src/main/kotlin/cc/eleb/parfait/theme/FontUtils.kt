package cc.eleb.parfait.theme

import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.extras.FlatAnimatedLafChange
import com.formdev.flatlaf.util.FontUtils
import java.awt.Component
import java.awt.Font
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.util.*
import javax.swing.*

object FontUtils {
    val fontMenu = JMenu()
    val availableFontFamilyNames: Array<String> = FontUtils.getAvailableFontFamilyNames().clone()
    var initialFontMenuItemCount: Int = -1

    fun init(){
        Arrays.sort(availableFontFamilyNames)
        //======== fontMenu ========
        fontMenu.text = "字体"
        val restoreFontMenuItem = JMenuItem()
        val incrFontMenuItem = JMenuItem()
        val decrFontMenuItem = JMenuItem()
        //---- restoreFontMenuItem ----
        restoreFontMenuItem.text = "还原字体"
        restoreFontMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_0,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        restoreFontMenuItem.addActionListener { restoreFont() }
        fontMenu.add(restoreFontMenuItem)

        //---- incrFontMenuItem ----
        incrFontMenuItem.text = "加大字体"
        incrFontMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_PLUS,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        incrFontMenuItem.addActionListener { incrFont() }
        fontMenu.add(incrFontMenuItem)

        //---- decrFontMenuItem ----
        decrFontMenuItem.text = "减小字体"
        decrFontMenuItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_MINUS,
            Toolkit.getDefaultToolkit().menuShortcutKeyMask
        )
        decrFontMenuItem.addActionListener { decrFont() }
        fontMenu.add(decrFontMenuItem)
        this.updateFontMenuItems()
    }

    fun fontFamilyChanged(e: ActionEvent) {
        val fontFamily: String = e.actionCommand
        FlatAnimatedLafChange.showSnapshot()
        val font: Font = UIManager.getFont("defaultFont")
        val newFont: Font = FontUtils.getCompositeFont(fontFamily, font.style, font.size)
        UIManager.put("defaultFont", newFont)
        FlatLaf.updateUI()
        FlatAnimatedLafChange.hideSnapshotWithAnimation()
    }

    fun fontSizeChanged(e: ActionEvent) {
        val fontSizeStr: String = e.actionCommand
        val font: Font = UIManager.getFont("defaultFont")
        val newFont: Font = font.deriveFont(fontSizeStr.toInt().toFloat())
        UIManager.put("defaultFont", newFont)
        FlatLaf.updateUI()
    }

    private fun restoreFont() {
        UIManager.put("defaultFont", null)
        updateFontMenuItems()
        FlatLaf.updateUI()
    }

    private fun incrFont() {
        val font: Font = UIManager.getFont("defaultFont")
        val newFont: Font = font.deriveFont((font.size + 1).toFloat())
        UIManager.put("defaultFont", newFont)
        updateFontMenuItems()
        FlatLaf.updateUI()
    }

    private fun decrFont() {
        val font: Font = UIManager.getFont("defaultFont")
        val newFont: Font = font.deriveFont((font.size - 1).coerceAtLeast(10).toFloat())
        UIManager.put("defaultFont", newFont)
        updateFontMenuItems()
        FlatLaf.updateUI()
    }

    fun updateFontMenuItems() {
        if (initialFontMenuItemCount < 0) initialFontMenuItemCount = fontMenu.itemCount else {
            // remove old font items
            for (i in fontMenu.itemCount - 1 downTo initialFontMenuItemCount) fontMenu.remove(i)
        }

        // get current font
        val currentFont: Font = UIManager.getFont("Label.font")
        val currentFamily: String = currentFont.family
        val currentSize: String = currentFont.size.toString()

        // add font families
        fontMenu.addSeparator()
        val families: ArrayList<String> = ArrayList(
            mutableListOf(
                "Arial", "Cantarell", "Comic Sans MS", "DejaVu Sans", "Dialog", "Inter", "Liberation Sans",
                "Noto Sans", "Open Sans", "Roboto", "SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana"
            )
        )
        if (!families.contains(currentFamily)) families.add(currentFamily)
        families.sortWith(java.lang.String.CASE_INSENSITIVE_ORDER)
        val familiesGroup = ButtonGroup()
        for (family: String in families) {
            if (Arrays.binarySearch(availableFontFamilyNames, family) < 0) continue  // not available
            val item = JCheckBoxMenuItem(family)
            item.isSelected = (family == currentFamily)
            item.addActionListener { e: ActionEvent -> fontFamilyChanged(e) }
            fontMenu.add(item)
            familiesGroup.add(item)
        }

        // add font sizes
        fontMenu.addSeparator()
        val sizes: ArrayList<String> = ArrayList(mutableListOf("10", "11", "12", "14", "16", "18", "20", "24", "28"))
        if (!sizes.contains(currentSize)) sizes.add(currentSize)
        sizes.sortWith(java.lang.String.CASE_INSENSITIVE_ORDER)
        val sizesGroup = ButtonGroup()
        for (size: String in sizes) {
            val item = JCheckBoxMenuItem(size)
            item.isSelected = (size == currentSize)
            item.addActionListener { e: ActionEvent -> fontSizeChanged(e) }
            fontMenu.add(item)
            sizesGroup.add(item)
        }

        // enabled/disable items
        val enabled: Boolean = UIManager.getLookAndFeel() is FlatLaf
        for (item: Component in fontMenu.menuComponents) item.isEnabled = enabled
    }
}