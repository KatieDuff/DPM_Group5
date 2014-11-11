package BlockLifter;

import InterBlockCommunication.NXTConnect;
import InterBlockCommunication.NXTReceive;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;

public class BlockLifter {
	//Motor variables
	final NXTRegulatedMotor clawMotor = Motor.A;
	final NXTRegulatedMotor liftingMotor = Motor.B;
	
	//ColourSensor variables
	final ColorSensor cs = new ColorSensor(SensorPort.S1);
	
	//TouchSensor variables
	final TouchSensor topTouchSensor = new TouchSensor(SensorPort.S2);
	final TouchSensor bottomTouchSensor = new TouchSensor(SensorPort.S3);
	
	//Block variables
	
	Block[] blocks;
	
	//InterBlock Communication 
	private NXTConnect conn;
	private final NXTReceive rec = new NXTReceive();
	
	/**Determines if it will pick up the block based on the colour
	 * will then pick up the block or not and communicate that to 
	 * the other brick via bluetooth
	 */
	public void main(String[] args) {
		this.blocks = new Block[4];
		try{
			this.conn = new NXTConnect();
		}
		catch(Exception e){}
	}
	
	/**Determines the colour of the block to be picked up*/
	private int determineBlockColour(){
		return 0;
	}
	
	/**picks up the block*/
	private void pickUpBlock(){
		
	}
}
