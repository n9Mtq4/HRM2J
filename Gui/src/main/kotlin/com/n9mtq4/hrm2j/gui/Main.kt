package com.n9mtq4.hrm2j.gui

import com.n9mtq4.kotlin.extlib.pst
import javax.swing.SwingUtilities
import javax.swing.UIManager

/**
 * Created by will on 7/17/16 at 11:41 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
fun main(args: Array<String>) {
	
	pst { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) }
	
	SwingUtilities.invokeLater {
		HrmGui()
	}
	
}
