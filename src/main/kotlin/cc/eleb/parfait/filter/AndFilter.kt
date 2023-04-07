package cc.eleb.parfait.filter

import javax.swing.RowFilter

class AndFilter<M, I>(private val filters: List<RowFilter<in M, in I>>) : RowFilter<M, I>() {
    override fun include(p0: Entry<out M, out I>): Boolean {
        filters.forEach {
            if (!it.include(p0)) return false
        }
        return true
    }
}