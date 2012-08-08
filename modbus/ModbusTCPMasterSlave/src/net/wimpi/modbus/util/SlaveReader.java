/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wimpi.modbus.util;

import java.util.*;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.ReadCoilsResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.Register;

/**
 *
 * @author nur
 */
public class SlaveReader { 
    
    private TCPMasterConnection con = null;
    private ModbusTCPTransaction trans = null;
      
    
    
    private HashMap houseVariables;
 
    public SlaveReader (TCPMasterConnection connection, HashMap vars)
    {
        houseVariables = vars;
        con = connection;
    }
    
    public int Read (String name){
        
        int value;
        
        if (houseVariables.containsKey(name))
        {
            HouseVariable v = (HouseVariable)houseVariables.get(name);
        
            switch (v.functionCall) { //Read readable variables
                case 1: //IX 
                    value = readCoils(v.modbusAddr,1);
                    return value;
                case 3: //IW 
                    value = readMultipleRegisters(v.modbusAddr,1);
                    return value;
            } 
        }
        else
        {
           System.out.println("Trying to read unrecognized variable"); 
        }
        return -1;
    }
    
    private int readCoils(int ref, int numberOfBits){ //FC!
        
        ReadCoilsRequest req01 = null;
        ReadCoilsResponse res01 = null;
        int returnedValue = 0;
        
        try {
                //1. Prepare request
                req01 = new ReadCoilsRequest(ref, numberOfBits); 
                if (Modbus.debug) System.out.println("Read coil request: " + req01.getHexMessage());
                
                //2. Prepare the transaction
                trans = new ModbusTCPTransaction(con);
                trans.setRequest(req01);
                trans.setReconnecting(false);
                
                //3. Execute transation
                trans.execute();
                
                //4. Get response
                res01 = (ReadCoilsResponse) trans.getResponse();
                if (Modbus.debug) System.out.println("Response (hex): " + res01.getHexMessage() );
                
                
                returnedValue = res01.getCoilStatus(7)? 1 : 0; //LSB
                
                System.out.println("Coils Status (binary)=" + returnedValue);
                                          
            } catch (Exception ex) {
              ex.printStackTrace();
            }
        
        return returnedValue;
    }
    
    private int readMultipleRegisters(int ref, int numberOfRegisters){ //FC3
        
        ReadMultipleRegistersRequest req03 = null;
        ReadMultipleRegistersResponse res03 = null;
        int returnedValue = 0;
        
        try {
                //1. Prepare request
                req03 = new ReadMultipleRegistersRequest(ref, numberOfRegisters); 
                if (Modbus.debug) System.out.println("Read reg request: " + req03.getHexMessage());
                
                //2. Prepare the transaction
                trans = new ModbusTCPTransaction(con);
                trans.setRequest(req03);
                trans.setReconnecting(false);
                
                //3. Execute transation
                trans.execute();
                
                //4. Get response
                res03 = (ReadMultipleRegistersResponse) trans.getResponse();
                if (Modbus.debug) System.out.println("Response (hex): " + res03.getHexMessage() );
                
                //Compare variable
                Register[] m_Registers = res03.getRegisters();
                for (int i = 0 ; i < m_Registers.length; i++)
                {
                    returnedValue = res03.getRegisterValue(i);
                    System.out.println("Holding Register Status (decimal)= " + returnedValue);
                    
                }
                 
            } catch (Exception ex) {
              ex.printStackTrace();
            }
        
        return returnedValue;
    }
    
}
