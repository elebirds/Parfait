/*
 * Created by JFormDesigner on Sat Mar 04 21:57:38 CST 2023
 */

package cc.eleb.parfait.ui;

import com.formdev.flatlaf.demo.BasicComponentsPanel;
import com.formdev.flatlaf.demo.ControlBar;
import com.formdev.flatlaf.demo.DataComponentsPanel;
import com.formdev.flatlaf.demo.intellijthemes.IJThemesPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * @author hhmcn
 */
public class ParfaitFrame extends JFrame {
    public ParfaitFrame() {
        initComponents();
    }

    private void newActionPerformed() {
        // TODO add your code here
    }

    private void openActionPerformed() {
        // TODO add your code here
    }

    private void saveAsActionPerformed() {
        // TODO add your code here
    }

    private void menuItemActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void exitActionPerformed() {
        // TODO add your code here
    }

    private void restoreFont() {
        // TODO add your code here
    }

    private void incrFont() {
        // TODO add your code here
    }

    private void decrFont() {
        // TODO add your code here
    }

    private void windowDecorationsChanged() {
        // TODO add your code here
    }

    private void menuBarEmbeddedChanged() {
        // TODO add your code here
    }

    private void unifiedTitleBar() {
        // TODO add your code here
    }

    private void showTitleBarIcon() {
        // TODO add your code here
    }

    private void underlineMenuSelection() {
        // TODO add your code here
    }

    private void alwaysShowMnemonics() {
        // TODO add your code here
    }

    private void animatedLafChangeChanged() {
        // TODO add your code here
    }

    private void showHintsChanged() {
        // TODO add your code here
    }

    private void showUIDefaultsInspector() {
        // TODO add your code here
    }

    private void aboutActionPerformed() {
        // TODO add your code here
    }

    private void selectedTabChanged() {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - g
        var menuBar1 = new JMenuBar();
        var fileMenu = new JMenu();
        var newMenuItem = new JMenuItem();
        var openMenuItem = new JMenuItem();
        var saveAsMenuItem = new JMenuItem();
        var closeMenuItem = new JMenuItem();
        exitMenuItem = new JMenuItem();
        var editMenu = new JMenu();
        var undoMenuItem = new JMenuItem();
        var redoMenuItem = new JMenuItem();
        var cutMenuItem = new JMenuItem();
        var copyMenuItem = new JMenuItem();
        var pasteMenuItem = new JMenuItem();
        var deleteMenuItem = new JMenuItem();
        var viewMenu = new JMenu();
        var checkBoxMenuItem1 = new JCheckBoxMenuItem();
        var menu1 = new JMenu();
        var subViewsMenu = new JMenu();
        var subSubViewsMenu = new JMenu();
        var errorLogViewMenuItem = new JMenuItem();
        var searchViewMenuItem = new JMenuItem();
        var projectViewMenuItem = new JMenuItem();
        var structureViewMenuItem = new JMenuItem();
        var propertiesViewMenuItem = new JMenuItem();
        scrollingPopupMenu = new JMenu();
        var menuItem2 = new JMenuItem();
        htmlMenuItem = new JMenuItem();
        var radioButtonMenuItem1 = new JRadioButtonMenuItem();
        var radioButtonMenuItem2 = new JRadioButtonMenuItem();
        var radioButtonMenuItem3 = new JRadioButtonMenuItem();
        fontMenu = new JMenu();
        var restoreFontMenuItem = new JMenuItem();
        var incrFontMenuItem = new JMenuItem();
        var decrFontMenuItem = new JMenuItem();
        optionsMenu = new JMenu();
        windowDecorationsCheckBoxMenuItem = new JCheckBoxMenuItem();
        menuBarEmbeddedCheckBoxMenuItem = new JCheckBoxMenuItem();
        unifiedTitleBarMenuItem = new JCheckBoxMenuItem();
        showTitleBarIconMenuItem = new JCheckBoxMenuItem();
        underlineMenuSelectionMenuItem = new JCheckBoxMenuItem();
        alwaysShowMnemonicsMenuItem = new JCheckBoxMenuItem();
        animatedLafChangeMenuItem = new JCheckBoxMenuItem();
        var showHintsMenuItem = new JMenuItem();
        var showUIDefaultsInspectorMenuItem = new JMenuItem();
        var helpMenu = new JMenu();
        aboutMenuItem = new JMenuItem();
        var contentPanel = new JPanel();
        tabbedPane = new JTabbedPane();
        var basicComponentsPanel = new BasicComponentsPanel();
        var dataComponentsPanel = new DataComponentsPanel();
        controlBar = new ControlBar();
        themesPanel = new IJThemesPanel();

        //======== this ========
        setTitle("Parfait Demo");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar1 ========
        {

            //======== fileMenu ========
            {
                fileMenu.setText("\u6587\u4ef6");
                fileMenu.setMnemonic('F');

                //---- newMenuItem ----
                newMenuItem.setText("\u65b0\u5efa");
                newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                newMenuItem.setMnemonic('N');
                newMenuItem.addActionListener(e -> newActionPerformed());
                fileMenu.add(newMenuItem);

                //---- openMenuItem ----
                openMenuItem.setText("\u6253\u5f00");
                openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                openMenuItem.setMnemonic('O');
                openMenuItem.addActionListener(e -> openActionPerformed());
                fileMenu.add(openMenuItem);

                //---- saveAsMenuItem ----
                saveAsMenuItem.setText("\u4fdd\u5b58");
                saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                saveAsMenuItem.setMnemonic('S');
                saveAsMenuItem.addActionListener(e -> saveAsActionPerformed());
                fileMenu.add(saveAsMenuItem);
                fileMenu.addSeparator();

                //---- closeMenuItem ----
                closeMenuItem.setText("\u5173\u95ed");
                closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                closeMenuItem.setMnemonic('C');
                closeMenuItem.addActionListener(e -> menuItemActionPerformed(e));
                fileMenu.add(closeMenuItem);
                fileMenu.addSeparator();

                //---- exitMenuItem ----
                exitMenuItem.setText("\u9000\u51fa");
                exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                exitMenuItem.setMnemonic('X');
                exitMenuItem.addActionListener(e -> exitActionPerformed());
                fileMenu.add(exitMenuItem);
            }
            menuBar1.add(fileMenu);

            //======== editMenu ========
            {
                editMenu.setText("\u7f16\u8f91");
                editMenu.setMnemonic('E');

                //---- undoMenuItem ----
                undoMenuItem.setText("Undo");
                undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                undoMenuItem.setMnemonic('U');
                undoMenuItem.addActionListener(e -> menuItemActionPerformed(e));
                editMenu.add(undoMenuItem);

                //---- redoMenuItem ----
                redoMenuItem.setText("Redo");
                redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                redoMenuItem.setMnemonic('R');
                redoMenuItem.addActionListener(e -> menuItemActionPerformed(e));
                editMenu.add(redoMenuItem);
                editMenu.addSeparator();

                //---- cutMenuItem ----
                cutMenuItem.setText("Cut");
                cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                cutMenuItem.setMnemonic('C');
                editMenu.add(cutMenuItem);

                //---- copyMenuItem ----
                copyMenuItem.setText("Copy");
                copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                copyMenuItem.setMnemonic('O');
                editMenu.add(copyMenuItem);

                //---- pasteMenuItem ----
                pasteMenuItem.setText("Paste");
                pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                pasteMenuItem.setMnemonic('P');
                editMenu.add(pasteMenuItem);
                editMenu.addSeparator();

                //---- deleteMenuItem ----
                deleteMenuItem.setText("Delete");
                deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
                deleteMenuItem.setMnemonic('D');
                deleteMenuItem.addActionListener(e -> menuItemActionPerformed(e));
                editMenu.add(deleteMenuItem);
            }
            menuBar1.add(editMenu);

            //======== viewMenu ========
            {
                viewMenu.setText("\u89c6\u56fe");
                viewMenu.setMnemonic('V');

                //---- checkBoxMenuItem1 ----
                checkBoxMenuItem1.setText("Show Toolbar");
                checkBoxMenuItem1.setSelected(true);
                checkBoxMenuItem1.setMnemonic('T');
                checkBoxMenuItem1.addActionListener(e -> menuItemActionPerformed(e));
                viewMenu.add(checkBoxMenuItem1);

                //======== menu1 ========
                {
                    menu1.setText("Show View");
                    menu1.setMnemonic('V');

                    //======== subViewsMenu ========
                    {
                        subViewsMenu.setText("Sub Views");
                        subViewsMenu.setMnemonic('S');

                        //======== subSubViewsMenu ========
                        {
                            subSubViewsMenu.setText("Sub sub Views");
                            subSubViewsMenu.setMnemonic('U');

                            //---- errorLogViewMenuItem ----
                            errorLogViewMenuItem.setText("Error Log");
                            errorLogViewMenuItem.setMnemonic('E');
                            errorLogViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
                            subSubViewsMenu.add(errorLogViewMenuItem);
                        }
                        subViewsMenu.add(subSubViewsMenu);

                        //---- searchViewMenuItem ----
                        searchViewMenuItem.setText("Search");
                        searchViewMenuItem.setMnemonic('S');
                        searchViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
                        subViewsMenu.add(searchViewMenuItem);
                    }
                    menu1.add(subViewsMenu);

                    //---- projectViewMenuItem ----
                    projectViewMenuItem.setText("Project");
                    projectViewMenuItem.setMnemonic('P');
                    projectViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
                    menu1.add(projectViewMenuItem);

                    //---- structureViewMenuItem ----
                    structureViewMenuItem.setText("Structure");
                    structureViewMenuItem.setMnemonic('T');
                    structureViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
                    menu1.add(structureViewMenuItem);

                    //---- propertiesViewMenuItem ----
                    propertiesViewMenuItem.setText("Properties");
                    propertiesViewMenuItem.setMnemonic('O');
                    propertiesViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
                    menu1.add(propertiesViewMenuItem);
                }
                viewMenu.add(menu1);

                //======== scrollingPopupMenu ========
                {
                    scrollingPopupMenu.setText("Scrolling Popup Menu");
                }
                viewMenu.add(scrollingPopupMenu);

                //---- menuItem2 ----
                menuItem2.setText("Disabled Item");
                menuItem2.setEnabled(false);
                viewMenu.add(menuItem2);

                //---- htmlMenuItem ----
                htmlMenuItem.setText("<html>some <b color=\"red\">HTML</b> <i color=\"blue\">text</i></html>");
                viewMenu.add(htmlMenuItem);
                viewMenu.addSeparator();

                //---- radioButtonMenuItem1 ----
                radioButtonMenuItem1.setText("Details");
                radioButtonMenuItem1.setSelected(true);
                radioButtonMenuItem1.setMnemonic('D');
                radioButtonMenuItem1.addActionListener(e -> menuItemActionPerformed(e));
                viewMenu.add(radioButtonMenuItem1);

                //---- radioButtonMenuItem2 ----
                radioButtonMenuItem2.setText("Small Icons");
                radioButtonMenuItem2.setMnemonic('S');
                radioButtonMenuItem2.addActionListener(e -> menuItemActionPerformed(e));
                viewMenu.add(radioButtonMenuItem2);

                //---- radioButtonMenuItem3 ----
                radioButtonMenuItem3.setText("Large Icons");
                radioButtonMenuItem3.setMnemonic('L');
                radioButtonMenuItem3.addActionListener(e -> menuItemActionPerformed(e));
                viewMenu.add(radioButtonMenuItem3);
            }
            menuBar1.add(viewMenu);

            //======== fontMenu ========
            {
                fontMenu.setText("\u4e3b\u9898");

                //---- restoreFontMenuItem ----
                restoreFontMenuItem.setText("Restore Font");
                restoreFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                restoreFontMenuItem.addActionListener(e -> restoreFont());
                fontMenu.add(restoreFontMenuItem);

                //---- incrFontMenuItem ----
                incrFontMenuItem.setText("Increase Font Size");
                incrFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                incrFontMenuItem.addActionListener(e -> incrFont());
                fontMenu.add(incrFontMenuItem);

                //---- decrFontMenuItem ----
                decrFontMenuItem.setText("Decrease Font Size");
                decrFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
                decrFontMenuItem.addActionListener(e -> decrFont());
                fontMenu.add(decrFontMenuItem);
            }
            menuBar1.add(fontMenu);

            //======== optionsMenu ========
            {
                optionsMenu.setText("Options");

                //---- windowDecorationsCheckBoxMenuItem ----
                windowDecorationsCheckBoxMenuItem.setText("Window decorations");
                windowDecorationsCheckBoxMenuItem.addActionListener(e -> windowDecorationsChanged());
                optionsMenu.add(windowDecorationsCheckBoxMenuItem);

                //---- menuBarEmbeddedCheckBoxMenuItem ----
                menuBarEmbeddedCheckBoxMenuItem.setText("Embedded menu bar");
                menuBarEmbeddedCheckBoxMenuItem.addActionListener(e -> menuBarEmbeddedChanged());
                optionsMenu.add(menuBarEmbeddedCheckBoxMenuItem);

                //---- unifiedTitleBarMenuItem ----
                unifiedTitleBarMenuItem.setText("Unified window title bar");
                unifiedTitleBarMenuItem.addActionListener(e -> unifiedTitleBar());
                optionsMenu.add(unifiedTitleBarMenuItem);

                //---- showTitleBarIconMenuItem ----
                showTitleBarIconMenuItem.setText("Show window title bar icon");
                showTitleBarIconMenuItem.addActionListener(e -> showTitleBarIcon());
                optionsMenu.add(showTitleBarIconMenuItem);

                //---- underlineMenuSelectionMenuItem ----
                underlineMenuSelectionMenuItem.setText("Use underline menu selection");
                underlineMenuSelectionMenuItem.addActionListener(e -> underlineMenuSelection());
                optionsMenu.add(underlineMenuSelectionMenuItem);

                //---- alwaysShowMnemonicsMenuItem ----
                alwaysShowMnemonicsMenuItem.setText("Always show mnemonics");
                alwaysShowMnemonicsMenuItem.addActionListener(e -> alwaysShowMnemonics());
                optionsMenu.add(alwaysShowMnemonicsMenuItem);

                //---- animatedLafChangeMenuItem ----
                animatedLafChangeMenuItem.setText("Animated Laf Change");
                animatedLafChangeMenuItem.setSelected(true);
                animatedLafChangeMenuItem.addActionListener(e -> animatedLafChangeChanged());
                optionsMenu.add(animatedLafChangeMenuItem);

                //---- showHintsMenuItem ----
                showHintsMenuItem.setText("Show hints");
                showHintsMenuItem.addActionListener(e -> showHintsChanged());
                optionsMenu.add(showHintsMenuItem);

                //---- showUIDefaultsInspectorMenuItem ----
                showUIDefaultsInspectorMenuItem.setText("Show UI Defaults Inspector");
                showUIDefaultsInspectorMenuItem.addActionListener(e -> showUIDefaultsInspector());
                optionsMenu.add(showUIDefaultsInspectorMenuItem);
            }
            menuBar1.add(optionsMenu);

            //======== helpMenu ========
            {
                helpMenu.setText("\u5e2e\u52a9");
                helpMenu.setMnemonic('H');

                //---- aboutMenuItem ----
                aboutMenuItem.setText("\u5173\u4e8e");
                aboutMenuItem.setMnemonic('A');
                aboutMenuItem.addActionListener(e -> aboutActionPerformed());
                helpMenu.add(aboutMenuItem);
            }
            menuBar1.add(helpMenu);
        }
        setJMenuBar(menuBar1);

        //======== contentPanel ========
        {
            contentPanel.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(
                    new javax.swing.border.EmptyBorder(0, 0, 0, 0), "JF\u006frmDesi\u0067ner Ev\u0061luatio\u006e"
                    , javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BOTTOM
                    , new java.awt.Font("Dialo\u0067", java.awt.Font.BOLD, 12)
                    , java.awt.Color.red), contentPanel.getBorder()));
            contentPanel.addPropertyChangeListener(
                    new java.beans.PropertyChangeListener() {
                        @Override
                        public void propertyChange(java.beans.PropertyChangeEvent e
                        ) {
                            if ("borde\u0072".equals(e.getPropertyName())) throw new RuntimeException()
                                    ;
                        }
                    });
            contentPanel.setLayout(new MigLayout(
                    "insets dialog,hidemode 3",
                    // columns
                    "[grow,fill]",
                    // rows
                    "[grow,fill]"));

            //======== tabbedPane ========
            {
                tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                tabbedPane.addChangeListener(e -> selectedTabChanged());
                tabbedPane.addTab("\u5b66\u751f\u7ba1\u7406", basicComponentsPanel);
                tabbedPane.addTab("Data Components", dataComponentsPanel);
            }
            contentPanel.add(tabbedPane, "cell 0 0");
        }
        contentPane.add(contentPanel, BorderLayout.CENTER);
        contentPane.add(controlBar, BorderLayout.SOUTH);
        contentPane.add(themesPanel, BorderLayout.EAST);

        //---- buttonGroup1 ----
        var buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(radioButtonMenuItem1);
        buttonGroup1.add(radioButtonMenuItem2);
        buttonGroup1.add(radioButtonMenuItem3);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - g
    private JMenuItem exitMenuItem;
    private JMenu scrollingPopupMenu;
    private JMenuItem htmlMenuItem;
    private JMenu fontMenu;
    private JMenu optionsMenu;
    private JCheckBoxMenuItem windowDecorationsCheckBoxMenuItem;
    private JCheckBoxMenuItem menuBarEmbeddedCheckBoxMenuItem;
    private JCheckBoxMenuItem unifiedTitleBarMenuItem;
    private JCheckBoxMenuItem showTitleBarIconMenuItem;
    private JCheckBoxMenuItem underlineMenuSelectionMenuItem;
    private JCheckBoxMenuItem alwaysShowMnemonicsMenuItem;
    private JCheckBoxMenuItem animatedLafChangeMenuItem;
    private JMenuItem aboutMenuItem;
    private JTabbedPane tabbedPane;
    private ControlBar controlBar;
    IJThemesPanel themesPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
