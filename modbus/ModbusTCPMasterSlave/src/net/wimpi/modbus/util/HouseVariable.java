/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wimpi.modbus.util;

/**
 *
 * @author nur
 */
public class HouseVariable {
    
    private static final double maxValueInRegister = 65535;
    public int modbusAddr;
    public String name;
    public int value;
    public int readFunctionCall;
    public boolean updated;
    public String type;
    public int writeFunctionCall;
    public int minValue;
    public int maxValue;
    
    public String toString(){
        return "name: "+name+" address: "+modbusAddr+" value: "+value;
    }
    
    public int formatValue(int newValue){ //in database and clients
        
        if ("IW".equals(type)){
            return (int) Math.ceil(minValue + (newValue * (maxValue - minValue)/maxValueInRegister));
        }
        
        return newValue;   
    }
    
    public int reverseFormatValue(int newValue){ //in slave
        
        if ("IW".equals(type)){
            return (int) Math.floor((newValue - minValue) * maxValueInRegister / (maxValue - minValue));
        }
        
        return newValue;
    }
}
