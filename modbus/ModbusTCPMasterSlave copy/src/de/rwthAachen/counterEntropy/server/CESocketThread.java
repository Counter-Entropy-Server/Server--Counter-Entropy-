/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwthAachen.counterEntropy.server;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nur
 */

/**
 * 
 * Communicates to the client by reading from and writing to the socket.
 */
public class CESocketThread extends Thread {
    
    public Socket socket = null;
    private CEScoketsProtocol protocol = null;
    
    public PrintWriter out = null;
    private BufferedReader in = null;

    public CESocketThread(Socket socket, CEScoketsProtocol protocol ) {
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
                else{
                    break;
                }
	    }
            
            System.out.println("A client socket disconnected");
            try {
                    out.close(); 
                    in.close();
                    socket.close();
                }
            catch (IOException e) {
                    e.printStackTrace();
                }

	}catch(SocketException ex){
            System.out.println("Closing client socket");
            try {
                    out.close(); 
                    in.close();
                    socket.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
        }catch (IOException e) {
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
    
    
    public synchronized void  notifyClient(String notifications){
        /*
       PrintWriter out2; 
        //DataOutputStream outToClient;
        try {
            out2 = new PrintWriter(socket.getOutputStream(), true);
            if (out2 != null && socket.isConnected())
                out2.println(notifications);
                //outToClient = new DataOutputStream(socket.getOutputStream());
                //outToClient.writeBytes(notifications);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        * 
        */
    }
    
    
}
