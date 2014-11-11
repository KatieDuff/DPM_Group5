package SearchAndRescue;

import Filtering.DifferentialFilter;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;

/** Performs odometer correction based on gridlines detected by the light sensor */
public class OdometerCorrection {
	private static final long CORRECTION_PERIOD = 10, LINE_ERROR_THRESHOLD = 3;
	private static double TILE_LENGTH = 30.48;
	private final Odometer odo;
	private final ColorSensor cs;
	private final double SENSOR_TO_CENTER = 11.7;
	private final DifferentialFilter filter;

	// constructor
	public OdometerCorrection(Odometer odo) {
		this.odo = odo;
		this.cs = new ColorSensor(SensorPort.S1);
		this.filter = new DifferentialFilter(this.cs);
		this.cs.setFloodlight(true);
	}

	public void odometryCorrection() {

	}

	/** Run method required for thread*/
	
	public void run() {
		long correctionStart, correctionEnd;
		while (true) {
			correctionStart = System.currentTimeMillis();
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}
