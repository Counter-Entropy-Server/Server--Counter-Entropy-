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
    
package net.wimpi.modbus.cmd;

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
 * @author Nur Hamdan
 * @version 1 (07/08/2012)
 * adapted from creator Dieter Wimberger
 */
public class DITest {

    
  public static void main(String[] args) {

    CEScoketsProtocol protocol;   //Server-Client protocol 
    //TCPMasterConnection con = null;
    int socketPort = 4444;  //Sockets port
    
    //Command line arguments
    InetAddress addr = null;    //Slave network address
    int modbusPort = Modbus.DEFAULT_PORT;   // port number on WAGO usually 502
    int slaveReadPeriod = 500;  //Read slave every slaveReadPeriod
    String houseVariablesExcelFilePath = null;  //Path to read house variables xls file

    CEModbusTCPMaster master = null;
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
                    modbusPort = Integer.parseInt(astr.substring(idx+1)); //port of wago
                    astr = astr.substring(0,idx);
                }
                addr = InetAddress.getByName(astr); //ip-address of WAGO 
            } catch (Exception ex) {
            ex.printStackTrace();
                printUsage();
                System.exit(1);
            }
        }

        //2. Open the connection
        master = new CEModbusTCPMaster();
        master.connect(addr,modbusPort);
        //con = new TCPMasterConnection(addr);
        //con.setPort(modbusPort);
        //con.connect();
        //if (Modbus.debug) System.out.println("Connected to " + addr.toString() + ":" + con.getPort());

        //3. Process xls file and get house variables
        house = new CEHouse();
        HashMap houseVariables = house.getHouseVariablesFromFile(houseVariablesExcelFilePath);
        //HashMap houseVariables = getHouseVariables(houseVariablesExcelFilePath); //key->variable-name value->houseVariable object
        //ArrayList addressSequances = getModbusAddressSequances(houseVariables); //groups consecutive modbus addresses to read them with one command
       
        //4. Instantiate database communication object
        db = new CEDatabaseComm(); 
        db.fillWithDevices(houseVariables); //fill devices table in database for once
               
        //5. Instantiate slave (wago) reader and writer objects
        //SlaveReader r = new SlaveReader(con,houseVariables);
        //SlaveWriter w = new SlaveWriter(con,houseVariables);
                
        //6. Open network sockets to communicate with other systems
        socket = new CEServerSocket(socketPort);
        socket.setHouseReferance(house);
        (new Thread(socket)).start(); //timer on one thread and server socket on another. Each client socket has its own thread
        
        //7. Start timer to read from slave (wago)
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
 
}//class DITest








