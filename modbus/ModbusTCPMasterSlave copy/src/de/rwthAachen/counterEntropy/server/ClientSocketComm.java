/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwthAachen.counterEntropy.server;

import java.io.*;
import java.net.*;

/**
 *
 * @author nur
 */
public class ClientSocketComm {
    
    private static PrintWriter out = null;
    
    public static void main(String[] args) throws IOException {
 
        Socket socket = null;
        BufferedReader in = null;
 
        try {
            socket = new Socket("Nurs-MacBook-Pro.local", 4444);
            
            System.out.println("Client connected to port: 4444.");
             
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: Nurs-MacBook-Pro.local.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: Nurs-MacBook-Pro.local.");
            System.exit(1);
        }
 
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;
 
        while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
            
            fromUser = stdIn.readLine();
            if (fromUser != null) {
            System.out.println("Client: " + fromUser);
            out.println("light1:1");
            }
        }
 
        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }
    
    public static void sendDataToServer (String [] variablesWithNewValues)
    {
        String fromClient = null;
        
        if (variablesWithNewValues.length>0){
          System.out.println("Client: " + fromClient);
          out.println(fromClient);  
        }
        
    }
}
