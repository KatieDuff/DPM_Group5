package SearchAndRescue;
import java.util.ArrayList;


public class Location {
     
    //All arrays size = 4 because there are 4 possible orientation per tile
     
    int gridX, gridY;                                           // Coordinate numbers for each tile from example the bottom left corner tile is (0, 0)
    Location[] adjacentLocations = {null, null, null, null};    // Each tile adjacent tiles
     
    //Each ultrasonic sensor has an array of locations since they will have different
    //paths when the robot moves. Example: if the robot moves forward, the left sensor
    //moves to the right.
    Location[][] currentLocation = {{null, null, null, null}, {null, null, null, null}};        
    int[][] currentOrientation = {{0,1,2,3}, {0,1,2,3}};                                            
     
    boolean[] initialOrientations = {true, true, true, true};   //Check if the initial orientation were valid
    boolean visited = false;                                    //Used for getting the path
    Location previous = null;                                   //Used for getting the path
     
     
    //Constructor
    public Location (int gridX, int gridY){
        this.gridX = gridX;
        this.gridY = gridY;
    }
     
    //Analyse the initial orientation using the last move and the current sensor being analyzed
    // sensor = 0 -> left sensor
    // sensor = 1 -> middle sensor
    // sensor = 2 -> right sensor
    public void analyse(Character c, int sensor) {
            //Remove possible arrows
            for (int i = 0; i < initialOrientations.length; i++){
                 
                //Check only if the initial orientation is still valid
                if(initialOrientations[i]){
                     
 
                        //There is a wall
                        if (c.equals('y')){
                            //If the location in front of the current position of the arrow is not blocked,
                            //then remove that arrow from the possible starting positions
                            if (currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i]] != null)
                                initialOrientations[i] = false;
                        }
                        //There is no wall
                        else if (c.equals('n')){
                            //If the location in front of the current position of the arrow is blocked,
                            //then remove that arrow from the possible starting positions
                            if (currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i]] == null)
                                initialOrientations[i] = false;
                        }
                        //Move forward by 1 tile
                        else if (c.equals('f')){
                            currentLocation[sensor][i] = currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i]];
                        }
                        //Move left by 1 tile
                        else if (c.equals('a')){
                            if (currentOrientation[sensor][i] == 0) currentLocation[sensor][i] = currentLocation[sensor][i].adjacentLocations[3];
                            else  currentLocation[sensor][i] = currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i] - 1];
                        }
                        //Move right by 1 tile
                        else if (c.equals('d')){
                            if (currentOrientation[sensor][i] == 3) currentLocation[sensor][i] = currentLocation[sensor][i].adjacentLocations[0];
                            else  currentLocation[sensor][i] = currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i] + 1];
                        }
                        //Move back by 1 tile
                        else if (c.equals('b')){
                            currentLocation[sensor][i] = currentLocation[sensor][i].adjacentLocations[(currentOrientation[sensor][i] + 2)%4];
                        }
                        //Turn left
                        else if (c.equals('l')){
                            if (currentOrientation[sensor][i] == 0) currentOrientation[sensor][i] = 3;
                            else  currentOrientation[sensor][i] = currentOrientation[sensor][i]-1;
                        }
                        //Turn right
                        else if (c.equals('r')){
                            if (currentOrientation[sensor][i] == 3) currentOrientation[sensor][i] = 0;
                            else  currentOrientation[sensor][i] = currentOrientation[sensor][i]+1;
                        }
                         
                        //Print stuff for testing purposes don't mind this
//                      if (sensor==0) System.out.print('L');
//                      else if (sensor==1) System.out.print('M');
//                      else if (sensor==2) System.out.print('R');
//                      
//                      
//                      System.out.print(orienteering.arrowCounter+": "+gridX+", "+gridY+", "+ i +", "+ initialOrientations[i]);
//                      System.out.print("\tL: "+ currentLocation[sensor][i].gridX + "," + currentLocation[sensor][i].gridY + "," +currentOrientation[sensor][i]+", "+currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i]]);
//                      System.out.print("\tM: "+ currentLocation[sensor][i].gridX + "," + currentLocation[sensor][i].gridY + "," +currentOrientation[sensor][i]+", "+currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i]]);
//                      System.out.print("\tR: "+ currentLocation[sensor][i].gridX + "," + currentLocation[sensor][i].gridY + "," +currentOrientation[sensor][i]+", "+currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i]]);
//                      System.out.println();
                     
                     
                    // If the arrow is still valid, increment the number of valid arrows in orienteering
                    if(initialOrientations[i] && sensor == 1) Orienteering.arrowCounter++;
 
            }
        }   
    }
 
    // sensor = 0 -> left sensor
    // sensor = 1 -> middle sensor
    public int numberOfPositionsRemoved(boolean a, char movement, int sensor) {
        int number = 0;
         
        for (int i = 0; i < initialOrientations.length; i++) {
 
            // Check only if the initial orientation is still valid
            if (initialOrientations[i]) {
                if (movement == 'l'){
                    if (currentOrientation[sensor][i] == 0){
                        if ((a == true && currentLocation[sensor][i].adjacentLocations[3] == null) || (a == false && currentLocation[sensor][i].adjacentLocations[3] != null)) number += 1;
                    }else{
                        if ((a == true && currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i] - 1] == null) || (a == false && currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i] - 1] != null)) number += 1;
                    }
                 
                 
                }else if (movement == 'r'){
                    if (currentOrientation[sensor][i] == 3){
                        if ((a == true && currentLocation[sensor][i].adjacentLocations[0] == null) || (a == false && currentLocation[sensor][i].adjacentLocations[0] != null)) number += 1;
                    }else{
                        if ((a == true && currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i] + 1] == null) || (a == false && currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i] + 1] != null)) number += 1;
                    }
                 
                 
                }
                else if (movement == 'f'){
                    //left sensor
                    if(sensor == 0){
                         
                        if ((a == true && currentLocation[sensor][i] == null) 
                                || (a == false && currentLocation[sensor][i] != null)) number += 1;
                         
                    }
                    //Middle sensor
                    else if (sensor ==1){
                            if ((a == true && currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i]].adjacentLocations[currentOrientation[sensor][i]] == null) || (a == false && currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i]].adjacentLocations[currentOrientation[sensor][i]] != null)) number += 1;
                    }
                    //right sensor
                    else if (sensor ==2){
                         
                        if (currentOrientation[sensor][i] == 0){
                             
                        if ((a == true && currentLocation[sensor][i].adjacentLocations[3].adjacentLocations[currentOrientation[sensor][i]] == null )
                                || (a == false && currentLocation[sensor][i].adjacentLocations[3].adjacentLocations[currentOrientation[sensor][i]] != null)) number += 1;
                        }
 
                        else{
                            if ((a == true && currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i] - 1].adjacentLocations[currentOrientation[sensor][i]] == null )
                                || (a == false && currentLocation[sensor][i].adjacentLocations[currentOrientation[sensor][i] - 1].adjacentLocations[currentOrientation[sensor][i]] != null)) number += 1;
                        }
                         
                    }
                     
                     
                     
 
                }
            }
        }
        return number;
    }
 
     
    //Accessors
 
    public int getX(){
        return this.gridX;
    }
 
    public int getY(){
        return this.gridY;
    }
 
    public boolean getOrientation(int i){
        return this.initialOrientations[i];
    }
     
    //Mutators
     
    public void setX(int x){
        this.gridX = x;
    }
     
    public void setY(int y){
        this.gridY = y;
    }
     
    public void setOrientation(int i, boolean b){
        this.initialOrientations[i] = b;
    }
 
 
}


