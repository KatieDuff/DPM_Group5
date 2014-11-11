package Filtering;

import java.util.Arrays;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;

public class DifferentialFilter implements SensorFilter, Runnable{

	// Color Sensor
    private ColorSensor lightSensor;
     
    // Constructor
    public DifferentialFilter (ColorSensor cs) {
        this.lightSensor = cs;
        this.lightSensor.setFloodlight(true);
    }
     
    public void run() {
         
        LCD.clear();
        int lightReading;
         
        // Fill the light window with 10 initial samples
        int[] lightWindow = getInitialLightWindow();
         
        int lightWindowMedian;
         
        while (true) {
             
            // Get the difference of two consecutive readings
            lightReading = getFilteredValue();
             
            // Store the difference calculated before in the first position of the light window array (shifts the entire array to the right)
            shiftArrayByOne(lightWindow, lightReading);
             
            // Find the median of the light window  
            lightWindowMedian = getMedian(lightWindow);
             
            // If the median goes above 2.0, a line has been detected (this value was determined experimentally)
            if (lightWindowMedian > 2.0){
                 
                Sound.beep();
                 
                // Reset the light window when a line has been detected
                for (int i = 0; i < lightWindow.length; i++) {
                    lightWindow[i] = 0;
                }
                 
                // Sleep for 1 second to avoid detecting the same line multiple times
                try {Thread.sleep(1000);} catch (InterruptedException e) {} 
                 
            }
             
        }
         
    }
     
    // Get the difference of two consecutive readings
    public int getFilteredValue() {
         
        int lightReading1;
        int lightReading2;
         
        lightReading1 = lightSensor.getRawLightValue();
        lightReading2 = lightSensor.getRawLightValue();
        int difference = Math.abs(lightReading2 - lightReading1);
         
        return difference;
         
    }
     
    // Fill the light window with its initial values
    public int[] getInitialLightWindow() {
         
        int[] lightWindow = new int[10];
        int lightReading1;
        int lightReading2;
        int difference;
         
        for (int i = 0; i < 11; i++) {
             
            lightReading1 = lightSensor.getRawLightValue();
            lightReading2 = lightSensor.getRawLightValue();
            difference = Math.abs(lightReading2 - lightReading1);
             
            // Skip the first reading reported by the light sensor since it is not accurate
            if (i > 0) {
                lightWindow[i-1] = difference;
            }
             
            // Sleep 250 miliseconds between readings
            try {Thread.sleep(250);} catch (InterruptedException e) {}
        }
         
        return lightWindow;
         
    }
 
    // Shift array by one to introduce a new reading at the first position and discard the last one
    public void shiftArrayByOne(int[] input, int latestValue) {
 
        for (int i = 0; i < input.length - 1; i++) {
            input[i] = input[i + 1];
        }
         
        input[input.length - 1] = latestValue;
    }
 
    // Calculate the median of the light window
    public int getMedian(int[] input) {
 
        int[] sortedArray = new int[input.length];
        System.arraycopy(input, 0, sortedArray, 0, input.length);
        Arrays.sort(sortedArray);
        int middle = sortedArray.length / 2;
         
        if (sortedArray.length % 2 == 1) {
            return sortedArray[middle];
        } 
         
        else {
            return (sortedArray[middle - 1] + sortedArray[middle]) / 2;
        }
    }

}
