package com.n9mtq4.hrm2j.parser

import com.n9mtq4.kotlin.extlib.ignoreAndGiven

/**
 * Created by will on 7/22/16 at 11:49 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
enum class CommandMatcher(private val regexStr: String, internal val getCommand: (String) -> Command, internal val regex: Regex = regexStr.toRegex(RegexOption.IGNORE_CASE)) {
	
	INBOX("""^(inbox|input)""", { Command.Inbox() }),
	OUTBOX("""^(outbox|output)""", { Command.Outbox() }),
	COPY_TO_BOX("""^copyto [0-9]+""", { Command.CopyToBox(getBoxNum(it)) }),
	COPY_TO_BOX_VALUE("""^copyto \[[0-9]+\]""", { Command.CopyToBoxValue(getBoxValueNum(it)) }),
	COPY_FROM_BOX("""^copyfrom [0-9]+""", { Command.CopyFromBox(getBoxNum(it)) }),
	COPY_FROM_BOX_VALUE("""^copyfrom \[[0-9]+\]""", { Command.CopyFromBoxValue(getBoxValueNum(it)) }),
	ADD_TO_BOX("""^add [0-9]+""", { Command.AddToBox(getBoxNum(it)) }),
	ADD_TO_BOX_VALUE("""^add \[[0-9]+\]""", { Command.AddToBoxValue(getBoxValueNum(it)) }),
	SUB_TO_BOX("""^sub [0-9]+""", { Command.SubToBox(getBoxNum(it)) }),
	SUB_TO_BOX_VALUE("""^sub \[[0-9]+\]""", { Command.SubToBoxValue(getBoxValueNum(it)) }),
	INCREMENT("""^(bumpup|inc) [0-9]+""", { Command.Increment(getBoxNum(it)) }),
	DECREMENT("""^(bumpdn|dec) [0-9]+""", { Command.Decrement(getBoxNum(it)) }),
	JUMP("""^jump .+""", { Command.Jump(getArg(it)) }),
	JUMP_IF_NEGATIVE("""^jumpn .+""", { Command.JumpIfNegative(getArg(it)) }),
	JUMP_IF_ZERO("""^jumpz .+""", { Command.JumpIfZero(getArg(it)) }),
	
	// expanding upon the default human resource machine instructions
	LOAD("""^load (-?[0-9]+|\D|'.')""", { Command.Load(getDataNum(it)) }),
	JUMP_IF_EQUAL("""^jumpeq [0-9]+ .+""", { Command.JumpIfEqual(getBoxNum(it), getArg(it, index = 2)) }),
	CRASH("""^crash""", { Command.Crash() });
	
}

private val dataRegex = """'.'""".toRegex(RegexOption.IGNORE_CASE)
internal fun getArg(str: String, index: Int = 1) = str.split(" ")[index]
internal fun getBoxNum(str: String, index: Int = 1) = getArg(str, index).toInt()
internal fun getBoxValueNum(str: String, index: Int = 1) = getArg(str, index).replace("[", "").replace("]", "").toInt()
internal fun getDataNum(str: String, index: Int = 1) = getArg(str, index).run {
	ignoreAndGiven(
			if (this.matches(dataRegex))
				DataConverter.toData(this.toCharArray()[1])
			else
				DataConverter.toData(this.toCharArray()[0])
	) {
		this.toInt()
	}
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
			filterNot { it.startsWith("define comment", true) }. // remove comment definitions
			filterNot { it.startsWith("define label", true) }. // remove label definitions
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
	return Program(sectionList)
	
}

private fun matchCommand(str: String): Command? {
	CommandMatcher.values().forEach {
		if (it.regex.matches(str)) return it.getCommand(str)
	}
	return null
}
