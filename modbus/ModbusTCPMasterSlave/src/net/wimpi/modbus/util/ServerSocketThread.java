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
    
    private PrintWriter out = null;
    private BufferedReader in = null;

    public ServerSocketThread(Socket socket, ScoketsCommProtocol protocol ) {
	super("ServerSocketThread");
	this.socket = socket;
        this.protocol = protocol;
    }

    public void run() { 
        
	try {
	    out = new PrintWriter(socket.getOutputStream(), true);
	    in = new BufferedReader(
				    new InputStreamReader(
				    socket.getInputStream()));

	    String inputLine, outputLine;
	    outputLine = protocol.processInput(null);
	    out.println(outputLine); //write to client

	    while (true){
                if ((inputLine = in.readLine()) != null){         //client is sending data
                   outputLine = protocol.processInput(inputLine);   //get the server answer
                   out.println(outputLine);                         //send the server answer to client
                } 
	    }

	} catch (IOException e) {
	    e.printStackTrace();
            try {
                    System.err.println("Closing client socket");
                out.close(); 
                in.close();
                socket.close(); 
            } catch (IOException ex) {
                ex.printStackTrace();
            }
	    
	}
    }
    
    /*
    public void notifyClient(String notifications){
        out.println(protocol.getNotificationsFromServer()); //send data from server to client    
    }
    * 
    */
    
}
