package com.n9mtq4.hrm2j.compiler

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by will on 7/18/16 at 12:11 AM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
class StandAloneCompiler(val text: String, val packageName: String = "com.n9mtq4.hrm2j.gen", val inboxValues: IntArray, val floorSize: Int, val floorValues: IntArray, val outboxSize: Int, val stackTrace: Boolean = false) {
	
	fun generateCode(): Generated {
		
		val c = Compiler(text, "HrmCpu\$impl", packageName, inboxValues, floorSize, floorValues, outboxSize, stackTrace, import = false, inner = true)
		val generatedCode = c.generateCode()
		
		val input = StandAloneCompiler::class.java.getResourceAsStream("/HrmRunner.java")
		val reader = BufferedReader(InputStreamReader(input))
		val readCode = reader.readText()
		val timeStamp = System.currentTimeMillis()
		val allCode = readCode.replace("GENERATED_CODE_TIMESTAMP_ID", "$timeStamp").replace("//GENERATED_CODE_HERE", generatedCode)
		
		reader.close()
		return Generated("HrmRunner\$$timeStamp", packageName, allCode)
		
	}
	
}

data class Generated(val className: String, val packageName: String, val code: String)
