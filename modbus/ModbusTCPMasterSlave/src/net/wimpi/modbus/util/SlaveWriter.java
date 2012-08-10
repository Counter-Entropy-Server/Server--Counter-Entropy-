/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wimpi.modbus.util;

import java.util.HashMap;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.msg.WriteCoilResponse;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.msg.WriteSingleRegisterResponse;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.procimg.SimpleRegister;

/**
 *
 * @author nur
 */
public class SlaveWriter {
    
    private TCPMasterConnection con = null;
    private ModbusTCPTransaction trans = null;
    
    private HashMap houseVariables;
 
    public SlaveWriter (TCPMasterConnection connection, HashMap vars)
    {
        houseVariables = vars;
        con = connection;
    }
    
    public void Write(String name, int value){
        
        if (houseVariables.containsKey(name)){
            
            HouseVariable v = (HouseVariable)houseVariables.get(name);

            if (value != v.value){ //if the value has changed
                switch (v.functionCall){
                    case 5:
                        WriteCoil(v.modbusAddr,v.value);//0 or 1
                        break;
                    case 6:
                        WriteSingleRegister(v.modbusAddr,v.value);//decimal
                        break;
                }
            }   
        }
        else
        {
           System.out.println("Trying to write unrecognized variable"); 
        }
        
    }
    
    private void WriteCoil(int ref, int value){
        
       WriteCoilRequest req05 = null;
       WriteCoilResponse res05 = null;
        
        //Read from WAGO
        try {
                //1. Prepare request
                req05 = new WriteCoilRequest(ref, (value==1?true:false)); 
                if (Modbus.debug) System.out.println("Write coil request: " + req05.getHexMessage());
                
                //2. Prepare the transaction
                trans = new ModbusTCPTransaction(con);
                trans.setRequest(req05);
                trans.setReconnecting(false);
                
                //3. Execute transation
                trans.execute();
                
                //4. Get response
                res05 = (WriteCoilResponse) trans.getResponse();
                if (Modbus.debug) System.out.println("Response (hex): " + res05.getHexMessage() );
                
                
                System.out.println("Coil Status (binary)=" + res05.getCoil());
                                   
            } catch (Exception ex) {
              ex.printStackTrace();
            } 
    }
    
    private void WriteSingleRegister(int ref, int value){
        
       WriteSingleRegisterRequest req06 = null;
       WriteSingleRegisterResponse res06 = null;
        
        //Read from WAGO
        try {
                //1. Prepare request
                req06 = new WriteSingleRegisterRequest(ref, new SimpleRegister(value)); 
                if (Modbus.debug) System.out.println("Write reg request: " + req06.getHexMessage());
                
                //2. Prepare the transaction
                trans = new ModbusTCPTransaction(con);
                trans.setRequest(req06);
                trans.setReconnecting(false);
                
                //3. Execute transation
                trans.execute();
                
                //4. Get response
                res06 = (WriteSingleRegisterResponse) trans.getResponse();
                if (Modbus.debug) System.out.println("Response (hex): " + res06.getHexMessage() );
                
                
                System.out.println("Register Status (decimal)=" + res06.getRegisterValue());
                
            } catch (Exception ex) {
              ex.printStackTrace();
            }        
    }
    
}
