package com.n9mtq4.hrm2j.compiler

/**
 * Created by will on 7/24/16 at 12:35 AM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */

private val variables = """
private int[] inbox;
private int inboxIndex = 0;
private int[] floor;
private int[] outbox;
private int outboxIndex = 0;
private int hand = 0;
public Throwable stackTrace = null;
"""
private val dataConverter = """
private static final int CHAR_MASK = 1 << 30;
private static boolean isNum(final int value) {
	return !isChar(value);
}
private static boolean isChar(final int value) {
	return (value & CHAR_MASK) != 0 && ((((value & (~CHAR_MASK)) >> 6) ^ 1) == 0 || (((value & (~CHAR_MASK)) >> 5) ^ 1) == 0);
}
private static char toChar(final int value) {
	return (char) (value & (~CHAR_MASK));
}
"""
private val toStringOutbox = """
@Override
public String toString() {
	String out = "";
	for (int i = 0; i < outboxIndex; i++) {
		final int value = outbox[i];
		if (isNum(value)) {
			final int num = value;
			out += num + ",";
		} else if (isChar(value)) {
			final char letter = toChar(value);
			out += letter + ",";
		} else {
			System.err.println("Data error: " + value);
		}
	}
	return out.substring(0, out.length() - 1);
}
"""
private val constructorBody = """
this.inbox = inboxValues;
this.floor = new int[floorSize];
for (int i = 0; i < floorValues.length; i += 2) {
	floor[floorValues[i]] = floorValues[i + 1];
}
this.outbox = new int[outboxSize];
"""

fun <T> fastCompile(program: Program, className: String, packageName: String, inboxValues: IntArray, floorSize: Int, floorValues: IntArray, outboxSize: Int, stackTrace: Boolean = false, generate: (String) -> T) {
	
	val gl = generate
	
	fun generateHeader() {
		
		// class 
		if (!packageName.isBlank()) gl("package $packageName;")
		gl("public class $className {")
		
		// the class body
		gl(variables)
		gl(dataConverter)
		gl(toStringOutbox)
		
		// default constructor values
		gl("private static final int[] defaultInboxValues = new int[] {${inboxValues.joinToString(separator = ",")}};")
		gl("private static final int defaultFloorSize = $floorSize;")
		gl("private static final int[] defaultFloorValues = new int[] {${floorValues.joinToString(separator = ",")}};")
		gl("private static final int defaultOutboxSize = $outboxSize;")
		
		// all constructor
		gl("public $className(int[] inboxValues, int floorSize, int[] floorValues, int outboxSize) {")
		gl(constructorBody)
		gl("}")
		
		// default constructor
		gl("public $className() {")
		gl("this(defaultInboxValues, defaultFloorSize, defaultFloorValues, defaultOutboxSize);")
		gl("}")
		
		// inbox constructor
		gl("public $className(int[] inboxValues) {")
		gl("this(inboxValues, defaultFloorSize, defaultFloorValues, defaultOutboxSize);")
		gl("}")
		
		// run function
		gl("public String run() {")
		gl("try {")
		gl("func_${program.sections[0].label}();")
		gl("} catch (Throwable e) {")
		if (stackTrace) gl("e.printStackTrace();")
		if (stackTrace) gl("this.stackTrace = e;")
		gl("return toString();")
		gl("}")
		gl("return toString();")
		gl("}")
		
		// default run function
		gl("public static String sRun() {")
		gl("return new $className().run();")
		gl("}")
		
		// all provided run function
		gl("public static String sRun(int[] inboxValues, int floorSize, int[] floorValues, int outboxSize) {")
		gl("return new $className(inboxValues, floorSize, floorValues, outboxSize).run();")
		gl("}")
		
		// only inboxValues
		gl("public static String sRun(int[] inboxValues) {")
		gl("return new $className(inboxValues).run();")
		gl("}")
		
		// main function
		gl("public static void main(String[] args) {")
		gl("System.out.println(sRun());")
		gl("}")
		
	}
	
	fun generateBody() {
		
		program.sections.forEachIndexed { i, it ->
			gl("private void func_${it.label}() {")
			it.commands.forEach { 
				gl(when(it) {
					is Command.Inbox -> "hand = inbox[inboxIndex++];"
					is Command.Outbox -> "outbox[outboxIndex++] = hand; hand = 0;"
					is Command.CopyToBox -> "floor[${it.pointer}] = hand;"
					is Command.CopyToBoxValue -> "floor[floor[${it.pointer}]] = hand;"
					is Command.CopyFromBox -> "hand = floor[${it.pointer}];"
					is Command.CopyFromBoxValue -> "hand = floor[floor[${it.pointer}]];"
					is Command.AddToBox -> "hand += floor[${it.pointer}];"
					is Command.AddToBoxValue -> "hand += floor[floor[${it.pointer}]];"
					is Command.SubToBox -> "hand -= floor[${it.pointer}];"
					is Command.SubToBoxValue -> "hand -= floor[floor[${it.pointer}]];"
					is Command.Increment -> "floor[${it.pointer}]++; hand = floor[${it.pointer}];"
					is Command.Decrement -> "floor[${it.pointer}]--; hand = floor[${it.pointer}];"
					is Command.Jump -> "func_${it.label}();\nreturn;"
					is Command.JumpIfNegative -> "if (hand < 0) { func_${it.label}(); return; }"
					is Command.JumpIfZero -> "if (hand == 0) { func_${it.label}(); return; }"
					// expanded commands
					is Command.Load -> "hand = ${it.value};"
					is Command.JumpIfEqual -> "if (hand == floor[${it.pointer}]) { func_${it.label}(); return; }"
					is Command.Crash -> "if (true) throw new RuntimeException(\"crash command\");" // if true fools the compiler to not give a unreachable code error
				})
			}
			// if needed, call the next function in the chain
			if (i < program.sections.size - 1)
				if (program.sections[i].commands.size <= 0 || program.sections[i].commands.last() !is Command.Jump) 
					gl("func_${program.sections[i + 1].label}();")
			gl("}")
		}
		
	}
	
	fun generateCloser() {
		// close class
		gl("}")
	}
	
	generateHeader()
	generateBody()
	generateCloser()
	
}
