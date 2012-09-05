/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwthAachen.counterEntropy.server;

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
public class CESlaveReader { 
    
    private TCPMasterConnection con = null;
    private ModbusTCPTransaction trans = null;
      
    
    public CESlaveReader (TCPMasterConnection connection)
    {
        con = connection;
    }
    
    
    public HashMap readCoils(int ref, int numberOfBits){ //FC!
        
        ReadCoilsRequest req01 = null;
        ReadCoilsResponse res01 = null;
        HashMap<Integer, Integer> readCoils = new HashMap<Integer, Integer>();
        
        try {
                //1. Prepare request
                req01 = new ReadCoilsRequest(ref, numberOfBits); 
                //if (Modbus.debug) System.out.println("Read coil request: " +  req01.getHexMessage());
                
                //2. Prepare the transaction
                trans = new ModbusTCPTransaction(con);
                trans.setRequest(req01);
                trans.setReconnecting(false);
                
                //3. Execute transation
                try{
                  trans.execute();  
                }
                catch (Exception ex){
                   System.out.println("WAGO was disconnected. System will exit.");
                   System.exit(-1);
                }
                
                //4. Get response
                res01 = (ReadCoilsResponse) trans.getResponse();
                //if (Modbus.debug) System.out.println("Response (hex): " + res01.getHexMessage() );
             
                int coilIndex = ref;
                
                //5. Save coils in HashMap: key->coil address value->coil status
                for (int i = 0; i < numberOfBits; i++)
                {
                    readCoils.put(Integer.valueOf(coilIndex),Integer.valueOf(res01.getCoilStatus(i)? 1 : 0)); //convert boolean to int
                    coilIndex++;
                }
                
                //System.out.println("Coils status (binary)=" + res01.getCoils());
                                          
            } catch (Exception ex) {
              ex.printStackTrace();
            }
        
        return readCoils;
    }
    
    public HashMap readMultipleRegisters(int ref, int numberOfRegisters){ //FC3
        
        ReadMultipleRegistersRequest req03 = null;
        ReadMultipleRegistersResponse res03 = null;
        HashMap<Integer, Integer> readRegisters = new HashMap<Integer, Integer>();
        
        try {
                //1. Prepare request
                req03 = new ReadMultipleRegistersRequest(ref, numberOfRegisters); 
                //if (Modbus.debug) System.out.println("Read reg request: " + req03.getHexMessage());
                
                //2. Prepare the transaction
                trans = new ModbusTCPTransaction(con);
                trans.setRequest(req03);
                trans.setReconnecting(false);
                
                //3. Execute transation
                trans.execute();
                
                //4. Get response
                res03 = (ReadMultipleRegistersResponse) trans.getResponse();
                //if (Modbus.debug) System.out.println("Response (hex): " + res03.getHexMessage() );
                
                int registerIndex = ref;
                
                //5. Save registers in HashMap: key->register address value->register value
                for (int i = 0 ; i < numberOfRegisters; i++)
                {
                    readRegisters.put(registerIndex,res03.getRegisterValue(i)); 
                    registerIndex++;
                    //System.out.println("Holding Register "+ ref+" Status (decimal)= " + returnedValue);  
                }
                 
            } catch (Exception ex) {
              ex.printStackTrace();
            }
        
        return readRegisters;
    }
    
}
