package SearchAndRescue;
import InterBlockCommunication.NXTConnect;
import InterBlockCommunication.NXTReceive;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class SearchAndRescue {
	Map map; 
	Orienteering ori;
	
	//InterBlock Communication 
	private NXTConnect conn;
	private final NXTReceive rec = new NXTReceive();

	public void main(String[] args) {
		generateMap();
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		this.ori = new Orienteering(odo, us, map);
		
		try{
			this.conn = new NXTConnect();
		}
		catch(Exception e){}
		
	}
	/**Get the map form the Bluetooth connection and generate a map*/
	private void generateMap(){
		int[][] blocks = new int[4][4];
		map = new Map(4,4,blocks);
	}
	
	/**Travel to a specified location in the map*/
	private void travelTo(int x, int y){
		
	}
	
	/**Communicate to the other brick via bluetooth to pick up block*/
	private void pickUpBlock(){
		
	}
	
}
