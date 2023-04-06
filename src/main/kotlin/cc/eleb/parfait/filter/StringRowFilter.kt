package cc.eleb.parfait.filter

import javax.swing.RowFilter

class StringRowFilter<M,I>(private val dt:String,private val index:Int,private val fullEqual: Boolean): RowFilter<M, I>() {
    override fun include(e: Entry<out M, out I>): Boolean = if(fullEqual){
        e.getStringValue(index)==dt
    }else{
        e.getStringValue(index).contains(dt)
    }
}