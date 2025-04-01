package cc.eleb.parfait.ui.panel

import cc.eleb.parfait.i18n.trs
import cc.eleb.parfait.ui.ParfaitFrame
import cc.eleb.parfait.utils.cast
import net.miginfocom.swing.MigLayout
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import java.io.IOException
import javax.swing.JLabel
import javax.swing.JPanel


class WelcomePanel : JPanel() {
    fun reloadTranslation() {
        label1.text = "welcome-panel-1".trs()
        label2.text = "welcome-panel-2".trs()
    }

    private fun initComponents() {
        this.reloadTranslation()
        layout = MigLayout("hidemode 3", "[fill][fill]", "[][][]")
        add(label1, "cell 0 0")
        add(label2, "cell 0 1")
        DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, object : DropTargetAdapter() {
            override fun drop(dtde: DropTargetDropEvent) {
                try {
                    val tr = dtde.transferable
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE)
                        val list = dtde.transferable
                            .getTransferData(DataFlavor.javaFileListFlavor) as List<*>
                        ParfaitFrame.instance.openFile(list[0].cast())
                        dtde.dropComplete(true)
                        updateUI()
                    } else {
                        dtde.rejectDrop()
                    }
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                } catch (ufe: UnsupportedFlavorException) {
                    ufe.printStackTrace()
                }
            }
        })
    }

    private var label1 = JLabel().apply {
        this.putClientProperty("FlatLaf.styleClass", "h0")
    }
    private var label2 = JLabel().apply {
        this.putClientProperty("FlatLaf.styleClass", "h1")
    }

    init {
        initComponents()
    }
}