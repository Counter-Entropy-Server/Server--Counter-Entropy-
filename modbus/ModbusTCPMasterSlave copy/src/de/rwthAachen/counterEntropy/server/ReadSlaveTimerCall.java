/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwthAachen.counterEntropy.server;

import de.rwthAachen.counterEntropy.server.CEDatabaseComm;
import java.util.*;

/**
 *
 * @author nur
 */

//Implements the run() method called by timer
public class ReadSlaveTimerCall extends TimerTask { 
   
    private boolean forceLog;   //flag to log all variables whether update or not
    private int times = 0;  //times member represent calling times
       
    private CEModbusTCPMaster master = null;
    private CEHouse house = null;
    private CEDatabaseComm db = null;
    private CEServerSocket socket = null;
    private HashMap houseVariables = null;
    
    public ReadSlaveTimerCall(CEModbusTCPMaster master, CEHouse house, CEDatabaseComm db, CEServerSocket socket){
        this.master = master;
        this.house = house;
        this.db = db;
        this.socket = socket;
        houseVariables = house.getHouseVariables();
    }
                
    //Called by timer
    public void run() { 
        
        
        
        //Log all variables to database every X mins (whether updates or not)
        if (times == 0) {
            forceLog = true; 
            times++;
        } else {
            forceLog = false;
            times++;
            if (times >= 100)
                times = 0;
        }
        
        
       
        //this.cancel(); //stop timer (for testing purpose)
        
        //Write (updated) variables sent from clients to slave
        WriteVariablesFromClientsToSlave();

        //Read (all) memory sequances of registers from slave
        ReadVariablesFromSlave();

        //Log into database and notify connected clients of (updated) house variables
        UpdateDatabaseAndClients();
    }
    
    
    
    
    private void WriteVariablesFromClientsToSlave()
    {
        Iterator it;
        int reverseFormattedValue;
        int newValue;
        HashMap clientVariables;
        CEHouseVariable v;
        String variableName;
        
        //Get clients data
        clientVariables = socket.protocol.getNotifactionsFromClients();
        if (clientVariables.size() == 0) //no notification
            return;
        
        //Iterate over all variables from clients 
        it = clientVariables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            
            variableName = (String) (String)pairs.getKey();
            v = house.findByName(variableName);
            if (v == null) //none existing variable
                continue;
            
            newValue = Integer.parseInt((String)pairs.getValue());
            if (newValue > v.maxValue || newValue < v.minValue) 
                continue; //none valid value
            
            if (v.value != newValue){ //check if variables were (updated)

                //reverse value format 
                reverseFormattedValue = v.reverseFormatValue(newValue);
                
                //write to slave
                master.write(v.modbusAddr,reverseFormattedValue,v.writeFunctionCall); 
            }
        }
        
        if (clientVariables.size() > 0){
            socket.protocol.clearNotifactionsFromClients();
        } else{ //no new variables
            return;
        }
        
    }
    
    private void ReadVariablesFromSlave()
    {      
        Iterator it;
        int value;
        int formattedValue;
        int addr;
        CEAddressSequance seq;
        HashMap slaveValues;
        CEHouseVariable v;
        
        //Iterate over all memory sequance of registers
        for (int i = 0; i < house.getMemorySequances().size(); i++){

            seq = (CEAddressSequance)house.getMemorySequances().get(i);
            
            //read from slave
            slaveValues = master.read(seq.ref1,seq.ref2,seq.functionCall); 

            if (slaveValues == null || slaveValues.size() == 0) //nothing was read
                continue;
            
            it = slaveValues.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();

                addr = (Integer)pairs.getKey();
                v = house.findByAddressAndType(addr,seq.type);
                
                value = (Integer)pairs.getValue();
                formattedValue = v.formatValue(value);
                        
                if (v.value != formattedValue){  //check if variables were (updated)
                    v.value = formattedValue;    //update houseVariables
                    v.updated = true;   //mark to notify other systems 
                }
            }
        }
    }
      
    private void UpdateDatabaseAndClients()
    {
        Iterator it;
        CEHouseVariable v;
        
        //Iterate over all houseVariables 
        it = houseVariables.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            v = (CEHouseVariable)pairs.getValue();
            
            //Update database 
            if (v.updated || forceLog){
                db.updateVariableByAddress(v.modbusAddr,v.value);                              
            }
            
            //Notify clients
            if (v.updated){
                //socket.addNewNotification(v);
            } 
            
            v.updated = false;
        }
        
        //socket.notifyClients();
    }
       
        
} // class ReadSlaveTimerCall

