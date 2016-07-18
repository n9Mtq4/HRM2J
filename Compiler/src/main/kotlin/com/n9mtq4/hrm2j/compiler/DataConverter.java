package com.n9mtq4.hrm2j.compiler;

/**
 * Created by will on 7/17/16 at 3:13 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
public class DataConverter {
	
	private static final int CHAR_MASK = 0b10000000000; // TODO: can add zeros to increase value
	
	public static int toInt(final int value) {
		return (value & (~CHAR_MASK));
	}
	
	public static char toChar(final int value) {
		return (char) (value & (~CHAR_MASK));
	}
	
	public static int toData(final int value) {
		return value;
	}
	
	public static int toData(final char value) {
		return value | CHAR_MASK;
	}
	
	public static boolean isNum(final int value) {
		return (value & CHAR_MASK) == 0;
	}
	
	public static boolean isChar(final int value) {
		return (value & CHAR_MASK) != 0;
	}
	
}
