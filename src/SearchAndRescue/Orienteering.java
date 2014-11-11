package SearchAndRescue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;

import Filtering.MedianFilter;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.UltrasonicSensor;

import java.util.Collections;
import java.util.LinkedList;
 
/**
 * back and front sensor
 * Just change the initialLocation variable value and run the simulator
 *
 */
 
 
public class Orienteering {
    public enum algorithmType {logN, STOCHASTIC};
     
    private final algorithmType algType;
    private int numberOfSensors = 2;
 
 
    // Filtering Constants
    private final int FILTER_OUT = 20;
    // Filtering Variables
    public int[] dist = new int[5];
    public int distCount = 0;
    public int filterControl, previousDistance, distance;
     
    public int[] currentLocationInfo = new int[3];
    char nextMove;
    int numberOfDecisions = 0;
    double decisionAverage = 0;
    Random rn = new Random();
     
    //Map variables
    //Map from the project specification
    Map mInfo = new Map(12, 12, new int[][]  {{4, 1},{9, 2},{6, 3},{3, 4},{6, 5},{1, 7},{7, 8},{4, 10}});
     
    //Map from lab 5
//  MapInfo mInfo = new MapInfo(4, 4, new int[][]  {{1, 0},{2, 2},{3, 2},{0, 3}});
 
    //Create map using 2d array
    Location[][] map;
    ArrayList<Character> stepsBackSensor = new ArrayList<Character>();      //Keep track of every movements of the left sensor
    ArrayList<Character> stepsMiddleSensor = new ArrayList<Character>();    //Keep track of every movements of the middle sensor
    static int arrowCounter = 0;                                            //Count the number of arrows that are still available
     
 
     
    //Constructor
    public Orienteering(algorithmType algType){
        this.algType = algorithmType.STOCHASTIC;
    }
     
     
     
    public void StartLocalization(){
         
          
        testAllPositions();
 
 
    }
 
    // Robot Decision
    private void decision(String d){
        if (d.equals("forward")){
             stepsMiddleSensor.add(moveForward());
             stepsBackSensor.add(moveBack());
             
             //Adjust the current location position
             if(currentLocationInfo[2] == 0){
                currentLocationInfo[1] += 1;        
            }else if(currentLocationInfo[2] == 1){
                currentLocationInfo[0] += 1;
            }else if(currentLocationInfo[2] == 2){
                currentLocationInfo[1] -= 1;
            }else{
                currentLocationInfo[0] -= 1;
                }
 
        }else if (d.equals("turnLeft")){
            stepsMiddleSensor.add(rotate(-90));
             stepsBackSensor.add(rotate(-90));
              
             //adjust the current location orientation
             if(currentLocationInfo[2] == 0) currentLocationInfo[2] = 3;
             else currentLocationInfo[2] -= 1;
              
        }else if(d.equals("turnRight")){
            stepsMiddleSensor.add(rotate(90));
             stepsBackSensor.add(rotate(90));
              
             //adjust the current location orientation
             if(currentLocationInfo[2] == 3) currentLocationInfo[2] = 0;
             else currentLocationInfo[2] += 1;
              
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
    //Move back
    private char moveBack() {
        return 'b';
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
//      System.out.print("("+ stepsBackSensor.get(stepsBackSensor.size() - 1)+ ", " );
//      System.out.print(stepsMiddleSensor.get(stepsMiddleSensor.size() - 1)+ ") " );
         
        //Trigger analysis for the left sensor
            for(int j = 0; j < mInfo.sizeY; j++){
                for(int i = 0; i < mInfo.sizeX; i++){
                    if (map[i][j] != null){
                    map[i][j].analyse(stepsBackSensor.get(stepsBackSensor.size() - 1), 0);
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
                         
                        map[i][j].currentOrientation[0][k] = k;
                        map[i][j].currentOrientation[1][k] = k;
                         
                        map[i][j].initialOrientations[k] = true;
 
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
         
        Queue<Location> q = new LinkedList<Location>();
        ArrayList<Location> path = new ArrayList<Location>();
         
        q.add(a);
        a.visited = true;
         
        while (!q.isEmpty()){
            //Current location
            Location current = q.remove();
             
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
                    q.add(current.adjacentLocations[i]);
                    current.adjacentLocations[i].visited = true;
                    current.adjacentLocations[i].previous = current;
                }
            }
        }
        //Return path
        Collections.reverse(path);
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
         
        //Choose between stochastic and logN
        if (algType == algorithmType.STOCHASTIC){
             
            //turn left or right
            nextMove = random(new char[] {'l', 'r'});
            if (nextMove == 'l')    
                decision("turnLeft");
            else if (nextMove == 'r')                   
                decision("turnRight");           
             
             locationsAnalysis();
        }
        else{
             
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
                if (checkForWall(0)) decision("turnRight");
                else decision("turnLeft");
            }
            else if (left[0] < right[0])
                decision("turnLeft");
             
            else
                decision("turnRight"); 
             
            locationsAnalysis();
        }
 
    }
     
    public void turnLeftOrRightOrForward(){
         
        //Choose between stochastic and logN
        if (algType == algorithmType.STOCHASTIC){
             
            //turn left or right or move forward
            nextMove = random(new char[] {'l',  'f', 'r'});
            if (nextMove == 'l')    
                decision("turnLeft");
            else if (nextMove == 'r')                   
                decision("turnRight");         
            else
                decision("forward");     
             
             locationsAnalysis();
        }
        else{
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
                if (checkForWall(0)) decision("turnRight");
                else decision("turnLeft");
            }
            else if (left[0] < right[0])
                decision("turnLeft");
            else
                decision("turnRight");
             
            locationsAnalysis();
        }
 
 
    }
    //Determines if there is a wall or not in front of each sensor (only for the simulator)
    //add trigger a location analysis
    //It uses the current location since we know it but the robot does not.
    public void sensorAnalyse() {
 
        boolean b, m;
         
        b = checkForWall(0);
        m = checkForWall(1);
 
 
 
        if (b) stepsBackSensor.add(wallDistance(15));
        else stepsBackSensor.add(wallDistance(35));
         
        if (m) stepsMiddleSensor.add(wallDistance(15));
        else stepsMiddleSensor.add(wallDistance(35));
 
        locationsAnalysis();
//        System.out.println("####################### Found? : " + checkIfFound());
         
    }
     
    //Return if the sensor is seeing a wall
    // sensor = 0 -> back sensor
    // sensor = 1 -> middle sensor
    // sensor = 2 -> right sensor
    //true -> there is a wall, false -> there is no wall
    public boolean checkForWall(int sensor){
         
        //left sensor
        if(sensor == 0){
                if (map[currentLocationInfo[0]][currentLocationInfo[1]].adjacentLocations[(currentLocationInfo[2] + 2)%4] == null){
                    return true;
                }
                else
                    return false;
        }
        //middle sensor
        else if(sensor == 1){
            if (map[currentLocationInfo[0]][currentLocationInfo[1]].adjacentLocations[currentLocationInfo[2]] == null){
                return true;
            }
            else
                return false;
             
        }
        //right sensor
        else{
            if (map[currentLocationInfo[0]][currentLocationInfo[1]].adjacentLocations[(currentLocationInfo[2] + 1)%4] == null){
                return true;
            }
            else
                return false;
        }
 
    }
     
    //Test all positions and prints average
    public void testAllPositions(){
         System.out.println("Results:");
             
            for(int x = 0; x < mInfo.sizeX; x ++){
                loop:
                for (int y = 0; y < mInfo.sizeY; y++){
                    for(int theta = 0; theta < 4; theta++){
                        for(int b = 0; b < mInfo.obstacles.length; b++){
                            if (x == mInfo.obstacles[b][0] && y == mInfo.obstacles[b][1]) break loop;
                        }
                    stepsBackSensor.clear();
                    stepsMiddleSensor.clear();
                    numberOfDecisions = 0;
                    arrowCounter = 0;
 
                    currentLocationInfo[0] = x;
                    currentLocationInfo[1] = y;
                    currentLocationInfo[2] = theta;
                     
                    initializeLocations(mInfo);
                     
                    //Add a left rotation for the left sensor and a right rotation for the right sensor
                    //to match the current orientation of the robot.
                    stepsBackSensor.add(rotate(-90));
 
                     
                    //Analyse the left sensor
                    for(int j = 0; j < mInfo.sizeY; j++){
                        for(int i = 0; i < mInfo.sizeX; i++){
                            if (map[i][j] != null)
                            map[i][j].analyse(stepsBackSensor.get(stepsBackSensor.size() - 1), 0);
                        }
                    }
                     
                     
                    //Add a left rotation for the left sensor and a right rotation for the right sensor
                    //to match the current orientation of the robot.
                    stepsBackSensor.add(rotate(-90));
 
                     
                    //Analyse the left sensor
                    for(int j = 0; j < mInfo.sizeY; j++){
                        for(int i = 0; i < mInfo.sizeX; i++){
                            if (map[i][j] != null)
                            map[i][j].analyse(stepsBackSensor.get(stepsBackSensor.size() - 1), 0);
                        }
                    }
 
                     
                    while(checkIfFound() == false){
                        //Checks if there is a wall
                        sensorAnalyse();
                         
                         
                        //If one possible starting position left, break the loop
                        if (checkIfFound()) break;
                         
                        //If there is a wall in front of the robot
                        if (map[currentLocationInfo[0]][currentLocationInfo[1]].adjacentLocations[currentLocationInfo[2]] == null){
                            //turn left or right; 
                            turnLeftOrRight();
                            numberOfDecisions++;
                             
                        }else{
                            //turn left or right or move forward
                            turnLeftOrRightOrForward();
                            numberOfDecisions++;
                             
                        }   
                         
                        }
 
 
                      System.out.print("\nInitial location: " + Arrays.toString(getInitialLocation()));
                      System.out.print("\tCurrent location: " + Arrays.toString(getCurrentLocation()));
                      System.out.print("\tNumber of decisions: " + numberOfDecisions +"\t\t");
                      decisionAverage += numberOfDecisions;
                       
                    //Print the decisions taken by the robot
                      for(int i = 0; i < stepsMiddleSensor.size(); i++){
                        if(stepsMiddleSensor.get(i).equals('y') || stepsMiddleSensor.get(i).equals('n'));
                        else System.out.print(stepsMiddleSensor.get(i) + " ");
 
                        }
                    }
                }   
            }
             
            System.out.println("\nDecision average: " + decisionAverage/544);
    }
 
    //Test one particular position with this format {int Xpos, int Ypos, int heading}
        public void testOnePosition(int x, int y, int theta){
             
            currentLocationInfo[0] = x;
            currentLocationInfo[1] = y;
            currentLocationInfo[2] = theta;
             
            initializeLocations(mInfo);
             
            //Add a left rotation for the left sensor and a right rotation for the right sensor
            //to match the current orientation of the robot.
            stepsBackSensor.add(rotate(-90));
 
             
            //Analyse the left sensor
            for(int j = 0; j < mInfo.sizeY; j++){
                for(int i = 0; i < mInfo.sizeX; i++){
                    if (map[i][j] != null)
                    map[i][j].analyse(stepsBackSensor.get(stepsBackSensor.size() - 1), 0);
                }
            }
             
            stepsBackSensor.add(rotate(-90));
 
             
            //Analyse the left sensor
            for(int j = 0; j < mInfo.sizeY; j++){
                for(int i = 0; i < mInfo.sizeX; i++){
                    if (map[i][j] != null)
                    map[i][j].analyse(stepsBackSensor.get(stepsBackSensor.size() - 1), 0);
                }
            }
             
             
            while(checkIfFound() == false){
                //Checks if there is a wall
                sensorAnalyse();
                 
                 
                //If one possible starting position left, break the loop
                if (checkIfFound()) break;
                 
                //If there is a wall in front of the robot
                if (map[currentLocationInfo[0]][currentLocationInfo[1]].adjacentLocations[currentLocationInfo[2]] == null){
                    //turn left or right; 
                    turnLeftOrRight();
                    numberOfDecisions++;
                     
                }else{
                    //turn left or right or move forward
                    turnLeftOrRightOrForward();
                    numberOfDecisions++;
                     
                }   
            }
              
              
             //Print initial and current location of the robot if found
 
                  
                 System.out.println("Results:");
                 System.out.println("Initial location: " + Arrays.toString(getInitialLocation()));
                 System.out.println("Current location: " + Arrays.toString(getCurrentLocation()));
                 System.out.println("Number of decisions: " + numberOfDecisions);
                 System.out.print("Decisions order: ");
                  
             //Print the decisions taken by the robot
                 for(int i = 0; i < stepsMiddleSensor.size(); i++){
                     if(stepsMiddleSensor.get(i).equals('y') || stepsMiddleSensor.get(i).equals('n'));
                     else System.out.print(stepsMiddleSensor.get(i) + " ");
                 }
                  
                  
                 int[] current = getCurrentLocation();
                ArrayList<Location> path = shortestPath (map[current[0]][current[1]], map[3][3]);
                 
                 
                //Print the path finding steps
                System.out.println("\n\nPath finding steps: ");
                for(int i = 0; i < path.size(); i++){
                    System.out.println(path.get(i).gridX + ", " + path.get(i).gridY);
                }
        }
         
        //Random
        public char random(char[] c){
            int answer = rn.nextInt(c.length) + 1;
             
            if(answer == 1)return 'l';
            else if(answer == 2)return 'f';
            else return 'r';
         
        }
 
}
