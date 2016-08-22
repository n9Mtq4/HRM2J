package com.n9mtq4.hrm2j.interpreter

import com.n9mtq4.hrm2j.parser.Command
import com.n9mtq4.hrm2j.parser.DataConverter
import com.n9mtq4.hrm2j.parser.Program
import com.n9mtq4.hrm2j.parser.sectionIndexOf

/**
 * Created by will on 7/28/16 at 12:08 AM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
class Interpreter(val program: Program, inboxValues: IntArray, floorSize: Int, floorValues: IntArray, outboxSize: Int, val printStackTrace: Boolean = true, val errorPrinting: (String) -> Unit = {}) {
	
	val inbox: IntArray = inboxValues
	var inboxIndex = 0
	val floor: IntArray = IntArray(floorSize) { 0 }
	val outbox: IntArray = IntArray(outboxSize)
	var outboxIndex = 0
	var hand: Int = 0
	var stackTrace: Throwable? = null
	
	init {
		var i = 0
		while (i < floorValues.size) {
			floor[floorValues[i]] = floorValues[i + 1]
			i += 2
		}
	}
	
	fun run() {
		
		try {
			runSection(0)
		}catch (e: Throwable) {
			stackTrace = e
			if (printStackTrace) e.printStackTrace()
		}
		
	}
	
	fun runSection(index: Int) {
		if (index == -1) return // finished
		val section = program.sections[index]
		if (section.commands.size <= 0) {
			if (index == program.sections.size - 1) return
			runSection(index + 1)
			return
		}
		section.commands.forEach {
			when(it) {
				is Command.Inbox -> { hand = inbox[inboxIndex++] }
				is Command.Outbox -> { outbox[outboxIndex++] = hand; hand = 0 }
				is Command.CopyToBox -> { floor[it.pointer] = hand }
				is Command.CopyToBoxValue -> { floor[floor[it.pointer]] = hand }
				is Command.CopyFromBox -> { hand = floor[it.pointer] }
				is Command.CopyFromBoxValue -> { hand = floor[floor[it.pointer]] }
				is Command.AddToBox -> { hand += floor[it.pointer] }
				is Command.AddToBoxValue -> { hand += floor[floor[it.pointer]] }
				is Command.SubToBox -> { hand -= floor[it.pointer] }
				is Command.SubToBoxValue -> { hand -= floor[floor[it.pointer]] }
				is Command.Increment -> { floor[it.pointer]++; hand = floor[it.pointer] }
				is Command.Decrement -> { floor[it.pointer]--; hand = floor[it.pointer] }
				// JUMPS
				is Command.Jump -> { runSection(program.sectionIndexOf(it.label)); return }
				is Command.JumpIfNegative -> { if (hand < 0) { runSection(program.sectionIndexOf(it.label)); return } }
				is Command.JumpIfZero -> { if (hand == 0) { runSection(program.sectionIndexOf(it.label)); return } }
				// EXPANSION
				is Command.Load -> { hand = it.value }
				is Command.JumpIfEqual -> { if (hand == floor[it.pointer]) { runSection(program.sectionIndexOf(it.label)); return } }
				is Command.Crash -> { throw RuntimeException("crash command") } // if true fools the compiler to not give a unreachable code error
				else -> errorPrinting("Unknown Command")
			}
		}
		if (index == program.sections.size - 1) return
		runSection(index + 1)
	}
	
	override fun toString(): String {
		var out = ""
		for (i in 0..outboxIndex - 1) {
			val value = outbox[i]
			if (DataConverter.isNum(value)) {
				val num = value
				out += "$num,"
			} else if (DataConverter.isChar(value)) {
				val letter = DataConverter.toChar(value)
				out += letter + ","
			} else {
				System.err.println("Data error: " + value)
			}
		}
		if (out.length == 0) return ""
		return out.substring(0, out.length - 1)
	}
	
}
