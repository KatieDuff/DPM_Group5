package InterBlockCommunication;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
 
import lejos.nxt.LCD;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
 
 
/**
 * 
 * author Joey Kazma
 */
public class NXTConnect {
     
     DataInputStream dis;
     DataOutputStream dos;
     NXTConnection conn;
     
    public NXTConnect() throws Exception
    {
         
        //Name of the receiver block
        String name = "Bob";
        
        LCD.clear();
        LCD.drawString("Trying cable", 0, 2);
        LCD.drawString("Connection..", 0, 3);
         
        //Initialize connection
        conn = RS485.getConnector().connect(name, NXTConnection.PACKET);
         
        //Connection failed
        if (conn == null)
        {
            //Exit system
            LCD.drawString("Connect fail", 0, 5);
            Thread.sleep(2000);
            System.exit(1);
        }
        LCD.drawString("Connection", 0, 2);
        LCD.drawString("Succeeded", 0, 3);
        LCD.refresh();
         
        //Open connection streams
         dis = conn.openDataInputStream();
         dos = conn.openDataOutputStream();
 
 
    }
    
    /** Method sends the data */
    
    public void send(int data){
        try {
            dos.writeInt(data);
            dos.flush();    //Not sure if this is required, will have to test it
            receive();
        } catch (IOException e) {
            LCD.drawString("Read Exception ", 0, 5);
            e.printStackTrace();
        }
    }
     
    /** Method that receives the data */
    public int receive(){
        int receivedData = 3;
        try {
            //Display received data
            receivedData = dis.readInt();
             LCD.drawString("Read: ", 0, 7);
             LCD.drawInt(receivedData, 8, 6, 7);
     
        } catch (IOException e) {
            LCD.drawString("Read Exception ", 0, 5);
            e.printStackTrace();
        }
        return receivedData;
    }
     
    /** Method to close the connection */
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
