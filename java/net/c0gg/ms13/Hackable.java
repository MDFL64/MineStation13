package net.c0gg.ms13;

public interface Hackable {
	public byte getWires();
	
	public void setWires(byte wires);
	
	public void getIndicators(byte[] indicators);
	
	public void functionDisabled(byte f);
	
	public void functionRestored(byte f);
	
	public void functionPulsed(byte f);
	
	public HackGroup getGroup();
}