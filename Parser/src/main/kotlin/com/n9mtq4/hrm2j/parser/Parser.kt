package com.n9mtq4.hrm2j.parser

import com.n9mtq4.kotlin.extlib.ignoreAndGiven

/**
 * Created by will on 7/22/16 at 11:49 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

private const val DIGIT = """(-?(0[xX][0-9a-fA-F]+)+|-?0[bB][01]+|(-?[0-9]+))"""
private const val VALUE = """(-?(0[xX][0-9a-fA-F]+)+|-?0[bB][01]+|(-?[0-9]+)|'.'|\D)"""

enum class CommandMatcher(private val regexStr: String, internal val getCommand: (String) -> Command, internal val regex: Regex = regexStr.toRegex(RegexOption.IGNORE_CASE)) {
	
	INBOX("""^(inbox|input)""", { Command.Inbox() }),
	OUTBOX("""^(outbox|output)""", { Command.Outbox() }),
	COPY_TO_BOX("""^copyto $DIGIT""", { Command.CopyToBox(getBoxNum(it)) }),
	COPY_TO_BOX_VALUE("""^copyto \[$DIGIT\]""", { Command.CopyToBoxValue(getBoxValueNum(it)) }),
	COPY_FROM_BOX("""^copyfrom $DIGIT""", { Command.CopyFromBox(getBoxNum(it)) }),
	COPY_FROM_BOX_VALUE("""^copyfrom \[$DIGIT\]""", { Command.CopyFromBoxValue(getBoxValueNum(it)) }),
	ADD_TO_BOX("""^add $DIGIT""", { Command.AddToBox(getBoxNum(it)) }),
	ADD_TO_BOX_VALUE("""^add \[$DIGIT\]""", { Command.AddToBoxValue(getBoxValueNum(it)) }),
	SUB_TO_BOX("""^sub $DIGIT""", { Command.SubToBox(getBoxNum(it)) }),
	SUB_TO_BOX_VALUE("""^sub \[$DIGIT\]""", { Command.SubToBoxValue(getBoxValueNum(it)) }),
	INCREMENT("""^(bumpup|inc) $DIGIT""", { Command.Increment(getBoxNum(it)) }),
	DECREMENT("""^(bumpdn|dec) $DIGIT""", { Command.Decrement(getBoxNum(it)) }),
	JUMP("""^jump .+""", { Command.Jump(getArg(it)) }),
	JUMP_IF_NEGATIVE("""^jumpn .+""", { Command.JumpIfNegative(getArg(it)) }),
	JUMP_IF_ZERO("""^jumpz .+""", { Command.JumpIfZero(getArg(it)) }),
	
	// expanding upon the default human resource machine instructions
	LOAD("""^load $VALUE""", { Command.Load(getDataNum(it)) }),
	JUMP_IF_EQUAL("""^jumpeq $DIGIT .+""", { Command.JumpIfEqual(getBoxNum(it), getArg(it, index = 2)) }),
	CRASH("""^crash""", { Command.Crash() });
	
}

private val dataRegex = """'.'""".toRegex(RegexOption.IGNORE_CASE)
internal fun getArg(str: String, index: Int = 1) = str.split(" ")[index]
internal fun getBoxNum(str: String, index: Int = 1) = getArg(str, index).toBasesInt()
internal fun getBoxValueNum(str: String, index: Int = 1) = getArg(str, index).replace("[", "").replace("]", "").toBasesInt()
internal fun getDataNum(str: String, index: Int = 1) = getArg(str, index).getHrmValue()

fun String.getHrmValue() = ignoreAndGiven(
		if (this.matches(dataRegex))
			DataConverter.toData(this.toCharArray()[1])
		else
			DataConverter.toData(this.toCharArray()[0])
) {
	this.toBasesInt()
}

fun String.toBasesInt(): Int {
	if (this.startsWith("-")) return -(this.substring(1, this.length).toBasesInt())
	if (this.startsWith("0x")) return Integer.parseInt(this.substring(2, this.length), 16)
	if (this.startsWith("0b")) return Integer.parseInt(this.substring(2, this.length), 2)
	return this.toInt()
}

fun parseProgram(str: String, error: (String) -> Unit = {}): Program {
	
	val lines = str.split("\n")
	
	val sectionList = arrayListOf<Section>()
	var commands = arrayListOf<Command>()
	var sectionName = "synthetic_compile_mainfunc"
	
	// sanatize the lines
	lines.
			map(String::trim). // remove extra spaces
			map { // handle comments
				when {
					it.startsWith("--") -> ""
					it.contains("--") -> it.split("--")[0].trim()
					else -> it 
				} 
			}.
			map { it.replace(Regex("\\s+"), " ") }. // replace more then one space with one space
			filterNot(String::isBlank). // remove blank lines
//			filterNot { it.startsWith("define comment", true) }. // remove comment definitions
//			filterNot { it.startsWith("define label", true) }. // remove label definitions
			forEach { line ->
				
				// now actually handle the parsing of the program
				if (line.endsWith(":")) {
					// this may not be the first time, so close the previous section first
					sectionList.add(Section(sectionName, commands))
					// a label, so start a section
					commands = arrayListOf()
					sectionName = line.split(":")[0] // TODO: maybe a substring would be faster?
					return@forEach // ok nothing else to do for this line, NEXT!
				}
				
				// ok, we must have an instruction now, lets parse that
				val command = matchCommand(line)
				if (command == null) error("unknown command: $line")
				command?.let { commands.add(it) }
				
			}
	
	// ok all done, lets close the current section
	sectionList.add(Section(sectionName, commands))
	
	// lets wrap it up in a nice program
	val program = Program(sectionList)
	
	postProgramCheck(program, error)
	
	return program
	
}

private fun postProgramCheck(program: Program, error: (String) -> Unit = {}) {
	
	// make sure all labels are present
	program.sections.flatMap { it.commands }.filter { it is Label }.forEach { 
		it as Label
		val index = program.sectionIndexOf(it.label)
		if (index == -1) error("No label '${it.label}' in this program!")
	}
	
	// spaces in labels will cause spaces in method names when compiling to java.
	program.sections.forEach { 
		val old = it.label
		if (old.contains(" ")) {
			val new = old.replace(" ", "__")
			error("The label '$old' contains a space, changing the name to '$new'.")
			it.label = new
		}
	}
	program.sections.
			map { it.label }.
			filter { it.contains(" ") }.
			forEach {
			}
	
}

private fun matchCommand(str: String): Command? {
	CommandMatcher.values().forEach {
		if (it.regex.matches(str)) return it.getCommand(str)
	}
	return null
}
