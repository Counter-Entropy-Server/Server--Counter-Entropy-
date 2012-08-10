/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wimpi.modbus.util;

import java.io.*;
import java.net.*;

/**
 *
 * @author nur
 */
public class ClientSocketComm {
    
    public static void main(String[] args) throws IOException {
 
        Socket socket = null;
        PrintWriter out = null;
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
            if (fromServer.equals("Bye."))
                break;
             
            fromUser = stdIn.readLine();
        if (fromUser != null) {
                System.out.println("Client: " + fromUser);
                out.println(fromUser);
        }
        }
 
        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }
}
