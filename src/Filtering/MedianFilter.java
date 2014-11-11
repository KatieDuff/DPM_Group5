package Filtering;

import lejos.nxt.UltrasonicSensor;

public class MedianFilter implements SensorFilter, Runnable {
	private final UltrasonicSensor us;
	
	/**Constructor for the MedianFilter*/
	public MedianFilter(UltrasonicSensor us){
		this.us = us;
	}
	
	public void run() {	
	}
	
	public int getFilteredValue() {
		return 0;
	}

}
