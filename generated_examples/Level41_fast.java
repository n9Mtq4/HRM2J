/*
* THIS COMMENT WAS PLACED BY A HUMAN, NOT THE COMPILER
* THIS CODE WAS ALSO REFORMATTED INTO A SOMEWHAT READABLE FORMAT
* 
* I did not write the source code for this hrm program. That
* code can be found here.
* 
* https://github.com/atesgoral/hrm-solutions/blob/9c92d7137f6a7593ab35389ab284fd3dcebd2a74/solutions/41-Sorting-Floor-34.714/20.651.selection-sniperrifle2004.asm
* 
* This was also the code used for the benchmark. The benchmark code looked like this
* 
* 
* ```kotlin
* val t = TestProgram()
* var sum = 0
* repeat(1000) {
*     sum += measureTimeMillis {
*         println(t.run())
*     }
* }
* println(sum)
* ```
* 
* */
public class Level41_fast {
	public Level41_fast(int[] inboxValues, int floorSize, int[] floorValues, int outboxSize) {
		
		
		this.inbox = inboxValues;
		
		this.floor = new int[floorSize];
		for (int i = 0; i < floorValues.length; i += 2) {
			floor[floorValues[i]] = floorValues[i + 1];
		}
		
		this.outbox = new int[outboxSize];
		
	}
	
	private static final int CHAR_MASK = 0b10000000000;
	private static boolean isNum(final int value) {
		return (value & CHAR_MASK) == 0;
	}
	private static boolean isChar(final int value) {
		return (value & CHAR_MASK) != 0;
	}
	private static int toInt(final int value) {
		return (value & (~CHAR_MASK));
	}
	private static char toChar(final int value) {
		return (char) (value & (~CHAR_MASK));
	}
	
	private int[] inbox;
	private int inboxIndex = 0;
	private int[] floor;
	private int[] outbox;
	private int outboxIndex = 0;
	private int hand = 0;
	
	@Override
	public String toString() {
		String out = "";
		for (int i = 0; i < outboxIndex; i++) {
			final int value = outbox[i];
			if (isNum(value)) {
				final int num = toInt(value);
				out += num + ",";
			}else if (isChar(value)) {
				final char letter = toChar(value);
				out += letter + ",";
			}else {
				System.err.println("Data error: " + value);
			}
		}
		return out.substring(0, out.length() - 1);
	}
	
	public static void main(String[] args) {
		System.out.println(new Level41_fast().run());
	}
	public Level41_fast() {
		this(new int[] {1,2,3,4,0,4,3,2,1,0,1,3,3,2,2,1,4,0,4,3,2,1,4,3,2,1,4,3,2,1,0,2,1,3,4,7,8,3,2,1,4,6,0,2,7,3,5,1,2,3,0,4,3,1,2,4,2,3,1,4,0}, 25, new int[] {24,0}, 255);
	}
	private void func_hrmfunc_compile_mainfunc() {
		func_a();
	}
	private void func_a() {
		hand = inbox[inboxIndex++];
		if (hand == 0) {func_d(); return;}
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
		if (hand < 0) { func_b(); return;}
		floor[23] = hand;
		func_e();
	}
	private void func_e() {
		floor[22] = hand;
		func_f();
	}
	private void func_f() {
		floor[23]--; hand = floor[23];
		if (hand < 0) { func_c(); return;}
		hand = floor[floor[22]];
		hand -= floor[floor[23]];
		if (hand < 0) { func_f(); return;}
		
		hand = floor[23];
		func_e();
		return;
	}
	public String run() {
		try {
			func_hrmfunc_compile_mainfunc();
		} catch(Exception e) {
			return toString();
		}
		return toString();
	}
}
