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
    
   //private int maxNumOfListeners; //iPad and kinect 
   private ArrayList<CESocketThread> socketListeners = new ArrayList<CESocketThread>();
   private int port = 4444;
   private CESocketThread listener;
   private CEHouse house;
    
    public CEServerSocket (int port){//, int maxNumOfListeners) {
        
        this.port = port;
        this.protocol = CEScoketsProtocol.getInstance(); //prepare string to send to clients and parse recived strings from clients        
        //this.maxNumOfListeners = maxNumOfListeners;
    }
    
    public void run(){
        
        ServerSocket serverSocket = null;
        boolean listening = true;
        //int socketListenersCounter = 0;
        
        
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port: " + port);

            while (listening){
                listener = new CESocketThread(serverSocket.accept(),protocol);
                if (socketListeners.contains(listener))
                    continue;
                socketListeners.add(listener);
                listener.start();
                System.out.println("Number of port listeners: " + getLivePortListeners().size());
        }
        
        
        serverSocket.close();
        
        } catch (IOException e) {
            System.out.println("Cannot listen on port: " + port + ". System will exit.");
            System.exit(-1);
        }        
    }
           
    
    public ArrayList getLivePortListeners(){
        ArrayList<CESocketThread> listeners = new ArrayList<CESocketThread>();
        for (CESocketThread thread:socketListeners){
            if (!thread.socket.isClosed()){
                listeners.add(thread);
                //socketListeners.remove(thread);
            }
        }
        return listeners;
    }
        
    public void addNewNotification(CEHouseVariable v){
        
        protocol.addNewNotification(v);
    }
    
    public synchronized void notifyClients()
    {
        String notifications = protocol.getNotificationsFromServer();
        if (getLivePortListeners().size() > 0 && !notifications.equals("")){
           for (int i = 0 ; i < getLivePortListeners().size(); i++ ){
                CESocketThread socketThread = (CESocketThread)getLivePortListeners().get(i);
                socketThread.notifyClient(notifications);
           }
           protocol.clearNotifactionsFromServer(); 
        } 
    }
    

    public void setHouseReferance(CEHouse house) {
        this.house= house;
        protocol.setHouseReferance(house);    
    }
    
}
