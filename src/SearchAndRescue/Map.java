package SearchAndRescue;

public class Map {
    
    int sizeX;
    int sizeY;
    int[][] obstacles;
     
    public Map (int sizeX, int sizeY, int[][] obstacles){
        this.sizeX = sizeX;
        this.sizeY = sizeX;
        this.obstacles = obstacles;
    }
}