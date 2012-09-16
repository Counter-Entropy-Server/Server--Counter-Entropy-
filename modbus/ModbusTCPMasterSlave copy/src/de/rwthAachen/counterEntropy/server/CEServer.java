
//License
/***
 * Java Modbus Library (jamod)
 * Copyright (c) 2002-2004, jamod development team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***/
    
/*
 * To run the slave simulator (wago simulator) in the terminal:
 * ModbusTCPMasterSlave nur$ java -Dnet.wimpi.modbus.debug=true   -cp build/classes:lib/jxl.jar net.wimpi.modbus.cmd.TCPSlaveTest 12345
 * To run client simulator in the terminal:
 * SocketsComm nur$ java -cp build/classes socketscomm.ClientSoctComm
 * To run the database in terminal:
 * database nur$ rails server
 * To clear the database in terminal:
 * database nur$ rake db:reset
 * To close databse in terminal:
 * ctrl+c
 * 
 * NOTE: system exits in 2 cases: 1- cannot connect to WAGO 2- cannot connect to socket
 */
package de.rwthAachen.counterEntropy.server;


import de.rwthAachen.counterEntropy.server.CEHouse;
import de.rwthAachen.counterEntropy.server.CEServerSocket;
import de.rwthAachen.counterEntropy.server.CEModbusTCPMaster;
import de.rwthAachen.counterEntropy.server.CEScoketsProtocol;
import de.rwthAachen.counterEntropy.server.ReadSlaveTimerCall;
import de.rwthAachen.counterEntropy.server.CEDatabaseComm;
import java.util.*;
import java.net.InetAddress;

import java.util.Timer;
import java.util.TimerTask;
import jxl.read.biff.BiffException;

import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.msg.*;
import net.wimpi.modbus.util.*;



/**
 * Class that implements a server to communicate with WAGO systems via TCPModucBuas protocol
 *
 * @author Nur Al-huda Hamdan
 * @version 1 (07/08/2012)
 * adapted from creator Dieter Wimberger
 */
public class CEServer {

    public static CEModbusTCPMaster master = null;    
    
  public static void main(String[] args) {

    int socketPort = 4444;  //Sockets port
    
    //Command line arguments
    InetAddress addr = null;    //Slave network address
    int modbusPort = Modbus.DEFAULT_PORT;   // port number on WAGO usually 502
    int slaveReadPeriod = 500;  //Read slave every slaveReadPeriod
    String houseVariablesExcelFilePath = null;  //Path to read house variables xls file

    //Server controls
    CEHouse house = null;
    CEDatabaseComm db = null;
    CEServerSocket socket = null;
    
    try {

        //1. Setup the parameters
        if (args.length < 3) {
            printUsage();
            System.exit(1);
        } else {
            try {
                houseVariablesExcelFilePath = args[2]; //excel file path
                slaveReadPeriod = Integer.parseInt(args[1]); //wago read period
                String astr = args[0];
                int idx = astr.indexOf(':');
                if(idx > 0) {
                    modbusPort = Integer.parseInt(astr.substring(idx+1)); //port of slave (wago)
                    astr = astr.substring(0,idx);
                }
                addr = InetAddress.getByName(astr); //ip-address of slave (wago) 
            } catch (Exception ex) {
            ex.printStackTrace();
                printUsage();
                System.exit(1);
            }
        }

        //2. Open the connection
        master = new CEModbusTCPMaster();
        if (!master.connect(addr,modbusPort)){
            System.out.println("Cannot connect to WAGO on " + addr.toString() + ":" + modbusPort + ". System will exit.");
            System.exit(-1);
        }
        
        //3. Intitialize house with house variables
        house = new CEHouse();
        HashMap houseVariables = house.getHouseVariablesFromFile(houseVariablesExcelFilePath);
        
        //4. Instantiate database communication object
        db = new CEDatabaseComm(); 
        db.fillWithDevices(houseVariables); //fill devices table in database for once
             
        /*
        //use for testing wago communication and database logging
        System.out.println(master.readByName("light1",house));
        master.writeByName("light1",1,house);
        System.out.println(master.readByName("light1",house));
        */
        
        //5. Open network sockets to communicate with other systems
        socket = new CEServerSocket(socketPort);
        socket.setHouseReferance(house);
        (new Thread(socket)).start(); //timer is on one thread and server socket on another. Each client socket has its own thread.
        
        //6. Start timer to read from slave (wago)
        Timer timer = new Timer("Read slave period "+slaveReadPeriod);
        ReadSlaveTimerCall t = new ReadSlaveTimerCall(master, house,db,socket);
        timer.schedule(t, 0, slaveReadPeriod); 
        
        

    } catch (Exception ex) {
      ex.printStackTrace();
      System.err.println("Errore in Server. Server will shut down!");
      if(master != null) master.closeConnection();
    }
  }

  private static void printUsage() {
    System.out.println("Counter Entropy Server: communicates house status with wago, database and clients");
  }
}








