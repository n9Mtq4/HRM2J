package com.n9mtq4.hrm2j.gui

import java.awt.Component
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JOptionPane

/**
 * Created by will on 7/25/16 at 8:20 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
fun Throwable.toText(): String {
	val sw = StringWriter()
	val pw = PrintWriter(sw)
	this.printStackTrace(pw)
	return sw.toString() // stack trace as a string
}

fun msg(parent: Component? = null, msg: String, title: String, msgType: Int = JOptionPane.INFORMATION_MESSAGE) = JOptionPane.showMessageDialog(parent, msg, title, msgType)
