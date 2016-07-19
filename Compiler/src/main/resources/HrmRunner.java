public class HrmRunner$GENERATED_CODE_TIMESTAMP_ID {
	
	public static void main(String[] args) {
//		TODO: arg parsing
		System.out.println(run());
	}
	
	public static String run() {
		HrmRunner$GENERATED_CODE_TIMESTAMP_ID.HrmCpu$impl impl = new HrmRunner$GENERATED_CODE_TIMESTAMP_ID.HrmCpu$impl();
		final String out = impl.run();
		return out;
	}
	
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
	
	public static abstract class HrmCpu {
		
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
				if (isNum(value)) {
					final int num = toInt(value);
					out += num;
				}else if (isChar(value)) {
					final char letter = toChar(value);
					out += letter;
				}else {
					System.err.println("Data error: " + value);
				}
			}
			return out;
		}
		
	}
	
	public static class DataHolder {
		
		private int[] data;
		public int index;
		
		public DataHolder(final int size) {
			
			data = new int[size];
			
		}
		
		public int getNum(final int index) {
			return toInt(data[index]);
		}
		
		public char getChar(final int index) {
			return toChar(data[index]);
		}
		
		public void setNum(final int index, final int value) {
			data[index] = toData(value);
		}
		
		public void setChar(final int index, final char value) {
			data[index] = toData(value);
		}
		
		public void setRaw(final int index, final int value) {
			data[index] = value;
		}
		
		public int getRaw(final int index) {
			return data[index];
		}
		
		public boolean isNum(final int index) {
			return isNum(data[index]);
		}
		
		public boolean isChar(final int index) {
			return isChar(data[index]);
		}
		
		public boolean isEmpty() {
			return index >= getSize() - 1;
		}
		
		public int getSize() {
			return data.length;
		}
		
	}
	
	//GENERATED_CODE_HERE
	
}
