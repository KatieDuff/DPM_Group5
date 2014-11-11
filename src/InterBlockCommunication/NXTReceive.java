package InterBlockCommunication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
 
import lejos.nxt.LCD;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
 
 
/**
 *
 *
 * author Joey Kazma
 */
public class NXTReceive
{
    //Declare streams and connection
    DataInputStream dis;
    DataOutputStream dos;
    NXTConnection conn;
     
    private int data;
     
    //Constructor
    public NXTReceive(){
         
        LCD.clear();
        LCD.drawString("Waiting for", 0, 2);
        LCD.drawString("connection...", 0, 3);
         
        //A while loop is required since it is always waiting for data
         
 
            //Try to connect
            conn = RS485.getConnector().waitForConnection(0, NXTConnection.PACKET);
         
            LCD.drawString("Connection", 0, 2);
            LCD.drawString("Succeeded", 0, 3);
            LCD.refresh();
             
            dis = conn.openDataInputStream();
            dos = conn.openDataOutputStream();
        }      
     
     
    // Method to receive data
    public int receiveData() {
        try {
            data = dis.readInt();
            LCD.drawString("Read: ", 0, 4);
            LCD.drawInt(data, 7, 6, 4);
 
            // Send back 1 if reading succeeded
            send(1);
 
        } catch (IOException e) {
            // Send 0 if reading failed
            LCD.drawString("Error: Data loss", 0, 4);
            send(0);
            closeConnection();
        }
        return data;
    }
     
    //Method to send data
    public void send(int data){
        try {
            dos.writeInt(data);
            dos.flush();    //Not sure if this is required, will have to test it
        } catch (IOException e) {
            LCD.drawString("Read Exception ", 0, 5);
            e.printStackTrace();
        }
    }
     
    //Close connection
    public void closeConnection(){
        try
        {
            LCD.drawString("Closing...    ", 0, 3);
            dis.close();
            dos.close();
            conn.close();
        }
        catch (IOException ioe)
        {
            LCD.drawString("Close Exception", 0, 5);
            LCD.refresh();
        }
        LCD.drawString("Finished        ", 0, 3);
        try {
            Thread.sleep(2000);} catch (InterruptedException e) {}
    }
}