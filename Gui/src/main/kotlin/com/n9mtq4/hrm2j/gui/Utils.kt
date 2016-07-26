package com.n9mtq4.hrm2j.gui

import java.io.PrintWriter
import java.io.StringWriter

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
