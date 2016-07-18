package com.n9mtq4.hrm2j.runtime;

/**
 * Created by will on 7/17/16 at 3:33 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
public abstract class HrmCpu {
	
	private int[] inboxValues;
	private int floorSize;
	private int[] floorValues;
	private int outboxSize;
	
	protected DataHolder inbox;
	protected DataHolder floor;
	protected DataHolder outbox;
	
	protected int hand;
	
	public HrmCpu(int[] inboxValues, int floorSize, int[] floorValues, int outboxSize) {
		
		this.inboxValues = inboxValues;
		this.floorSize = floorSize;
		this.floorValues = floorValues;
		this.outboxSize = outboxSize;
		
		this.inbox = new DataHolder(inboxValues.length);
		for (int i = 0; i < inboxValues.length; i++) {
			inbox.setRaw(i, inboxValues[i]);
		}
		
		this.floor = new DataHolder(floorSize);
		for (int i = 0; i < floorValues.length; i += 2) {
			floor.setRaw(floorValues[i], floorValues[i + 1]);
		}
		
		this.outbox = new DataHolder(outboxSize);
		
	}
	
	public abstract String run();
	
	public void inbox() {
		hand = inbox.getRaw(inbox.index++);
	}
	
	public void outbox() {
		outbox.setRaw(outbox.index++, hand);
//		TODO: clear hand value, we can't for now, so set to 0
		hand = 0;
	}
	
	public void copyTo(final int pointer) {
		floor.setRaw(pointer, hand);
	}
	
	public void copyToPointer(final int index) {
		final int pointer = floor.getNum(index);
		copyTo(pointer);
	}
	
	/**
	 * copyfrom 1
	 * */
	public void copyFrom(final int pointer) {
		hand = floor.getRaw(pointer);
	}
	
	/**
	 * copyfrom [1]
	 * */
	public void copyFromPointer(final int index) {
		final int pointer = floor.getNum(index);
		copyFrom(pointer);
	}
	
	public void add(final int pointer) {
		hand += floor.getRaw(pointer);
	}
	
	public void addPointer(final int index) {
		final int pointer = floor.getNum(index);
		add(pointer);
	}
	
	public void subtract(final int pointer) {
		hand -= floor.getRaw(pointer);
	}
	
	public void subtractPointer(final int index) {
		final int pointer = floor.getNum(index);
		subtract(pointer);
	}
	
	public void increment(final int pointer) {
		final int value = floor.getRaw(pointer);
		floor.setRaw(pointer, value + 1);
		copyFrom(pointer);
	}
	
	public void decrement(final int pointer) {
		final int value = floor.getRaw(pointer);
		floor.setRaw(pointer, value - 1);
		copyFrom(pointer);
	}
	
//	the jumps are handled by the compiler
	
	@Override
	public String toString() {
		String out = "";
		for (int i = 0; i < outbox.index; i++) {
			final int value = outbox.getRaw(i);
			if (DataConverter.isNum(value)) {
				final int num = DataConverter.toInt(value);
				out += num;
			}else if (DataConverter.isChar(value)) {
				final char letter = DataConverter.toChar(value);
				out += letter;
			}else {
				System.err.println("Data error: " + value);
			}
		}
		return out;
	}
	
}
