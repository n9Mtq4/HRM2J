/*
* THIS COMMENT WAS PLACED BY A HUMAN, NOT THE COMPILER
* THIS CODE WAS ALSO REFORMATTED INTO A SOMEWHAT READABLE FORMAT
* 
* I did not write the source code for this hrm program. That
* code can be found here.
* 
* https://github.com/atesgoral/hrm-solutions/blob/9c92d7137f6a7593ab35389ab284fd3dcebd2a74/solutions/41-Sorting-Floor-34.714/20.651.selection-sniperrifle2004.asm
* 
* */
public class Level41Fast {
	
	private int[] inbox;
	private int inboxIndex = 0;
	private int[] floor;
	private int[] outbox;
	private int outboxIndex = 0;
	private int hand = 0;
	
	
	private static final int CHAR_MASK = 1 << 30;
	private static boolean isNum(final int value) {
		return !isChar(value);
	}
	private static boolean isChar(final int value) {
		return (value & CHAR_MASK) != 0 && (((value & (~CHAR_MASK)) >> 6) ^ 0b01) == 0;
	}
	private static char toChar(final int value) {
		return (char) (value & (~CHAR_MASK));
	}
	
	
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
	
	private static final int[] defaultInboxValues = new int[] {1,2,3,4,0,4,3,2,1,0,1,3,3,2,2,1,4,0,4,3,2,1,4,3,2,1,4,3,2,1,0,2,1,3,4,7,8,3,2,1,4,6,0,2,7,3,5,1,2,3,0,4,3,1,2,4,2,3,1,4,0};
	private static final int defaultFloorSize = 25;
	private static final int[] defaultFloorValues = new int[] {};
	private static final int defaultOutboxSize = 4095;
	public Level41Fast(int[] inboxValues, int floorSize, int[] floorValues, int outboxSize) {
		
		this.inbox = inboxValues;
		this.floor = new int[floorSize];
		for (int i = 0; i < floorValues.length; i += 2) {
			floor[floorValues[i]] = floorValues[i + 1];
		}
		this.outbox = new int[outboxSize];
		
	}
	public Level41Fast() {
		this(defaultInboxValues, defaultFloorSize, defaultFloorValues, defaultOutboxSize);
	}
	public Level41Fast(int[] inboxValues) {
		this(inboxValues, defaultFloorSize, defaultFloorValues, defaultOutboxSize);
	}
	public String run() {
		try {
			func_synthetic_compile_mainfunc();
		} catch (Throwable e) {
			e.printStackTrace();
			return toString();
		}
		return toString();
	}
	public static String sRun() {
		return new Level41Fast().run();
	}
	public static String sRun(int[] inboxValues, int floorSize, int[] floorValues, int outboxSize) {
		return new Level41Fast(inboxValues, floorSize, floorValues, outboxSize).run();
	}
	public static String sRun(int[] inboxValues) {
		return new Level41Fast(inboxValues).run();
	}
	public static void main(String[] args) {
		System.out.println(sRun());
	}
	private void func_synthetic_compile_mainfunc() {
		func_a();
	}
	private void func_a() {
		hand = inbox[inboxIndex++];
		if (hand == 0) { func_d(); return; }
		floor[floor[24]] = hand;
		func_b();
	}
	private void func_b() {
		floor[24]++; hand = floor[24];
		func_a();
		return;
	}
	private void func_c() {
		hand = floor[floor[22]];
		outbox[outboxIndex++] = hand; hand = 0;
		hand = floor[floor[24]];
		floor[floor[22]] = hand;
		func_d();
	}
	private void func_d() {
		floor[24]--; hand = floor[24];
		if (hand < 0) { func_b(); return; }
		floor[23] = hand;
		func_e();
	}
	private void func_e() {
		floor[22] = hand;
		func_f();
	}
	private void func_f() {
		floor[23]--; hand = floor[23];
		if (hand < 0) { func_c(); return; }
		hand = floor[floor[22]];
		hand -= floor[floor[23]];
		if (hand < 0) { func_f(); return; }
		hand = floor[23];
		func_e();
		return;
	}
}
