package com.n9mtq4.hrm2j.runtime;

/**
 * Created by will on 7/17/16 at 2:21 PM.
 *
 * @author Will "n9Mtq4" Bresnahan
 */
public class DataHolder {
	
	private int[] data;
	public int index;
	
	public DataHolder(final int size) {
		
		data = new int[size];
		
	}
	
	public int getNum(final int index) {
		return DataConverter.toInt(data[index]);
	}
	
	public char getChar(final int index) {
		return DataConverter.toChar(data[index]);
	}
	
	public void setNum(final int index, final int value) {
		data[index] = DataConverter.toData(value);
	}
	
	public void setChar(final int index, final char value) {
		data[index] = DataConverter.toData(value);
	}
	
	public void setRaw(final int index, final int value) {
		data[index] = value;
	}
	
	public int getRaw(final int index) {
		return data[index];
	}
	
	public boolean isNum(final int index) {
		return DataConverter.isNum(data[index]);
	}
	
	public boolean isChar(final int index) {
		return DataConverter.isChar(data[index]);
	}
	
	public boolean isEmpty() {
		return index >= getSize() - 1;
	}
	
	public int getSize() {
		return data.length;
	}
	
}
