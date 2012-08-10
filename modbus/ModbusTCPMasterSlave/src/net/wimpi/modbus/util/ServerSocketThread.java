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
 * Communicates to the client by reading from and writing to the socket.
 */
public class ServerSocketThread extends Thread {
    
    private Socket socket = null;
    private ScoketsCommProtocol protocol = null;
    private String lastNotificationString = null;

    public ServerSocketThread(Socket socket, ScoketsCommProtocol protocol ) {
	super("ServerSocketThread");
	this.socket = socket;
        this.protocol = protocol;
    }

    public void run() {

        PrintWriter out = null;
        BufferedReader in = null;
        
	try {
	    out = new PrintWriter(socket.getOutputStream(), true);
	    in = new BufferedReader(
				    new InputStreamReader(
				    socket.getInputStream()));

	    String inputLine, outputLine;
	    outputLine = protocol.processInput(null);
	    out.println(outputLine); //write to client

	    while (true){
                if (((inputLine = in.readLine()) != null)){ //client is sending data
                   outputLine = protocol.processInput(inputLine); //get the server answer
                   if ("exit".equals(outputLine))
                       break;
                   out.println(outputLine); //send the server answer to client
                } 
                if (lastNotificationString != protocol.ServerNotifications()) { //server has data for client
                    out.println(protocol.ServerNotifications()); //send data from server to client
                    lastNotificationString = protocol.ServerNotifications(); //keep track 
                }
	    }
	    out.close();
	    in.close();
	    socket.close();

	} catch (IOException e) {
	    e.printStackTrace();
            try {
               out.close(); 
               in.close();
               socket.close(); 
            } catch (IOException ex) {
                ex.printStackTrace();
            }
	    
	}
    }
}
