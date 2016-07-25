package com.n9mtq4.hrm2j.gui

import com.n9mtq4.hrm2j.compiler.fastCompile
import com.n9mtq4.hrm2j.compiler.parseProgram
import com.n9mtq4.hrm2j.compiler.runProgram
import com.n9mtq4.kotlin.extlib.io.open
import com.n9mtq4.kotlin.extlib.pst
import java.io.File
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
	
//	println((-1L).toBin())
//	println((1 shl 30).toBin())
//	println()
	
}

fun old() {
	val text = open("pr.txt", "r").use { it.readText() }!!
	val program = parseProgram(text) { println(it) }
	val result = runProgram(program, intArrayOf(1,2,3,0,3,2,1,0), 0x40, intArrayOf(), 0xfff, stackTrace = true, file = File("gen/TestCompile.java"))
	println(result)
}

private fun Int.toBin() = Integer.toBinaryString(this)
private fun Long.toBin() = java.lang.Long.toBinaryString(this)

fun fast() {
	val text = open("pr.txt", "r").use { it.readText() }!!
	val program = parseProgram(text)
	fastCompile(program, "TestSuperFast", "test", intArrayOf(1,2,3,4,0,4,3,2,1,0,1,3,3,2,2,1,4,0,4,3,2,1,4,3,2,1,4,3,2,1,0,2,1,3,4,7,8,3,2,1,4,6,0,2,7,3,5,1,2,3,0,4,3,1,2,4,2,3,1,4,0), 64, intArrayOf(), 0xfff, true) {
//		println(it)
	}
}
