package com.mrvelibor.stratego.sound;

public enum Volume {
	
	OFF(0, -6), LOW(1, -6), MEDIUM(64, 0), HIGH(127, 6);
	
	public final int intValue;
	public final float floatValue;
	
	private Volume(int intValue, float floatValue) {
		this.intValue = intValue;
		this.floatValue = floatValue;
	}
	
	public static Volume get(String value) {
		switch(value)
			{
			case "OFF":
				return OFF;
			case "LOW":
				return LOW;
			case "MEDIUM":
				return MEDIUM;
			case "HIGH":
				return HIGH;
				
			default:
				return null;
			}
	}
}
