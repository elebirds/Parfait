/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.eleb.parfait.ui

import net.miginfocom.swing.MigLayout
import java.awt.BorderLayout
import java.awt.Window
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.*

/**
 * @author Karl Tauber
 */
class NewDialog(owner: Window?) : JDialog(owner) {
    private fun okActionPerformed() {
        println("ok")
        dispose()
    }

    private fun cancelActionPerformed() {
        println("cancel")
        dispose()
    }

    private fun initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = JPanel()
        contentPanel = JPanel()
        label1 = JLabel()
        textField1 = JTextField()
        label3 = JLabel()
        comboBox2 = JComboBox()
        label2 = JLabel()
        comboBox1 = JComboBox()
        buttonBar = JPanel()
        okButton = JButton()
        cancelButton = JButton()
        menuBar1 = JMenuBar()
        menu1 = JMenu()
        menuItem8 = JMenuItem()
        menuItem7 = JMenuItem()
        menuItem6 = JMenuItem()
        menuItem5 = JMenuItem()
        menuItem4 = JMenuItem()
        menuItem3 = JMenuItem()
        menuItem2 = JMenuItem()
        menuItem1 = JMenuItem()
        menu2 = JMenu()
        menuItem18 = JMenuItem()
        menuItem17 = JMenuItem()
        menuItem16 = JMenuItem()
        menuItem15 = JMenuItem()
        menuItem14 = JMenuItem()
        menuItem13 = JMenuItem()
        menuItem12 = JMenuItem()
        menuItem11 = JMenuItem()
        menuItem10 = JMenuItem()
        menuItem9 = JMenuItem()
        menu3 = JMenu()
        menuItem25 = JMenuItem()
        menuItem26 = JMenuItem()
        menuItem24 = JMenuItem()
        menuItem23 = JMenuItem()
        menuItem22 = JMenuItem()
        menuItem21 = JMenuItem()
        menuItem20 = JMenuItem()
        menuItem19 = JMenuItem()
        popupMenu1 = JPopupMenu()
        cutMenuItem = JMenuItem()
        copyMenuItem = JMenuItem()
        pasteMenuItem = JMenuItem()

        //======== this ========
        title = "New"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        isModal = true
        val contentPane = contentPane
        contentPane.layout = BorderLayout()

        //======== dialogPane ========
        run {
            dialogPane!!.layout = BorderLayout()

            //======== contentPanel ========
            run {
                contentPanel!!.layout = MigLayout(
                    "insets dialog,hidemode 3",  // columns
                    "[fill]" +
                            "[grow,fill]",  // rows
                    "[]" +
                            "[]" +
                            "[]"
                )

                //---- label1 ----
                label1!!.text = "Name:"
                contentPanel!!.add(label1, "cell 0 0")

                //---- textField1 ----
                textField1!!.componentPopupMenu = popupMenu1
                contentPanel!!.add(textField1, "cell 1 0")

                //---- label3 ----
                label3!!.text = "Package:"
                contentPanel!!.add(label3, "cell 0 1")

                //---- comboBox2 ----
                comboBox2!!.isEditable = true
                comboBox2!!.model = DefaultComboBoxModel(
                    arrayOf(
                        "com.myapp",
                        "com.myapp.core",
                        "com.myapp.ui",
                        "com.myapp.util",
                        "com.myapp.extras",
                        "com.myapp.components",
                        "com.myapp.dialogs",
                        "com.myapp.windows"
                    )
                )
                contentPanel!!.add(comboBox2, "cell 1 1")

                //---- label2 ----
                label2!!.text = "Type:"
                contentPanel!!.add(label2, "cell 0 2")

                //---- comboBox1 ----
                comboBox1!!.model = DefaultComboBoxModel(
                    arrayOf(
                        "Class",
                        "Interface",
                        "Package",
                        "Annotation",
                        "Enum",
                        "Record",
                        "Java Project",
                        "Project",
                        "Folder",
                        "File"
                    )
                )
                contentPanel!!.add(comboBox1, "cell 1 2")
            }
            dialogPane!!.add(contentPanel, BorderLayout.CENTER)

            //======== buttonBar ========
            run {
                buttonBar!!.layout = MigLayout(
                    "insets dialog,alignx right",  // columns
                    "[button,fill]" +
                            "[button,fill]",  // rows
                    null
                )

                //---- okButton ----
                okButton!!.text = "OK"
                okButton!!.addActionListener { e: ActionEvent? -> okActionPerformed() }
                buttonBar!!.add(okButton, "cell 0 0")

                //---- cancelButton ----
                cancelButton!!.text = "Cancel"
                cancelButton!!.addActionListener { e: ActionEvent? -> cancelActionPerformed() }
                buttonBar!!.add(cancelButton, "cell 1 0")
            }
            dialogPane!!.add(buttonBar, BorderLayout.SOUTH)

            //======== menuBar1 ========
            run {


                //======== menu1 ========
                run {
                    menu1!!.text = "text"

                    //---- menuItem8 ----
                    menuItem8!!.text = "text"
                    menu1!!.add(menuItem8)

                    //---- menuItem7 ----
                    menuItem7!!.text = "text"
                    menu1!!.add(menuItem7)

                    //---- menuItem6 ----
                    menuItem6!!.text = "text"
                    menu1!!.add(menuItem6)

                    //---- menuItem5 ----
                    menuItem5!!.text = "text"
                    menu1!!.add(menuItem5)

                    //---- menuItem4 ----
                    menuItem4!!.text = "text"
                    menu1!!.add(menuItem4)

                    //---- menuItem3 ----
                    menuItem3!!.text = "text"
                    menu1!!.add(menuItem3)

                    //---- menuItem2 ----
                    menuItem2!!.text = "text"
                    menu1!!.add(menuItem2)

                    //---- menuItem1 ----
                    menuItem1!!.text = "text"
                    menu1!!.add(menuItem1)
                }
                menuBar1!!.add(menu1)

                //======== menu2 ========
                run {
                    menu2!!.text = "text"

                    //---- menuItem18 ----
                    menuItem18!!.text = "text"
                    menu2!!.add(menuItem18)

                    //---- menuItem17 ----
                    menuItem17!!.text = "text"
                    menu2!!.add(menuItem17)

                    //---- menuItem16 ----
                    menuItem16!!.text = "text"
                    menu2!!.add(menuItem16)

                    //---- menuItem15 ----
                    menuItem15!!.text = "text"
                    menu2!!.add(menuItem15)

                    //---- menuItem14 ----
                    menuItem14!!.text = "text"
                    menu2!!.add(menuItem14)

                    //---- menuItem13 ----
                    menuItem13!!.text = "text"
                    menu2!!.add(menuItem13)

                    //---- menuItem12 ----
                    menuItem12!!.text = "text"
                    menu2!!.add(menuItem12)

                    //---- menuItem11 ----
                    menuItem11!!.text = "text"
                    menu2!!.add(menuItem11)

                    //---- menuItem10 ----
                    menuItem10!!.text = "text"
                    menu2!!.add(menuItem10)

                    //---- menuItem9 ----
                    menuItem9!!.text = "text"
                    menu2!!.add(menuItem9)
                }
                menuBar1!!.add(menu2)

                //======== menu3 ========
                run {
                    menu3!!.text = "text"

                    //---- menuItem25 ----
                    menuItem25!!.text = "text"
                    menu3!!.add(menuItem25)

                    //---- menuItem26 ----
                    menuItem26!!.text = "text"
                    menu3!!.add(menuItem26)

                    //---- menuItem24 ----
                    menuItem24!!.text = "text"
                    menu3!!.add(menuItem24)

                    //---- menuItem23 ----
                    menuItem23!!.text = "text"
                    menu3!!.add(menuItem23)

                    //---- menuItem22 ----
                    menuItem22!!.text = "text"
                    menu3!!.add(menuItem22)

                    //---- menuItem21 ----
                    menuItem21!!.text = "text"
                    menu3!!.add(menuItem21)

                    //---- menuItem20 ----
                    menuItem20!!.text = "text"
                    menu3!!.add(menuItem20)

                    //---- menuItem19 ----
                    menuItem19!!.text = "text"
                    menu3!!.add(menuItem19)
                }
                menuBar1!!.add(menu3)
            }
            dialogPane!!.add(menuBar1, BorderLayout.NORTH)
        }
        contentPane.add(dialogPane, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(owner)

        //======== popupMenu1 ========
        run {


            //---- cutMenuItem ----
            cutMenuItem!!.text = "Cut"
            cutMenuItem!!.setMnemonic('C')
            popupMenu1!!.add(cutMenuItem)

            //---- copyMenuItem ----
            copyMenuItem!!.text = "Copy"
            copyMenuItem!!.setMnemonic('O')
            popupMenu1!!.add(copyMenuItem)

            //---- pasteMenuItem ----
            pasteMenuItem!!.text = "Paste"
            pasteMenuItem!!.setMnemonic('P')
            popupMenu1!!.add(pasteMenuItem)
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private var dialogPane: JPanel? = null
    private var contentPanel: JPanel? = null
    private var label1: JLabel? = null
    private var textField1: JTextField? = null
    private var label3: JLabel? = null
    private var comboBox2: JComboBox<String>? = null
    private var label2: JLabel? = null
    private var comboBox1: JComboBox<String>? = null
    private var buttonBar: JPanel? = null
    private var okButton: JButton? = null
    private var cancelButton: JButton? = null
    private var menuBar1: JMenuBar? = null
    private var menu1: JMenu? = null
    private var menuItem8: JMenuItem? = null
    private var menuItem7: JMenuItem? = null
    private var menuItem6: JMenuItem? = null
    private var menuItem5: JMenuItem? = null
    private var menuItem4: JMenuItem? = null
    private var menuItem3: JMenuItem? = null
    private var menuItem2: JMenuItem? = null
    private var menuItem1: JMenuItem? = null
    private var menu2: JMenu? = null
    private var menuItem18: JMenuItem? = null
    private var menuItem17: JMenuItem? = null
    private var menuItem16: JMenuItem? = null
    private var menuItem15: JMenuItem? = null
    private var menuItem14: JMenuItem? = null
    private var menuItem13: JMenuItem? = null
    private var menuItem12: JMenuItem? = null
    private var menuItem11: JMenuItem? = null
    private var menuItem10: JMenuItem? = null
    private var menuItem9: JMenuItem? = null
    private var menu3: JMenu? = null
    private var menuItem25: JMenuItem? = null
    private var menuItem26: JMenuItem? = null
    private var menuItem24: JMenuItem? = null
    private var menuItem23: JMenuItem? = null
    private var menuItem22: JMenuItem? = null
    private var menuItem21: JMenuItem? = null
    private var menuItem20: JMenuItem? = null
    private var menuItem19: JMenuItem? = null
    private var popupMenu1: JPopupMenu? = null
    private var cutMenuItem: JMenuItem? = null
    private var copyMenuItem: JMenuItem? = null
    private var pasteMenuItem: JMenuItem? = null // JFormDesigner - End of variables declaration  //GEN-END:variables

    init {
        initComponents()

        // hide menubar, which is here for testing
        menuBar1!!.isVisible = false
        getRootPane().defaultButton = okButton

        // register ESC key to close frame
        (contentPane as JComponent).registerKeyboardAction(
            { e: ActionEvent? -> dispose() },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        )
    }
}