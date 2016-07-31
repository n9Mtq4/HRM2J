package com.n9mtq4.hrm2j.parser

/**
 * Created by will on 7/22/16 at 11:00 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
sealed class Command() {
	
	class Inbox() : Command()
	class Outbox() : Command()
	data class CopyToBox(override val pointer: Int) : Command(), Pointer
	data class CopyToBoxValue(override val pointer: Int) : Command(), Pointer
	data class CopyFromBox(override val pointer: Int) : Command(), Pointer
	data class CopyFromBoxValue(override val pointer: Int) : Command(), Pointer
	data class AddToBox(override val pointer: Int) : Command(), Pointer
	data class AddToBoxValue(override val pointer: Int) : Command(), Pointer
	data class SubToBox(override val pointer: Int) : Command(), Pointer
	data class SubToBoxValue(override val pointer: Int) : Command(), Pointer
	data class Increment(override val pointer: Int) : Command(), Pointer
	data class Decrement(override val pointer: Int) : Command(), Pointer
	data class Jump(override val label: String) : Command(), Label
	data class JumpIfNegative(override val label: String) : Command(), Label
	data class JumpIfZero(override val label: String) : Command(), Label
	
	// expanding upon the default human resource machine instructions
	data class Load(override val value: Int) : Command(), Value
	data class JumpIfEqual(override val pointer: Int, override val label: String) : Command(), Pointer, Label
	class Crash() : Command()
	
}

interface Pointer {
	val pointer: Int
}
interface Label {
	val label: String
}
interface Value {
	val value: Int
}

data class Section(val label: String, val commands: List<Command>)
data class Program(val sections: List<Section>)

fun Program.sectionIndexOf(label: String): Int {
	this.sections.forEachIndexed { i, section ->
		if (section.label == label) {
			return i
		}
	}
	return -1
}
