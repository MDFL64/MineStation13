package net.c0gg.ms13;

import java.util.Random;

public enum HackGroup {
	AIRLOCK;
	
	private byte[] functionMappings;
	
	private HackGroup() {
		functionMappings = new byte[] {0,1,2,3,4,5,6,7};
		shuffleArray(functionMappings);
	}
	
	public byte getFunction(byte wire) {
		for (byte i=0;i<8;i++) {
			if (functionMappings[i]==wire) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean isFunctionDisabled(byte function,byte wires) {
		return (wires & (1<<functionMappings[function])) !=0;
	}
	
	//Fisher–Yates yoloswag
	private static void shuffleArray(byte[] ar)
	{
		Random rnd = new Random();
		for (int i = ar.length - 1; i >= 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			byte a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}
}





