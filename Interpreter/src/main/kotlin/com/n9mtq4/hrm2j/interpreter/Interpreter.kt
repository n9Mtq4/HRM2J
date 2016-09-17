package com.n9mtq4.hrm2j.interpreter

import com.n9mtq4.hrm2j.parser.Command
import com.n9mtq4.hrm2j.parser.Program
import com.n9mtq4.hrm2j.parser.sectionIndexOf

/**
 * Created by will on 7/28/16 at 12:08 AM.
 * 
 * OMG! Intellij wants tabs and spaces in this indented header
 * 
 * @author Will "n9Mtq4" Bresnahan
 */
class Interpreter(val program: Program, 
				  inboxValues: IntArray, 
				  floorSize: Int, 
				  floorValues: IntArray, 
				  outboxSize: Int, 
				  val printStackTrace: Boolean = true, 
				  val errorPrinting: (String) -> Unit = {}) : 
		HrmRuntime(inboxValues, floorSize, floorValues, outboxSize) {
	
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
	
}
