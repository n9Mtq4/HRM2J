package com.n9mtq4.hrm2j.gui

import com.n9mtq4.hrm2j.compiler.StandAloneCompiler
import com.n9mtq4.kotlin.extlib.io.open
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import javax.tools.ToolProvider

/**
 * Created by will on 7/17/16 at 11:41 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
fun main(args: Array<String>) {
	
	val text = open("pr.txt", "r").use { it.readText() }!!
	val sac = StandAloneCompiler(text, packageName = "", inboxValues = intArrayOf(1,2,3,4,0,4,3,2,1,0,1,3,3,2,2,1,4,0,4,3,2,1,4,3,2,1,4,3,2,1,0,2,1,3,4,7,8,3,2,1,4,6,0,2,7,3,5,1,2,3,0,4,3,1,2,4,2,3,1,4,0), floorSize = 25, floorValues = intArrayOf(24, 0), outboxSize = 0xff)
	
	val gen = sac.generateCode()
	
	val sourceFile = open("gen/" + gen.className + ".java", "w")
	sourceFile.mkdirs()
	sourceFile.writeln(gen.code)
	sourceFile.close()
	
	println(gen.code)
	
	val compiler = ToolProvider.getSystemJavaCompiler()
	compiler.run(null, null, null, sourceFile.path)
	
	val classLoader = URLClassLoader.newInstance(arrayOf<URL>(File("gen/").toURI().toURL()))
	val cls = Class.forName(gen.className, true, classLoader)
	val method = cls.getDeclaredMethod("run")
	val out = method.invoke(null)
	
	println(out)
	
}
