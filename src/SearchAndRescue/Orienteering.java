package SearchAndRescue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import lejos.nxt.UltrasonicSensor;
 
/**
 * left and front sensor
 * Just change the initialLocation variable value and run the simulator
 *
 */
 
public class Orienteering {
    
    private final TwoWheeledRobot robot;
    private final UltrasonicSensor leftSensor;
    private final UltrasonicSensor frontSensor; 
    private final int numberOfSensors = 2;
 
 
    // Filtering Constants
    private final int FILTER_OUT = 20;
    // Filtering Variables
    public int[] dist = new int[5];
    public int distCount = 0;
    public int filterControl, previousDistance, distance;
	private final double WALL_DISTANCE = 30;
     
    char nextMove;
    int numberOfDecisions = 0;
    double decisionAverage = 0;
     
    //Map variables
    //Map from the project specification
    Map mInfo = new Map(12, 12, new int[][]  {{4, 1},{9, 2},{6, 3},{3, 4},{6, 5},{1, 7},{7, 8},{4, 10}});
     
    //Map from lab 5
//  MapInfo mInfo = new MapInfo(4, 4, new int[][]  {{1, 0},{2, 2},{3, 2},{0, 3}});
 
    //Create map using 2d array
    Location[][] map;
    ArrayList<Character> stepsLeftSensor = new ArrayList<Character>();      //Keep track of every movements of the left sensor
    ArrayList<Character> stepsMiddleSensor = new ArrayList<Character>();    //Keep track of every movements of the middle sensor
    static int arrowCounter = 0;                                            //Count the number of arrows that are still available
     
 
     
    //Constructor
    public Orienteering(UltrasonicSensor ls, UltrasonicSensor fs, TwoWheeledRobot robot){
    	this.leftSensor = ls;
    	this.frontSensor = fs;
    	this.robot = robot;
    }
     
     
     
    public void StartLocalization(){

        
        while(checkIfFound() == false){
            //Checks if there is a wall
            stepsLeftSensor.add(isThereAWall(leftSensor));
            stepsMiddleSensor.add(isThereAWall(frontSensor));
            locationsAnalysis();
             
            //If one possible starting position left, break the loop
            if (checkIfFound()) break;
             
            //If there is a wall in front of the robot
            if (isThereAWall(frontSensor) == 'y'){
                //turn left or right; 
                turnLeftOrRight();
                numberOfDecisions++;
                 
            }else{
                //turn left or right or move forward
                turnLeftOrRightOrForward();
                numberOfDecisions++;
                 
            }   
        }

    }
 
    // Robot Decision
    private void decision(String d){
        if (d.equals("forward")){
             stepsMiddleSensor.add(moveForward());
             stepsLeftSensor.add(moveRight());
             robot.moveRobot(30);

             
        }else if (d.equals("turnLeft")){
            stepsMiddleSensor.add(rotate(-90));
             stepsLeftSensor.add(rotate(-90));
             robot.rotateRobot(-90);

              
        }else if(d.equals("turnRight")){
            stepsMiddleSensor.add(rotate(90));
             stepsLeftSensor.add(rotate(90));
             robot.rotateRobot(90);

              
        }
    }
     
     
    //Move forward
    private char moveForward() {
        return 'f';
    }
    //Move left
    private char moveLeft() {
        return 'a';
    }
    //Move right
    private char moveRight() {
        return 'd';
    }
     
    //Rotate left or right
    private char rotate(int i) {
        if (i == 90) return 'r';
        else return 'l';
    }
     
    //Observe if there is a wall
    private char wallDistance(int Dist){
        if (Dist < 20) return 'y';
        else return 'n';
    }
     
    //Trigger analysis for each location
    public void locationsAnalysis(){
        arrowCounter = 0;
//      System.out.print("\n\nSTEP NUMBER: "+stepsMiddleSensor.size() +", " );
//      System.out.print("("+ stepsLeftSensor.get(stepsLeftSensor.size() - 1)+ ", " );
//      System.out.print(stepsMiddleSensor.get(stepsMiddleSensor.size() - 1)+ ") " );
         
        //Trigger analysis for the left sensor
            for(int j = 0; j < mInfo.sizeY; j++){
                for(int i = 0; i < mInfo.sizeX; i++){
                    if (map[i][j] != null){
                    map[i][j].analyse(stepsLeftSensor.get(stepsLeftSensor.size() - 1), 0);
                    map[i][j].analyse(stepsMiddleSensor.get(stepsMiddleSensor.size() - 1), 1);
                     
                    }
                }
            }
 
 
             
    }
     
    //Check if starting location was found
    public boolean checkIfFound(){
        return (arrowCounter == 1);
    }
     
    //Create locations 2Darray
    public void initializeLocations(Map m){
        //Populate the 2d array
 
        map = new Location[m.sizeX][m.sizeY];
         
        for(int j = 0; j < m.sizeY; j++){
            mainloop:
            for(int i = 0; i < m.sizeX; i++){
                //Set blocked tiles to null
                for(int b = 0; b < m.obstacles.length; b++){
                    if (i == m.obstacles[b][0] && j == m.obstacles[b][1]){
                        map[i][j] = null;
                        continue mainloop;
                    }
                }
                    map[i][j] = new Location(i,j);
                     
                    //Set current locations for each arrow as the starting position of each location when created
                    // 0 1 2 refer respectively to left, middle, right sensors
                    for(int k = 0; k<4 ;k++){
                        map[i][j].currentLocation[0][k] = map[i][j];
                        map[i][j].currentLocation[1][k] = map[i][j];
                    }
            }
        }
         
        //Set adjacent locations values for each location
        for(int j = 0; j < m.sizeY; j++){
            for(int i = 0; i < m.sizeX; i++){
//              System.out.println(i +", " + j+ ", " + map[i][j]);
                if (map[i][j] != null){
                    if(j < m.sizeY-1) map[i][j].adjacentLocations[0] = map[i][j+1];
                    if(i < m.sizeX-1) map[i][j].adjacentLocations[1] = map[i+1][j];
                    if(j > 0) map[i][j].adjacentLocations[2] = map[i][j-1];
                    if(i > 0) map[i][j].adjacentLocations[3] = map[i-1][j];
                }
            }
        }
    }
    // Return shortest path in an arrayList
    public ArrayList<Location> shortestPath(Location a, Location b){
         
        Queue<Location> q = new Queue<Location>();
        ArrayList<Location> path = new ArrayList<Location>();
         
        q.push(a);
        a.visited = true;
         
        while (!q.isEmpty()){
            //Current location
            Location current = (Location) q.pop();
             
            //If the current location is equal to final location
            if(current.gridX == b.gridX && current.gridY == b.gridY){ 
                //Back track to get the path
                for(Location l = current; l != null; l = l.previous) {
                    path.add(l);
               }
               break;
                }
            //For each adjacent locations to current location,
            //If it's not null or has never been visited, then add to queue
            for(int i = 0; i < current.adjacentLocations.length; i++){
                if (current.adjacentLocations[i] != null && current.adjacentLocations[i].visited == false){
                    q.push(current.adjacentLocations[i]);
                    current.adjacentLocations[i].visited = true;
                    current.adjacentLocations[i].previous = current;
                }
            }
        }
        //Return path
        return path;
    }
    // Return starting position
    public int[] getInitialLocation(){
        int[] a = new int[3];
        for(int j = 0; j < mInfo.sizeY; j++){
            for(int i = 0; i < mInfo.sizeX; i++){
                if (map[i][j] != null){
                    for (int k = 0; k < 4; k++){
                        if (map[i][j].initialOrientations[k] == true){
                            a[0] = i;
                            a[1] = j;
                            a[2] = k;
                            break;
                        }
                    }
                }
            }
        }
        return a;
    }
    //Return current position
    public int[] getCurrentLocation(){
        int[] current = getInitialLocation();
        int stepsSize = stepsMiddleSensor.size();
         
        for (int i = 0; i < stepsSize; i++){
 
            if (stepsMiddleSensor.get(i).equals('f')){
                if(current[2] == 0) current[1] += 1;
                else if(current[2] == 1)current[0] += 1;
                else if(current[2] == 2)current[1] -= 1;
                else if(current[2] == 3)current[0] -= 1;
            }
            else if (stepsMiddleSensor.get(i).equals('l')){
                if(current[2] == 0)current[2] = 3;
                else current[2] -= 1;
            }
            else if (stepsMiddleSensor.get(i).equals('r')){
                if(current[2] == 3)current[2] = 0;
                else current[2] += 1;
            }
        }
         
        return current;
    }
     
    //turn left or right;
    public void turnLeftOrRight(){
         
             
            int[] left = {0,0,0};
            int[] right = {0,0,0};
             
            //Trigger analysis for the left and middle sensor
            for (int k = 0; k < numberOfSensors; k++) {
                for (int j = 0; j < mInfo.sizeY; j++) {
                    for (int i = 0; i < mInfo.sizeX; i++) {
                        if (map[i][j] != null) {
 
                            left[1] += map[i][j].numberOfPositionsRemoved(true,'l', k);
                            left[2] += map[i][j].numberOfPositionsRemoved(false,'l', k);
 
                            right[1] += map[i][j].numberOfPositionsRemoved(true,'r', k);
                            right[2] += map[i][j].numberOfPositionsRemoved(false,'r', k);
                        }
 
                    }
                }
                 
                left[0] += Math.abs(left[1] - left[2]);
                right[0] += Math.abs(right[1] - right[2]);
                 
                 
                //Reset values for next sensor
                left[1] =0;
                left[2] =0;
                right[1] =0;
                right[2] =0;
 
            }
             
            //If both left and right direction are equally good, pick the one without a wall
            if(left[0] == right[0]){
                if (checkForWall(leftSensor)) decision("turnRight");
                else decision("turnLeft");
            }
            else if (left[0] < right[0])
                decision("turnLeft");
             
            else
                decision("turnRight"); 
             
            locationsAnalysis();
        }
  
 
    
     
    public void turnLeftOrRightOrForward(){
         

            int[] left = {0,0,0};
            int[] forward = {0,0,0};
            int[] right = {0,0,0};
             
            //Trigger analysis for the left sensor
            for (int k = 0; k < numberOfSensors; k++) {
                for (int j = 0; j < mInfo.sizeY; j++) {
                    for (int i = 0; i < mInfo.sizeX; i++) {
                        if (map[i][j] != null) {
 
                            left[1] += map[i][j].numberOfPositionsRemoved(true,'l', k);
                            left[2] += map[i][j].numberOfPositionsRemoved(false,'l', k);
 
                            right[1] += map[i][j].numberOfPositionsRemoved(true,'r', k);
                            right[2] += map[i][j].numberOfPositionsRemoved(false,'r', k);
                             
                            forward[1] += map[i][j].numberOfPositionsRemoved(true,'f', k);
                            forward[2] += map[i][j].numberOfPositionsRemoved(false,'f', k);
                        }
 
                    }
                }
                 
                left[0] += Math.abs(left[1] - left[2]);
                right[0] += Math.abs(right[1] - right[2]);
                forward[0] += Math.abs(forward[1] - forward[2]);    
                 
                //Reset values for next sensor
                left[1] =0;
                left[2] =0;
                right[1] =0;
                right[2] =0;
             
                forward[1] =0;
                forward[2] =0;
                 
 
            }
 
            if(forward[0] <= right[0] && forward[0] <= left[0]){
                decision("forward");     
            }
            //If both left and right direction are equally good, pick the one without a wall
            else if(left[0] == right[0]){
                if (checkForWall(leftSensor)) decision("turnRight");
                else decision("turnLeft");
            }
            else if (left[0] < right[0])
                decision("turnLeft");
            else
                decision("turnRight");
             
            locationsAnalysis();
        }
 
     
    //Return if the sensor is seeing a wall
    //true -> there is a wall, false -> there is no wall
    public boolean checkForWall(UltrasonicSensor us){
         
    		if (isThereAWall(us) == 'y')
    			return true;
    		else
    			 return false;
    
    }

  //Get Distance
  	private int getFilteredData(UltrasonicSensor us) {
  		int distance;
  		// do a ping
  		us.ping();

  		// wait for the ping to complete
  		try {
  			Thread.sleep(50);
  		} catch (InterruptedException e) {
  		}

  		// there will be a delay here
  		distance = us.getDistance();
  		// Filter set distance var to 255 if it has been repeated 4 times
  		if (distance == 255 && previousDistance == 255
  				&& filterControl < FILTER_OUT) {
  			// bad value, do not set the distance var, but increment
  			// repeated
  			// 255 counter
  			filterControl++;
  		} else if (distance == 255 && previousDistance == 255) {
  			// true 255, set the distance to 255
  			this.distance = distance;
  		} else if (distance == 255) {
  			// distance of 255 is not repeated
  			previousDistance = distance;
  		} else {
  			// distance went below 255, therefore reset everything
  			filterControl = 0;
  			previousDistance = distance;
  			this.distance = distance;
  		}
  		return this.distance;
  	}
  	
  	//Get median of an array
  	private int getMedian(int [] a){
  		Arrays.sort(a);
  		if (a.length % 2 == 0)
  		   return (a[a.length/2] + a[a.length/2 - 1])/2;
  		else
  		   return a[a.length/2];
  	}
  	
  //Observe if there is a wall
  	private char isThereAWall(UltrasonicSensor us){
  		int[] wallDistances = new int[5];
  		for (int i = 0; i < wallDistances.length; i++) {
  			try {
  				Thread.sleep(50);
  			} catch (InterruptedException e) {
  			}
  			wallDistances[i] = getFilteredData(us);
  		}
  		int wallDistance = getMedian(wallDistances);
  		if (wallDistance < WALL_DISTANCE) {
  			return 'y';
  		}
  		return 'n';
  	}
  	
}



