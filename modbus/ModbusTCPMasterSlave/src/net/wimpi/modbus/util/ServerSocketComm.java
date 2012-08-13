/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wimpi.modbus.util;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import net.wimpi.modbus.cmd.CEHouse;

/**
 *
 * @author nur
 */

/**
 * 
 * Loops forever, listening for client connection requests on a ServerSocket.
 * When a request comes in, it accepts the connection, creates a new ServerSocketThread object to process it,
 * hands it the socket returned from accept, and starts the thread. Then the server goes back to listening
 * for connection requests.
 */

public class ServerSocketComm implements Runnable{
    
   public ScoketsCommProtocol protocol;
    
   private int maxNumOfListeners; //iPad and kinect 
   private ArrayList<ServerSocketThread> socketListeners = new ArrayList<ServerSocketThread>();
   private int port = 4444;
   private ServerSocketThread listener;
   private CEHouse house;
    
    public ServerSocketComm (int port, int maxNumOfListeners) {
        
        this.port = port;
        this.protocol = ScoketsCommProtocol.getInstance(); //prepare string to send to clients and parse recived strings from clients        
        this.maxNumOfListeners = maxNumOfListeners;
    }
    
    public void run(){
        
        ServerSocket serverSocket = null;
        boolean listening = true;
        int socketListenersCounter = 0;
        
        
        try {
            serverSocket = new ServerSocket(port);
            
            while (listening && socketListenersCounter < maxNumOfListeners){
            System.out.println("Server listening on port: " + port);
            listener = new ServerSocketThread(serverSocket.accept(),protocol);
            socketListeners.add(listener);
            listener.start();
            System.out.println("Number of port listeners: " + socketListenersCounter++);
        }
        
        
        serverSocket.close();
        
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }        
    }
           
    
    public ArrayList getSocketListeners(){
        
        return  socketListeners;
    }
    
 
    /*
    public void addNewNotification(HouseVariable v){
        
        protocol.addNewNotification(v);
    }
    
    public void notifyClients()
    {
        if (socketListeners.size() > 0){
           for (int i = 0 ; i < socketListeners.size(); i++ ){
                socketListeners.get(i).notifyClient(protocol.getNotificationsFromServer());
           }
           protocol.clearNotifactionsFromServer(); 
        } 
    }
    * 
    */

    public void setHouseReferance(CEHouse house) {
        this.house= house;
        protocol.setHouseReferance(house);    
    }
    
}
