package cc.eleb.parfait.filter

import javax.swing.RowFilter

class AlwaysRowFilter<M, I>(val res: Boolean) : RowFilter<M, I>() {
    override fun include(p0: Entry<out M, out I>?): Boolean = res
}