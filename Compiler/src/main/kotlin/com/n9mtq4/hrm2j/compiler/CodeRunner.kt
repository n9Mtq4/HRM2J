package com.n9mtq4.hrm2j.compiler

import com.n9mtq4.kotlin.extlib.io.open
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import javax.tools.ToolProvider

/**
 * Created by will on 7/24/16 at 2:48 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
fun runProgram(str: String, inboxValues: IntArray, floorSize: Int, floorValues: IntArray, outboxSize: Int, stackTrace: Boolean = false, file: File = File.createTempFile("InlineCompiled", ".java")) = runProgram(parseProgram(str), inboxValues, floorSize, floorValues, outboxSize, stackTrace, file)
fun runProgram(program: Program, inboxValues: IntArray, floorSize: Int, floorValues: IntArray, outboxSize: Int, stackTrace: Boolean = false, file: File = File.createTempFile("InlineCompiled", ".java")): String {
	
	val cls = getCompiledClass(program, inboxValues, floorSize, floorValues, outboxSize, stackTrace, file)
	
	val method = cls.getDeclaredMethod("sRun")
	val out = method.invoke(null)
	
	return out as String
	
}

fun getCompiledClass(program: Program, inboxValues: IntArray, floorSize: Int, floorValues: IntArray, outboxSize: Int, stackTrace: Boolean = false, file: File = File.createTempFile("InlineCompiled", ".java")): Class<*> {
	val className = file.nameWithoutExtension
	val sourceFile = open(file, "w")
	
	fastCompile(program, className, "", inboxValues, floorSize, floorValues, outboxSize, stackTrace) {
		sourceFile.writeln(it)
	}
	
	sourceFile.close()
	
	return compileFile(sourceFile, className)
}

private fun compileFile(file: File, className: String): Class<*> {
	val compiler = ToolProvider.getSystemJavaCompiler()
	compiler.run(null, null, null, file.path)
	
	val classLoader = URLClassLoader.newInstance(arrayOf<URL>(file.parentFile.toURI().toURL()))
	val cls = Class.forName(className, true, classLoader)
	return cls
}
