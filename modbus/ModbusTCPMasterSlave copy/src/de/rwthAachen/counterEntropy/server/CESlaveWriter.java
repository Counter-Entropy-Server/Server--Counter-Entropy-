/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwthAachen.counterEntropy.server;

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
public class CESlaveWriter {
    
    private TCPMasterConnection con = null;
    private ModbusTCPTransaction trans = null;
    
 
    public CESlaveWriter (TCPMasterConnection connection)
    {
        con = connection;
    }
    
    public void writeCoil(int ref, int value){
        
       WriteCoilRequest req05 = null;
       WriteCoilResponse res05 = null;
        
        //Read from WAGO
        try {
                //1. Prepare request
                boolean binaryValue = value==1?true:false;
                req05 = new WriteCoilRequest(ref, binaryValue); 
                //if (Modbus.debug) System.out.println("Write coil request: " + req05.getHexMessage());
                
                //2. Prepare the transaction
                trans = new ModbusTCPTransaction(CEServer.master.con);
                trans.setRequest(req05);
                trans.setReconnecting(false);
                
              //3. Execute transation
                try{
                  trans.execute();  
                }
                catch (Exception a){
                   System.out.println("WAGO was disconnected. Reconnecting ...");
                   //CEServer.reconnectToWAGO();
                   boolean connectionSucceeded = false;
                    while (!connectionSucceeded)
                    {
                        try{
                        if (CEServer.master.reconnect()){
                            connectionSucceeded = true;
                            return;
                            //trans.execute();
                        }
                        }catch (Exception b){
                            connectionSucceeded = false;
                        }
                    }
                }
                
                //4. Get response
                res05 = (WriteCoilResponse) trans.getResponse();
                //if (Modbus.debug) System.out.println("Response (hex): " + res05.getHexMessage() );
                
                //System.out.println("Coil Status (binary)=" + res05.getCoil());
                                   
            } catch (Exception ex) {
              ex.printStackTrace();
            } 
    }
    
    public void writeSingleRegister(int ref, int value){
        
       WriteSingleRegisterRequest req06 = null;
       WriteSingleRegisterResponse res06 = null;
        
        //Read from WAGO
        try {
                //1. Prepare request
                req06 = new WriteSingleRegisterRequest(ref, new SimpleRegister(value)); 
                //if (Modbus.debug) System.out.println("Write reg request: " + req06.getHexMessage());
                
                //2. Prepare the transaction
                trans = new ModbusTCPTransaction(CEServer.master.con);
                trans.setRequest(req06);
                trans.setReconnecting(false);
                
                //3. Execute transation
                try{
                  trans.execute();  
                }
                catch (Exception a){
                   System.out.println("WAGO was disconnected. Reconnecting ...");
                   boolean connectionSucceeded = false;
                    while (!connectionSucceeded)
                    {
                        try{
                        if (CEServer.master.reconnect()){
                            connectionSucceeded = true;
                            return;
                        }
                        }catch (Exception b){
                            connectionSucceeded = false;
                        }
                    }
                }
                
                //4. Get response
                res06 = (WriteSingleRegisterResponse) trans.getResponse();
                //if (Modbus.debug) System.out.println("Response (hex): " + res06.getHexMessage() );
                
                
                //System.out.println("Register Status (decimal)=" + res06.getRegisterValue());
                
            } catch (Exception ex) {
              ex.printStackTrace();
            }        
    }
    
}
