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

    TCPMasterConnection con = null;

    //Slave network address
    InetAddress addr = null;
    
    //Slave network port
    int port = Modbus.DEFAULT_PORT;

    try {

        //1. Setup the parameters
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        } else {
            try {
            String astr = args[0];
            int idx = astr.indexOf(':');
            if(idx > 0) {
                port = Integer.parseInt(astr.substring(idx+1)); // port number on WAGO usually 502
                astr = astr.substring(0,idx);
            }
            addr = InetAddress.getByName(astr); // ip-address of WAGO 
            } catch (Exception ex) {
            ex.printStackTrace();
            printUsage();
            System.exit(1);
            }
        }

        //2. Open the connection
        con = new TCPMasterConnection(addr);
        con.setPort(port);
        con.connect();

        if (Modbus.debug) System.out.println("Connected to " + addr.toString() + ":" + con.getPort());

        //3. Process xls file and get house variables
        HashMap houseVariables;
        houseVariables = getHouseVariables();
        
        //4. Instantiate database communication object
        DBComm db = new DBComm(); //(can send the server ip address from command line?)
        //fill devices table in database
        db.FillDevices(houseVariables);
               
        //4. Instantiate slave reader object
        SlaveReader r = new SlaveReader(con,houseVariables);

         //5. Instantiate slave writer object
        SlaverWriter w = new SlaverWriter(con,houseVariables);
        
        //w.Write("window3_requested_position",10);
        //w.Write("light1",1);
        
        //6. Open network sockets to communicate with other systems
        
        
        //5. Start timer to read from slave
        Timer timer = new Timer("Read slave time (5000ms)");

        ReadSlaveTimerCall t = new ReadSlaveTimerCall(r,houseVariables,db);

        timer.schedule(t, 0, 5000); // 5000ms (input from command line?)

        //7. Close the connection
        //con.close();

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static void printUsage() {
    System.out.println("");
  }

  private static HashMap getHouseVariables() {
    
    HashMap variables;
      try
      {
        //read xls file into HashMap
        ReadExcel reader = new ReadExcel();
        reader.setInputFile("/Users/nur/NetBeansProjects/ModbusTCPMasterSlave/120801_SlaveMasterRegistersMapping.xls");
        variables = reader.read();
        
        return variables;

      } catch (Exception ex) {
        ex.printStackTrace();
      }
    return null;
  }

}//class DITest

class ReadSlaveTimerCall extends TimerTask { //Implements the run() method called by timer
    
    //times member represent calling times
    private int times = 0;
    
    //flag to log all variables whether changed from previous reading or not
    boolean forceLog; 
    
    //referance to slave reader 
    SlaveReader reader;
    
    //referance to database
    DBComm database;
    
    //house variables
    HashMap houseVariables;
    HouseVariable v;
    
    String dbRequest;
    
    public ReadSlaveTimerCall(SlaveReader r, HashMap variables, DBComm db){
        reader = r;
        houseVariables = variables;
        database = db;
    }
            
    //Called by timer
    public void run() {
        
        times++; 
        
        //Log all variables every 5 mins
        if (times == 1) {
            forceLog = true; 
            System.out.println("Timer: Write everything to db");
        } else if (times >= 100){
            forceLog = false;
            times = 0;
        }
        
        // Stop timer (for testing purpose)
        this.cancel();
        
        //Iterate over all houseVariables to read
        Iterator it = houseVariables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            v = (HouseVariable)pairs.getValue();
            
            if (!v.readable) //only readable variables
                continue;
            
            int value = reader.Read(v.name); 
            
            //Compare new variable value with previous read
            if (v.value != value){ 
                v.value = value;
                v.updated = true; //mark to notify other systems 
            }
            
            //Update database and notify other systmes
            if (v.updated || forceLog)
            {
                //log to db
                dbRequest = "/setDeviceStateByAddress?address="+v.modbusAddr+"&state="+v.value;
                database.sendGetRequest(dbRequest);
                                
                if (v.updated){
                 
                    //notify other systems
                }   
                
                v.updated = false;
            }
            
        }
    }
}





