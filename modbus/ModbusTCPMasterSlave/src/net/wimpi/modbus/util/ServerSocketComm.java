/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wimpi.modbus.util;

import java.net.*;
import java.io.*;

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
    
   private int maxNumOfListeners = 2; //iPad and kinect 
   private ServerSocketThread [] socketListeners = new ServerSocketThread[maxNumOfListeners];
   private ScoketsCommProtocol protocol;
   private int port = 4444;
    
    public ServerSocketComm (int port, ScoketsCommProtocol protocol ) {
        
        this.port = port;
        this.protocol = protocol;
    }
    
    public void run(){
        
        ServerSocket serverSocket = null;
        boolean listening = true;
        int socketListenersCounter = 0;
        
        
        try {
            serverSocket = new ServerSocket(port);
            
            while (listening && socketListenersCounter < maxNumOfListeners){
            System.out.println("Server listening on port: " + port);
            socketListeners [socketListenersCounter] = new ServerSocketThread(serverSocket.accept(),protocol);
            socketListeners [socketListenersCounter].start();
            socketListenersCounter++;
            System.out.println("Number of port listeners: " + socketListenersCounter);
        }
        
        
        serverSocket.close();
        
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }        
    }
           
    
    public ServerSocketThread [] getSocketListeners()
    {
        return  socketListeners;
    }
    
}
