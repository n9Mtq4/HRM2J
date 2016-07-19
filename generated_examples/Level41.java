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
//package com.n9mtq4.hrm2j.runtime;
public class Level41 extends HrmCpu {
	public Level41(int[] inboxValues, int floorSize, int[] floorValues, int outboxSize) {
		super(inboxValues, floorSize, floorValues, outboxSize);
	}
	public Level41() {
		super(new int[] {1,2,3,4,0,4,3,2,1,0,1,3,3,2,2,1,4,0,4,3,2,1,4,3,2,1,4,3,2,1,0,2,1,3,4,7,8,3,2,1,4,6,0,2,7,3,5,1,2,3,0,4,3,1,2,4,2,3,1,4,0}, 25, new int[] {24,0}, 255);
	}
	private void func_hrmfunc_compile_mainfunc() {
		func_a();
	}
	private void func_a() {
		inbox();
		if (hand == 0) {func_d(); return;};
		copyToPointer(24);
		func_b();
	}
	private void func_b() {
		increment(24);
		func_a();
		return;
	}
	private void func_c() {
		;
		copyFromPointer(22);
		outbox();
		copyFromPointer(24);
		copyToPointer(22);
		func_d();
	}
	private void func_d() {
		;
		decrement(24);
		if (hand < 0) { func_b(); return;};
		copyTo(23);
		func_e();
	}
	private void func_e() {
		copyTo(22);
		func_f();
	}
	private void func_f() {
		decrement(23);
		if (hand < 0) { func_c(); return;};
		copyFromPointer(22);
		subtractPointer(23);
		if (hand < 0) { func_f(); return;};
		;
		copyFrom(23);
		func_e();
		return;
	}
	@Override
	public String run() {
		try {
			func_hrmfunc_compile_mainfunc();
		} catch(Exception e) {
			return toString();
		}
		return toString();
	}
}
