/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rwthAachen.counterEntropy.server;

import java.net.InetAddress;
import java.util.HashMap;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.net.TCPMasterConnection;

/**
 *
 * @author nur
 */
public class CEModbusTCPMaster {
    
    TCPMasterConnection con = null;
    CESlaveReader reader = null;
    CESlaveWriter writer = null;
            
    public void CEModbusMaster()
    {
        
    }
        
    public void connect(InetAddress addr, int port)
    {
        try
        {
            con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();
            if (Modbus.debug) System.out.println("Connected to " + addr.toString() + ":" + con.getPort());
            
        }catch (Exception ex){
          ex.printStackTrace();
          con.close();
        }
            
        
    }
    
    public HashMap read (int ref1, int ref2, int fc) {
             
        if (reader == null)     
            reader = new CESlaveReader(con);

        switch (fc) { //Read readable variables
                case 1: //IX 
                    return reader.readCoils(ref1,ref2-ref1);
                case 3: //IW 
                    return reader.readMultipleRegisters(ref1,ref2-ref1);
                default:
                    System.out.println("Trying to read unrecognized variable"); 
            } 

        return null;
    }
    
    public void write (int ref, int value, int fc) {
             
        if (writer == null)     
            writer = new CESlaveWriter(con);
        
       
                switch (fc){
                    case 5:
                        writer.writeCoil(ref,value);//0 or 1
                        break;
                    case 6:
                        writer.writeSingleRegister(ref,value);//decimal
                        break;
                    default:
                       System.out.println("Trying to write unrecognized variable"); 

                }
    }
    
    public void closeConnection(){
        try
        {
            con.close();
            
        }catch (Exception ex){
          ex.printStackTrace();
        }
        
    }
    
    public int readByName(String name, CEHouse house){
        
        CEHouseVariable v = house.findByName(name);
        if(v != null){
           HashMap values = read(v.modbusAddr,v.modbusAddr+1,v.readFunctionCall);
            return (Integer) values.get(v.modbusAddr);
        }
        return -1000; //err
    }
    
    public void writeByName(String name, int value, CEHouse house){
        
        CEHouseVariable v = house.findByName(name);
        value = v.reverseFormatValue(value);
        if(v != null)
            write(v.modbusAddr,value,v.writeFunctionCall);
    }
}
