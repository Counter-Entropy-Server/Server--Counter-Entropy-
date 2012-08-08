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
    public int modbusAddr;
    public String name;
    public int value;
    public int functionCall;
    public boolean updated;
    public boolean readable;
}
