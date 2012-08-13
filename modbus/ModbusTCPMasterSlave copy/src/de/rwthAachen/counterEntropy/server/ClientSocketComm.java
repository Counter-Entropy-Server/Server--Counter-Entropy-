/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwthAachen.counterEntropy.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 *
 * @author nur
 */
public class ClientSocketComm {
    
    private static PrintWriter out = null;
    private static String notificationString = "";
    
    public static void main(String[] args) throws IOException {
 
        Socket socket = null;
        BufferedReader in = null;
        String hostname = "Nurs-MacBook-Pro.local";
 
        try {
            socket = new Socket(hostname, 4444);
            
            System.out.println("Client connected to port: 4444.");
             
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: "+ hostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: "+ hostname);
            System.exit(1);
        }
 
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;
 
        while ((fromServer = in.readLine()) != null) {
            
            processInput(fromServer);
            //System.out.println("Server: " + fromServer);
            
            /*
            fromUser = stdIn.readLine();
            if (fromUser != null) {
            System.out.println("Client: " + fromUser);
            * 
            */
            if (getNotificationString() != "")
                out.println(getNotificationString());
        }
   
 
        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }
    
    public static void processInput(String input){
      /*  
        String variablesDelims = "[,]";
        String[] variables;
            
        String request, key, value;

        variablesDelims = "[,]";
        request = variables[1];
        variables = request.split(variablesDelims);

        variablesDelims = "[:]";
        for (int i = 0; i < variables.length; i++){
            if (variables[i].split(variablesDelims).length != 2) //wrong format
                continue;
            key = variables[i].split(variablesDelims)[0]; //name
            value = variables[i].split(variablesDelims)[1]; //value

            if (key == null || value == null) //empty strings
                continue;
            variablesFromClients.put(key,value);
        }
        * 
        */
    }
    
    public static void toggleVariables (ArrayList variables){
        String string = "";
        string = "toggel ";

        for (int i =0; i < variables.size(); i++){
            
            string += variables.get(i)+",";
        }

        notificationString = string;
        
    }

    public static String getNotificationString(){
           
        String temp = notificationString;
        notificationString = "";
        return temp;
    
    }   

}
