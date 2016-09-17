package com.n9mtq4.hrm2j.interpreter

import com.n9mtq4.hrm2j.parser.DataConverter

/**
 * Created by will on 8/23/16 at 2:50 AM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
open class HrmRuntime(inboxValues: IntArray, floorSize: Int, floorValues: IntArray, outboxSize: Int) {
	
	val inbox: IntArray = inboxValues
	var inboxIndex = 0
	val floor: IntArray = IntArray(floorSize) { 0 }
	val outbox: IntArray = IntArray(outboxSize)
	var outboxIndex = 0
	var hand: Int = 0
	var stackTrace: Throwable? = null
	var outputStringCache = ""
	
	init {
		var i = 0
		while (i < floorValues.size) {
			floor[floorValues[i]] = floorValues[i + 1]
			i += 2
		}
	}
	
	fun applyOutputToStringCache(commas: Boolean = true) {
		var out = ""
		for (i in 0..outboxIndex - 1) {
			val value = outbox[i]
			if (DataConverter.isNum(value)) {
				val num = value
				out += num.toString() + if (commas) "," else ""
			} else if (DataConverter.isChar(value)) {
				val letter = DataConverter.toChar(value)
				out += letter + if (commas) "," else ""
			} else {
				System.err.println("Data error: " + value)
			}
		}
		outputStringCache += if (out.length == 0) "" else out.substring(0, out.length - 1)
		// reset the output position
		outboxIndex = 0
	}
	
	override fun toString(): String {
		applyOutputToStringCache()
		return outputStringCache
	}
	
}
