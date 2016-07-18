package com.n9mtq4.hrm2j.compiler



/**
 * Created by will on 7/17/16 at 4:30 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
class Compiler(val text: String, val className: String, val packageName: String, val inboxValues: IntArray, val floorSize: Int, val floorValues: IntArray, val outboxSize: Int, val stackTrace: Boolean = false) {
	
	private var firstFunc = ""
	private var lastFunc = ""
	private var prevCommand = ""
	
	private var code = ""
	
	fun generateCode(): String {
		generateHeader()
		generateBody()
		generateCloser()
		return code
	}
	
	fun generateHeader() {
		
		gl("package $packageName;")
		gl("import com.n9mtq4.hrm2j.runtime.*;")
		gl("public class $className extends HrmCpu {")
		gl("public $className(int[] inboxValues, int floorSize, int[] floorValues, int outboxSize) {")
		gl("super(inboxValues, floorSize, floorValues, outboxSize);")
		gl("}")
		gl("public $className() {")
		gl("super(new int[] {${inboxValues.joinToString(separator = ",")}}, $floorSize, new int[] {${floorValues.joinToString(separator = ",")}}, $outboxSize);")
		gl("}")
		
	}
	
	fun generateBody() {
		
		val lines = ("hrmfunc_compile_mainfunc:\n" + text).split("\n")
		
		lines.map(String::trim).map { 
			
//			get rid of comments
			when {
				it.startsWith("--") -> ""
				it.contains("--") -> it.split("--")[0].trim()
				else -> it
			}
			
		}.map { it.replace(Regex("\\s+"), " ") }.forEach { line ->
			
			if (line.isBlank()) return@forEach
			if (line.startsWith("define comment", true)) return // comments & labels mark the end, so we can finish
			if (line.startsWith("define label", true)) return // comments & labels mark the end, so we can finish
			
			if (line.endsWith(":")) {
//				label
				val labelName = line.split(":")[0]
				lastFunc = labelName
				if (firstFunc == "") firstFunc = labelName
				else {
//					close the previous function
					if (!prevCommand.contains("return")) gl("func_$lastFunc();")
					gl("}")
				}
				
//				define the next function
				gl("private void func_$lastFunc() {")
				
				return@forEach
				
			}
			
			gl(when {
				
				line.contains("inbox", true) -> "inbox()"
				line.contains("outbox", true) -> "outbox()"
				line.contains("copyto", true) -> {
					val number = line.split(" ")[1]
					if (number.contains("[") && number.contains("]")) {
						"copyToPointer(${number.replace("[", "").replace("]", "")})"
					}else {
						"copyTo($number)"
					}
				}
				line.contains("copyfrom", true) -> {
					val number = line.split(" ")[1]
					if (number.contains("[") && number.contains("]")) {
						"copyFromPointer(${number.replace("[", "").replace("]", "")})"
					}else {
						"copyFrom($number)"
					}
				}
				line.contains("add", true) -> {
					val number = line.split(" ")[1]
					if (number.contains("[") && number.contains("]")) {
						"addPointer(${number.replace("[", "").replace("]", "")})"
					}else {
						"add($number)"
					}
				}
				line.contains("sub", true) -> {
					val number = line.split(" ")[1]
					if (number.contains("[") && number.contains("]")) {
						"subtractPointer(${number.replace("[", "").replace("]", "")})"
					}else {
						"subtract($number)"
					}
				}
				line.contains("bumpup", true) -> {
					val number = line.split(" ")[1]
					"increment($number)"
				}
				line.contains("bumpdn", true) -> {
					val number = line.split(" ")[1]
					"decrement($number)"
				}
				line.contains("jump ", true) -> {
					val label = line.split(" ")[1]
					"func_$label();\nreturn"
				}
				line.contains("jumpz ", true) -> {
					val label = line.split(" ")[1]
					"if (hand == 0) {func_$label(); return;}"
				}
				line.contains("jumpn ", true) -> {
					val label = line.split(" ")[1]
					"if (hand < 0) { func_$label(); return;}"
				}
				else -> ""
				
			} + ";")
			
		}
		
	}
	
	fun generateCloser() {
		
//		close the last function
		gl("}")
		
		// run function
		gl("@Override")
		gl("public String run() {")
		gl("try {")
		gl("func_$firstFunc();")
		gl("} catch(Exception e) {")
		if (stackTrace) gl("e.printStackTrace();")
		gl("return toString();")
		gl("}")
		gl("return toString();")
		gl("}")
		
		gl("}") // close class
		
	}
	
	private fun gl(str: String) {
		prevCommand = str
		code += str + "\n"
//		println(str)
	}
	
}
