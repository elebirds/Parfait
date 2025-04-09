/*
 * Project Parfait
 * Copyright (c) elebird 2025.
 * All rights reserved.
 */

package moe.hhm.parfait.view.component.table

import java.awt.Dimension
import javax.swing.JTable

class StudentDataTable : JTable() {
    init {
        preferredViewportSize = Dimension(800, 600)
    }
}