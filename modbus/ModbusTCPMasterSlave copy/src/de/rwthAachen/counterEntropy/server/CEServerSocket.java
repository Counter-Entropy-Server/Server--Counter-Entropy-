/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwthAachen.counterEntropy.server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author nur
 */

/**
 * 
 * Loops forever, listening for client connection requests on a ServerSocket.
 * When a request comes in, it accepts the connection, creates a new CESocketThread object to process it,
 * hands it the socket returned from accept, and starts the thread. Then the server goes back to listening
 * for connection requests.
 */

public class CEServerSocket implements Runnable{
    
   public CEScoketsProtocol protocol;
    
   private int maxNumOfListeners; //iPad and kinect 
   private ArrayList<CESocketThread> socketListeners = new ArrayList<CESocketThread>();
   private int port = 4444;
   private CESocketThread listener;
   private CEHouse house;
    
    public CEServerSocket (int port, int maxNumOfListeners) {
        
        this.port = port;
        this.protocol = CEScoketsProtocol.getInstance(); //prepare string to send to clients and parse recived strings from clients        
        this.maxNumOfListeners = maxNumOfListeners;
    }
    
    public void run(){
        
        ServerSocket serverSocket = null;
        boolean listening = true;
        //int socketListenersCounter = 0;
        
        
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port: " + port);

            while (listening && socketListeners.size() < maxNumOfListeners){
                listener = new CESocketThread(serverSocket.accept(),protocol);
                socketListeners.add(listener);
                listener.start();
                System.out.println("Number of port listeners: " + getLiveClientSockets().size());
        }
        
        
        serverSocket.close();
        
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }        
    }
           
    
    private ArrayList getLiveClientSockets()
    {

        for (CESocketThread thread:socketListeners){
            if (!thread.socket.isConnected()){
                socketListeners.remove(thread);
            }
        }
        return socketListeners;
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
